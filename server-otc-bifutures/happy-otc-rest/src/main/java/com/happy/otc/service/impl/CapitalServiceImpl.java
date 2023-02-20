package com.happy.otc.service.impl;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.bitan.market.vo.OriginalExchangeInfoVO;
import com.bitan.message.enums.OtcMessageEnum;
import com.bitan.message.vo.SendOtcMessageRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.vo.BiFuturesOtc;
import com.happy.otc.contants.Contants;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dao.*;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.*;
import com.happy.otc.entity.Currency;
import com.happy.otc.enums.*;
import com.happy.otc.service.*;
import com.happy.otc.service.bifutures.UserAssetsService;
import com.happy.otc.service.remote.IMarketService;
import com.happy.otc.service.remote.IMessageService;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.util.MoneyUtil;
import com.happy.otc.util.OtcUtils;
import com.happy.otc.vo.CapitalInfoVO;
import com.happy.otc.vo.CapitalLogInfoVO;
import com.happy.otc.vo.FundVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CapitalServiceImpl implements ICapitalService {


    private static final Logger logger = LogManager.getLogger(CommodityServiceImpl.class);

    @Autowired
    CapitalMapper capitalMapper;
    @Autowired
    CommodityMapper commodityMapper;
    @Autowired
    CurrencyMapper currencyMapper;
    @Autowired
    CapitalDetailMapper capitalDetailMapper;
    @Autowired
    CapitalDetailCountMapper capitalDetailCountMapper;
    @Autowired
    CapitalLogMapper capitalLogMapper;
    @Autowired
    CapitalInOutMapper capitalInOutMapper;
    @Autowired
    ThreadPoolTaskExecutor executor;
    @Autowired
    IMarketService marketService;
    @Autowired
    ICommodityService commodityService;
    @Autowired
    IMessageService messageService;
    @Autowired
    IUserIdentityService userIdentityService;
    @Autowired
    IUserService userService;
    @Autowired
    ITransactionPairService transactionPairService;
    @Autowired
    UserAssetsService userAssetsService;

    @Override
    public PageInfo<CapitalInfoVO> getCapitalInfoVO(Long userId, Integer pageIndex, Integer pageSize) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId",userId);
        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<CapitalInfoVO> list = capitalMapper.selectByParam(params);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public CapitalInfoVO getCapitalInfoVO(CapitalInfoVO capitalInfoVO) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId",capitalInfoVO.getUserId());
        params.put("currencyId",capitalInfoVO.getCurrencyId());
        List<CapitalInfoVO> list = capitalMapper.selectByParam(params);
        return list.size() == 1 ? list.get(0):null;
    }


    /**
     * 划转处理
     * @param userId
     * @param money
     * @param currencyId
     * @param capitalLogTypeEnum
     * @return
     */
    @Transactional
    @Override
    public int capitalCut(Long userId, BigDecimal money, Long currencyId, CapitalLogTypeEnum capitalLogTypeEnum) throws ParserConfigurationException, SAXException, ParseException, IOException{

        CapitalInfoVO capitalInfoVO = getCapitalInfoByUserIdAndCurrencyId(userId, currencyId);

        BiFuturesOtc userAssets = userAssetsService.getAssetsByUserId(userId);

        //记录卖方资金变化
        CapitalLog capitalLog = new CapitalLog();

        //todo 需要增加合约账户余额的处理
        if (capitalLogTypeEnum == CapitalLogTypeEnum.CUT_OUT){
            if (capitalInfoVO == null || capitalInfoVO.getCapitalAvailable().compareTo(money) < 0){
                BizException.fail( MessageCode.NOT_ENOUGH_ERR, "资金数量过大，用户余额不足");
            }

            capitalInfoVO.setCapitalAvailable(capitalInfoVO.getCapitalAvailable().subtract(money));
            userAssetsService.updateAssetsByUserAssetsDto(userId,money,1);

        }else if (capitalLogTypeEnum == CapitalLogTypeEnum.CUT_IN){
            //todo 需要合约账户信息
            BigDecimal futuresAssets = userAssets.getPrincipal();
            if (futuresAssets.compareTo(money) < 0){
                BizException.fail( MessageCode.NOT_ENOUGH_ERR, "资金数量过大，合约账户余额不足");
            }
            userAssetsService.updateAssetsByUserAssetsDto(userId,money,2);
            capitalInfoVO.setCapitalAvailable(capitalInfoVO.getCapitalAvailable().add(money));
        }
        //资金划转
        capitalMapper.updateByPrimaryKeySelective(capitalInfoVO);

        capitalLog.setUserId(userId);
        capitalLog.setType(capitalLogTypeEnum.getValue().byteValue());
        capitalLog.setCurrencyId(currencyId);
        capitalLog.setFrozenNumber(BigDecimal.ZERO);
        capitalLog.setAvailableNumber(money);
        capitalLog.setComment("OTC账户" + capitalLogTypeEnum.getName()+ " : " + money);
        capitalLog.setOrderId(0L);
        capitalLog.setCreateTime(new Date());
        int flag = capitalLogMapper.insertSelective(capitalLog);

        return  flag;
    }


    /**
     * 发币处理
     * @param capitalDetailId   交易单ID
     */
    @Transactional
    @Override
    public void transferCapital(Long capitalDetailId){

        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalDetailId);
        if (capitalDetail == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "不存在此订单");
        }

        Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());

        //判断商户的准备或者购买的数量是否足够
        BigDecimal transactionVolume = capitalDetail.getTransactionVolume();

        if (transactionVolume.compareTo(BigDecimal.ZERO) <= 0) {
            BizException.fail(MessageCode.POSITIVE_MONEY_ERR,"交易金额需为正数");
        }
        if (commodity.getFrozenQuantity().compareTo(transactionVolume) < 0){
            BizException.fail(MessageCode.NOT_ENOUGH_FROZEN_GOODS,"商品的冻结数量不够");
        }
        commodity.setFrozenQuantity(commodity.getFrozenQuantity().subtract(
                transactionVolume));
        //判断商户的币种数量已卖完
        if (commodity.getCurrencyQuantity().compareTo(BigDecimal.ZERO) == 0 &&
                commodity.getFrozenQuantity().compareTo(BigDecimal.ZERO) == 0) {
            commodity.setStatus(1);
        }
        commodity.setUpdateTime(new Date());

/*        //计算商品的冻结手续费
        if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL.getValue())) {
            commodity.setFeeQuantity(commodity.getFeeQuantity().subtract(capitalDetail.getServiceFee()));
        }*/
        //修正商品的属性
       int res = commodityMapper.updateByPrimaryKeySelective(commodity);

        if (res == 0){
            BizException.fail(ApiResponseCode.METHOD_UN_IMPL, "");
        }

        //对于用户之间的资金进行转移
        CapitalInfoVO sellUserIdCapital = getCapitalInfoByUserIdAndCurrencyId(capitalDetail.getSellUserId(), commodity.getCurrencyId());
        CapitalInfoVO buyUserIdCapital = getCapitalInfoByUserIdAndCurrencyId(capitalDetail.getBuyUserId(), commodity.getCurrencyId());

        //卖家支付的金额
        BigDecimal sellMoney;
        if (capitalDetail.getCommodityType().equals(CommodityTypeEnum.SELL.getValue())) {
            //卖单,卖方需支付订单数量+手续费
            sellMoney = transactionVolume.add(capitalDetail.getServiceFee());
        } else {
            //买单，卖方只需支付订单数量
            sellMoney = transactionVolume;
        }

        //对于卖方用户的冻结金额进行操作
        if (sellUserIdCapital.getCapitalFrozen().compareTo(sellMoney) < 0) {
            BizException.fail(MessageCode.NOT_ENOUGH_FROZEN_MONEY, "冻结资金不够");
        }

        sellUserIdCapital.setCapitalFrozen(sellUserIdCapital.getCapitalFrozen().subtract(sellMoney));
        capitalMapper.updateByPrimaryKeySelective(sellUserIdCapital);

        //记录卖方资金变化
        CapitalLog capitalLog = new CapitalLog();
        capitalLog.setUserId(capitalDetail.getSellUserId());
        capitalLog.setType(CapitalLogTypeEnum.SELL.getValue().byteValue());
        capitalLog.setCurrencyId(commodity.getCurrencyId());
        capitalLog.setFrozenNumber(sellMoney.negate());
        capitalLog.setComment("交易放币，订单号:" + capitalDetailId);
        capitalLog.setOrderId(capitalDetailId);
        capitalLog.setCreateTime(new Date());
        capitalLogMapper.insertSelective(capitalLog);

        //买家收到的金额
        BigDecimal buyMoney;
        if (capitalDetail.getCommodityType().equals(CommodityTypeEnum.SELL.getValue())) {
            //卖单,买方收到订单数量
            buyMoney = transactionVolume;
        } else {
            //买单，买方收到订单数量-手续费
            buyMoney = transactionVolume.subtract(capitalDetail.getServiceFee());
        }

        //对于买方用户的金额进行操作
        buyUserIdCapital.setCapitalAvailable(buyUserIdCapital.getCapitalAvailable().add(buyMoney));
        capitalMapper.updateByPrimaryKeySelective(buyUserIdCapital);

        //记录买方资金变化
        capitalLog = new CapitalLog();
        capitalLog.setUserId(capitalDetail.getBuyUserId());
        capitalLog.setType(CapitalLogTypeEnum.BUY.getValue().byteValue());
        capitalLog.setCurrencyId(commodity.getCurrencyId());
        capitalLog.setAvailableNumber(buyMoney);
        capitalLog.setComment("交易得币，订单号:" + capitalDetailId);
        capitalLog.setOrderId(capitalDetailId);
        capitalLog.setCreateTime(new Date());
        capitalLogMapper.insertSelective(capitalLog);


        //对于用户交易成功次数的统计
        Map<String, Object> params = new HashMap<>();
        params.put("userId", capitalDetail.getSellUserId());
        CapitalDetailCount capitalDetailCount = capitalDetailCountMapper.selectByPrimaryKey(params);
        capitalDetailCount.setSuccessTradeCount(capitalDetailCount.getSuccessTradeCount()+1);
        capitalDetailCount.setTotalTradeCount(capitalDetailCount.getTotalTradeCount()+1);
        capitalDetailCount.setUpdateTime(new Date());
        capitalDetailCount.setCloseRate(Double.valueOf(100 * capitalDetailCount.getSuccessTradeCount()/
                        capitalDetailCount.getTotalTradeCount()));
        capitalDetailCount.setExchangeHour(OtcUtils.exchangeHour(capitalDetailCount.getSuccessTradeCount(),
                capitalDetail.getPayTime().getTime(),capitalDetailCount.getExchangeHour()));
        capitalDetailCountMapper.updateByPrimaryKeySelective(capitalDetailCount);
        params.put("userId", capitalDetail.getBuyUserId());
        capitalDetailCount = capitalDetailCountMapper.selectByPrimaryKey(params);
        capitalDetailCount.setSuccessTradeCount(capitalDetailCount.getSuccessTradeCount()+1);
        capitalDetailCount.setTotalTradeCount(capitalDetailCount.getTotalTradeCount()+1);
        capitalDetailCount.setUpdateTime(new Date());
        capitalDetailCount.setCloseRate(Double.valueOf(100 * capitalDetailCount.getSuccessTradeCount()/
                capitalDetailCount.getTotalTradeCount()));
        capitalDetailCount.setExchangeHour(OtcUtils.exchangeHour(capitalDetailCount.getSuccessTradeCount(),
                capitalDetail.getPayTime().getTime(),capitalDetailCount.getExchangeHour()));
        capitalDetailCountMapper.updateByPrimaryKeySelective(capitalDetailCount);

        //判断当前商品可以交易的数量是否不足，如果不足则进行下架
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //当前商品还可以交易的值
                BigDecimal nowQuantity = commodity.getFrozenQuantity().add(commodity.getCurrencyQuantity());
                BigDecimal minQuantity = null;
                try {
                    if (commodity.getTradeMethod() == 0){
                        minQuantity = commodity.getMinimumAmount().divide(commodity.getCurrencyPrice(), 8, BigDecimal.ROUND_HALF_UP);
                    }else {

                        KindEnum kindEnum = KindEnum.getInstance(commodity.getKind());
                        if (Objects.isNull(kindEnum)){
                            BizException.fail(ApiResponseCode.PARA_ERR," kind");
                        }
                        RelevantKindEnum relevantKindEnum = RelevantKindEnum.getInstance(commodity.getRelevantKind());
                        if (Objects.isNull(relevantKindEnum)){
                            BizException.fail(ApiResponseCode.PARA_ERR," relevantKind");
                        }

                        Double price = transactionPairService.getRealTimeOnePoint(kindEnum,relevantKindEnum);
                        BigDecimal b = BigDecimal.valueOf(price * 0.01 * commodity.getCurrencyPrice().doubleValue());
                        minQuantity = commodity.getMinimumAmount().divide(b, 8, BigDecimal.ROUND_HALF_UP);
                    }

                    if (nowQuantity.compareTo(minQuantity) < 0){
                        commodityService.delCommodity(commodity.getCommodityId());
                    }
                }catch (Exception e){
                    e.getStackTrace();
                }

                LanguageEnum languageEnum = LanguageEnum.CHINESE;
                //对于买卖双方进行短信通知
                UserDTO buyer = userService.getUserInfoByUserId(capitalDetail.getBuyUserId());
                UserDTO seller = userService.getUserInfoByUserId(capitalDetail.getSellUserId());
                if (buyer != null) {
                    UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(capitalDetail.getBuyUserId());

                    if (!Objects.isNull(userIdentity.getLanguageType())){
                        languageEnum = LanguageEnum.getInstance(Integer.valueOf(userIdentity.getLanguageType()));
                        if (Objects.isNull(languageEnum)) {
                            languageEnum = LanguageEnum.CHINESE;
                        }
                    }
                    SendOtcMessageRequest sendOtcMessageRequest = new SendOtcMessageRequest();
                    sendOtcMessageRequest.setAccount(buyer.getCountryCode()+ buyer.getMobile());
                    sendOtcMessageRequest.setKind(commodity.getKind());
                    sendOtcMessageRequest.setRelevantKind(commodity.getRelevantKind());
                    sendOtcMessageRequest.setCoins(capitalDetail.getTransactionVolume().setScale(6, BigDecimal.ROUND_DOWN).toString());
                    sendOtcMessageRequest.setPrice(capitalDetail.getTransactionPrice().toString());
                    sendOtcMessageRequest.setOrderNumber(capitalDetail.getOrderNumber());
                    sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.BUY_COINS_SUCCESS.getValue());
                    sendOtcMessageRequest.setUserName(seller.getUserName());
                    sendOtcMessageRequest.setAppType(languageEnum.getValue());
                    try {
                        messageService.sendOtcMessage(sendOtcMessageRequest);
                    } catch (Exception e) {
                        logger.error("下架商品出错，商品id= "+commodity.getCommodityId() + e.getStackTrace());

                    }
                }


                if (seller != null) {
                    UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(capitalDetail.getSellUserId());

                    if (!Objects.isNull(userIdentity.getLanguageType())){
                        languageEnum = LanguageEnum.getInstance(Integer.valueOf(userIdentity.getLanguageType()));
                        if (Objects.isNull(languageEnum)) {
                            languageEnum = LanguageEnum.CHINESE;
                        }
                    }
                    SendOtcMessageRequest sendOtcMessageRequest = new SendOtcMessageRequest();
                    sendOtcMessageRequest.setAccount(seller.getCountryCode() + seller.getMobile());
                    sendOtcMessageRequest.setKind(commodity.getKind());
                    sendOtcMessageRequest.setRelevantKind(commodity.getRelevantKind());
                    sendOtcMessageRequest.setCoins(capitalDetail.getTransactionVolume().setScale(6,BigDecimal.ROUND_DOWN).toString());
                    sendOtcMessageRequest.setPrice(capitalDetail.getTransactionPrice().toString());
                    sendOtcMessageRequest.setOrderNumber(capitalDetail.getOrderNumber());
                    sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.SELL_COINS_SUCCESS.getValue());
                    sendOtcMessageRequest.setUserName(buyer.getUserName());
                    sendOtcMessageRequest.setAppType(languageEnum.getValue());
                    try {
                        messageService.sendOtcMessage(sendOtcMessageRequest);
                    }catch (Exception e){

                    }

                }
            }
        });


    }

    @Override
    public List<CapitalInfoVO> getCapitalInfoVO(Map<String, Object> params) {

        List<CapitalInfoVO> list = capitalMapper.selectByParam(params);
        return list;
    }

    @Override
    public CapitalInfoVO getCapitalInfoByUserIdAndCurrencyId(Long userId, Long currencyId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("currencyId", currencyId);
        List<CapitalInfoVO> list = capitalMapper.selectByParam(params);
        return list.size() > 0 ? list.get(0):null;
    }

    @Transactional
    @Override
    public int updateCapitalInfo(CapitalInfoVO capitalInfoVO){

        return capitalMapper.updateByPrimaryKeySelective(capitalInfoVO);
    }

    @Transactional
    @Override
    public Boolean addCapitalInfo(Long userId){

        List<Currency> list = currencyMapper.selectByPrarm();
        Capital capital = new Capital();
        int result = 0;
        for (Currency currency :list){
            capital.setUserId(userId);
            capital.setCurrencyId(currency.getCurrencyId());
            result = capitalMapper.insertSelective(capital);
        }
        return result > 0;
    }

    @Override
    public PageInfo<CapitalLogInfoVO> getCapitalLogList(Long userId, Integer type, String time, Integer intTimeZone, Long kind,
                                                        Integer pageIndex, Integer pageSize) {
        List<Integer> types = new ArrayList<>();
        if (type == 0) {
            types.add(CapitalLogTypeEnum.BUY.getValue());
            types.add(CapitalLogTypeEnum.SELL.getValue());
            types.add(CapitalLogTypeEnum.CHARGE.getValue());
            types.add(CapitalLogTypeEnum.WITHDRAWALS.getValue());
            types.add(CapitalLogTypeEnum.CUT_IN.getValue());
            types.add(CapitalLogTypeEnum.CUT_OUT.getValue());
        }  else if (type == 9){
            types.add(CapitalLogTypeEnum.CUT_IN.getValue());
            types.add(CapitalLogTypeEnum.CUT_OUT.getValue());
        }
        else {
            types.add(type);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("types", types);
        //获取月初月末
        if (StringUtils.isNotBlank(time)) {
            String timeZoneID = "GMT" + (intTimeZone > 0 ? "+":"") + String.valueOf(intTimeZone);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
            df.setTimeZone(timeZone);

            Date start = null;
            try {
                start = df.parse(time);
            } catch (ParseException e) {
                BizException.fail(ApiResponseCode.PARA_ERR, null);
            }
            Calendar calendar = Calendar.getInstance(timeZone);
            calendar.setTime(start);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.MILLISECOND, -1);
            Date end = calendar.getTime();

            params.put("start", start);
            params.put("end", end);
        }
        if (kind != null && kind > 0) {
            params.put("currencyId", kind);
        }

        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);

        List<CapitalLogInfoVO> capitalLogs = capitalLogMapper.selectByParam(params);

        //获取订单额外信息
        for (CapitalLogInfoVO capitalLog : capitalLogs) {
            CapitalLogTypeEnum typeEnum = CapitalLogTypeEnum.getInstance(capitalLog.getType().intValue());
            CapitalDetail capitalDetail;
            UserDTO userDTO;
            switch (typeEnum) {
                case BUY:       //买入
                    capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalLog.getOrderId());
                    if (capitalDetail != null) {
                        capitalLog.setPrice(capitalDetail.getTransactionPrice());
                        capitalLog.setRelevantKind(capitalDetail.getRelevantKind());
                        userDTO = userService.getUserInfoByUserId(capitalDetail.getSellUserId());
                        capitalLog.setTargetUsername(userDTO.getUserName());
                        //手续费
                        if (capitalDetail.getCommodityType().equals(CommodityTypeEnum.BUY.getValue())) {
                            capitalLog.setFee(capitalDetail.getServiceFee());
                        }
                    }
                    break;
                case SELL:      //卖出
                    capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalLog.getOrderId());
                    if (capitalDetail != null) {
                        capitalLog.setPrice(capitalDetail.getTransactionPrice());
                        capitalLog.setRelevantKind(capitalDetail.getRelevantKind());
                        userDTO = userService.getUserInfoByUserId(capitalDetail.getBuyUserId());
                        capitalLog.setTargetUsername(userDTO.getUserName());
                        //手续费
                        if (capitalDetail.getCommodityType().equals(CommodityTypeEnum.SELL.getValue())) {
                            capitalLog.setFee(capitalDetail.getServiceFee());
                        }
                    }
                    break;
                case CHARGE:    //充币
                    break;
                case WITHDRAWALS:   //提币
                    CapitalInOut capitalInOut = capitalInOutMapper.selectByPrimaryKey(capitalLog.getOrderId());
                    if (capitalInOut != null) {
                        capitalLog.setAddress(capitalInOut.getCurrencyAddress());
                        capitalLog.setFee(capitalInOut.getServiceFee());
                    }
                    break;
            }
        }

        PageInfo<CapitalLogInfoVO> pageInfo = new PageInfo(capitalLogs);

        return pageInfo;
    }


    /**
     * 获取用户的资金详情
     * @param userId
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ParseException
     * @throws IOException
     */
    public FundVO buildFundVO(Long userId) throws ParserConfigurationException, SAXException, ParseException, IOException {
        PageInfo<CapitalInfoVO>  pageInfo =  getCapitalInfoVO(userId,1,10);
        List<String> kind = new ArrayList<>(  );
        FundVO fundVO = new FundVO();
        Map<String,BigDecimal> otcMoneyMap = new HashMap<>(  );
        BigDecimal otcAsset = BigDecimal.ZERO;
        BigDecimal otcAssetCNY = BigDecimal.ZERO;
        BigDecimal otcAssetUSD = BigDecimal.ZERO;
        for (CapitalInfoVO capitalInfoVO:pageInfo.getList()) {
            if (capitalInfoVO.getCapitalAvailable().compareTo( BigDecimal.ZERO ) > 0
                    || capitalInfoVO.getCapitalFrozen().compareTo( BigDecimal.ZERO ) > 0){
                kind.add(capitalInfoVO.getCurrencySimpleName()+"/USDT");
                otcMoneyMap.put(capitalInfoVO.getCurrencySimpleName()+"/USDT", MoneyUtil.moneyAdd(capitalInfoVO.getCapitalFrozen(),capitalInfoVO.getCapitalAvailable()));
            }
        }

        //todo 获取合约的账户余额及权益价值
        BiFuturesOtc userAssets = userAssetsService.getAssetsByUserId(userId);

        BigDecimal heyue = userAssets != null ? userAssets.getBalance() : BigDecimal.ZERO;
        fundVO.setAvailableBond(userAssets != null ? userAssets.getPrincipal() : BigDecimal.ZERO );
        fundVO.setUserdBond( userAssets != null ? userAssets.getUsedPrincipal() : BigDecimal.ZERO);
        fundVO.setProfitToday(userAssets != null ? userAssets.getTodayCellPol() : BigDecimal.ZERO);
        fundVO.setProfit(userAssets != null ? userAssets.getPositionPol() : BigDecimal.ZERO);
        if (heyue.compareTo( BigDecimal.ZERO ) > 0){
            if (!kind.contains(KindEnum.USDT.getValue()+"/USDT")){
                kind.add( KindEnum.USDT.getValue()+"/USDT");
            }
        }

        if (!kind.isEmpty()){
            List<OriginalExchangeInfoVO>  list = transactionPairService.getExchangeInfoList(kind);
            for (OriginalExchangeInfoVO vo:list) {
                BigDecimal tempMoney =otcMoneyMap.get(vo.getKind());
                if (vo.getKind().equals(KindEnum.USDT.getValue()+"/USDT")){
                    fundVO.setFuturesAssets(heyue.setScale(6, RoundingMode.FLOOR));
                    fundVO.setFuturesAssetsCNY(MoneyUtil.moneyMul(heyue,new BigDecimal(vo.getLegalTendeCNY())).setScale(2, RoundingMode.FLOOR));
                    fundVO.setFuturesAssetsUSD(MoneyUtil.moneyMul(heyue,new BigDecimal(vo.getLegalTendeUSD())).setScale(2, RoundingMode.FLOOR));
                }

                if (tempMoney != null){
                    if (vo.getKind().equals(KindEnum.USDT.getValue()+"/USDT")){
                        otcAsset = MoneyUtil.moneyAdd(tempMoney,otcAsset).setScale(6, RoundingMode.FLOOR);
                    }else {
                        otcAsset = MoneyUtil.moneyAdd(MoneyUtil.moneyMul(tempMoney,new BigDecimal(vo.getPrice())),otcAsset).setScale(6, RoundingMode.FLOOR);
                    }

                    otcAssetCNY = MoneyUtil.moneyAdd(MoneyUtil.moneyMul(tempMoney,new BigDecimal(vo.getLegalTendeCNY())),otcAssetCNY).setScale(2, RoundingMode.FLOOR);
                    otcAssetUSD = MoneyUtil.moneyAdd(MoneyUtil.moneyMul(tempMoney,new BigDecimal(vo.getLegalTendeUSD())),otcAssetUSD).setScale(2, RoundingMode.FLOOR);
                }
            }
        }

        fundVO.setOtcAssets(otcAsset);
        fundVO.setOtcAssetsCNY(otcAssetCNY);
        fundVO.setOtcAssetsUSD(otcAssetUSD);

        fundVO.setAssets(MoneyUtil.moneyAdd(fundVO.getOtcAssets(), fundVO.getFuturesAssets()).setScale(6, RoundingMode.FLOOR));
        fundVO.setAssetsCNY(MoneyUtil.moneyAdd(fundVO.getOtcAssetsCNY(), fundVO.getFuturesAssetsCNY()).setScale(2, RoundingMode.FLOOR));
        fundVO.setAssetsUSD(MoneyUtil.moneyAdd(fundVO.getOtcAssetsUSD(), fundVO.getFuturesAssetsUSD()).setScale(2, RoundingMode.FLOOR));
        fundVO.setList(pageInfo.getList());
        return  fundVO;
    }
}
