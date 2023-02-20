package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.CommonUtils;
import com.bitan.common.utils.RedisUtil;
import com.bitan.message.enums.OtcMessageEnum;
import com.bitan.message.vo.SendOtcMessageRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.protobuf.InvalidProtocolBufferException;
import com.happy.otc.contants.Contants;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dao.CapitalDetailMapper;
import com.happy.otc.dao.CapitalLogMapper;
import com.happy.otc.dao.CommodityMapper;
import com.happy.otc.dao.UserBlackListMapper;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.*;
import com.happy.otc.entity.Currency;
import com.happy.otc.enums.*;
import com.happy.otc.proto.CommodityExtendVO;
import com.happy.otc.service.*;
import com.happy.otc.service.remote.IMarketService;
import com.happy.otc.service.remote.IMessageService;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.util.OtcUtils;
import com.happy.otc.vo.CapitalInfoVO;
import com.happy.otc.vo.CommodityInfoVO;
import com.happy.otc.vo.ExchangeInfoVO;
import com.happy.otc.vo.UserIdentityVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CommodityServiceImpl implements ICommodityService {

    private static final Logger logger = LogManager.getLogger(CommodityServiceImpl.class);

    @Autowired
    CommodityMapper commodityMapper;
    @Autowired
    ICurrencyService currencyService;
    @Autowired
    ICapitalService capitalService;
    @Autowired
    CapitalDetailMapper capitalDetailMapper;
    @Autowired
    IUserIdentityService userIdentityService;
    @Autowired
    IOauthService oauthService;
    @Autowired
    IUserAccountService userAccountService;
    @Autowired
    IEasemobService easemobService;
    @Autowired
    ICapitalDetailCountService capitalDetailCountService;
    @Autowired
    CapitalLogMapper capitalLogMapper;
    @Autowired
    UserBlackListMapper userBlackListMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    IFeeRuleService feeRuleService;
    @Autowired
    IMarketService marketService;
    @Autowired
    IUserService userService;
    @Autowired
    ThreadPoolTaskExecutor executor;
    @Autowired
    IMessageService messageService;
    @Autowired
    ITransactionPairService transactionPairService;

    @Override
    public PageInfo<CommodityInfoVO> getCommodityList(Integer commodityType, String kind, String relevantKind, Long userId, Integer pageIndex, Integer pageSize){

        Map<String, Object> params = new HashMap<>();
        params.put("commodityType",commodityType);
        params.put("relevantKind",relevantKind);
        params.put("kind",kind);
        params.put("status",0);
//        params.put("selfUserId",userId);

        //获取被屏蔽人列表
        List<Long> blackList = userBlackListMapper.selectAllTargetIdByUserId(userId);
        params.put("ignoreUserIds", blackList);
        //数量大于0
        params.put("hasQuantity", true);

        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<CommodityInfoVO> commodityInfoVOList = commodityMapper.selectByParam(params);

        List<Long> userIdList = new ArrayList<>();
        for (CommodityInfoVO commodityInfoVO:commodityInfoVOList) {
            if (!userIdList.contains(commodityInfoVO.getUserId())){
                userIdList.add(commodityInfoVO.getUserId());
            }
        }
//        Result<Map<Long, UserInfoVO>> userInfoVOMap = oauthService.getUserInfoByUserIdList(userIdList);
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);
        for (CommodityInfoVO commodityInfoVO:commodityInfoVOList) {
//            commodityInfoVO.setNickName(userInfoVOMap.getData().get(commodityInfoVO.getUserId()).getUserName());
            commodityInfoVO.setNickName(userDTOMap.get(commodityInfoVO.getUserId()).getUserName());
        }

        PageInfo pageInfo = new PageInfo(commodityInfoVOList);
        return  pageInfo;
    }


    /**
     * 针对有着浮动赔率的特殊排序
     * @param params
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<CommodityInfoVO> getCommodityListByParam(Map<String, Object> params, Integer pageIndex, Integer pageSize){

        //限制未下架商品
        params.put("status",0);
        //获取被屏蔽人列表
        if (params.get("selfUserId") != null){
            List<Long> blackList = userBlackListMapper.selectAllTargetIdByUserId(Long.valueOf(params.get("selfUserId").toString()));
            params.put("ignoreUserIds", blackList);
        }

        //数量大于0
        params.put("hasQuantity", true);

        PageHelper.startPage(1, pageIndex*pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        //固定的价格列表
        params.put("tradeMethod",0);
        List<CommodityInfoVO> fixeList = commodityMapper.selectByParam(params);
        PageInfo fixePageInfo = new PageInfo(fixeList);

        PageHelper.startPage(1, pageIndex*pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        //浮动的价格列表
        params.put("tradeMethod",1);
        List<CommodityInfoVO> flottanteList = commodityMapper.selectByParam(params);
        PageInfo flottantePageInfo = new PageInfo(flottanteList);

        //判断是否取到的数据为空
        PageInfo pageInfo = new PageInfo();
        Long totals = fixePageInfo.getTotal()+flottantePageInfo.getTotal();
        if (totals.compareTo((pageIndex -1) * pageSize * 1L) < 0){
            pageInfo.setList(new ArrayList());
            return pageInfo;
        }

        List<Long> userIdList = new ArrayList<>();
        for (CommodityInfoVO commodityInfoVO:fixeList) {
            if (!userIdList.contains(commodityInfoVO.getUserId())){
                userIdList.add(commodityInfoVO.getUserId());
            }
        }


        KindEnum kindEnum = KindEnum.getInstance(params.get("kind").toString());
        if (Objects.isNull(kindEnum)){
            BizException.fail(ApiResponseCode.PARA_ERR," kind");
        }
        RelevantKindEnum relevantKindEnum = RelevantKindEnum.getInstance(params.get("relevantKind").toString());
        if (Objects.isNull(relevantKindEnum)){
            BizException.fail(ApiResponseCode.PARA_ERR," relevantKind");
        }

        Double price = transactionPairService.getRealTimeOnePoint(kindEnum,relevantKindEnum);

        //对于浮动的价格进行计算
        for (CommodityInfoVO commodityInfoVO:flottanteList) {
            if (!userIdList.contains(commodityInfoVO.getUserId())){
                userIdList.add(commodityInfoVO.getUserId());
            }
            BigDecimal b = BigDecimal.valueOf(price*0.01*(100+commodityInfoVO.getCurrencyPrice().doubleValue()));
            commodityInfoVO.setCurrencyPrice(b.setScale(2,BigDecimal.ROUND_DOWN));
        }


        fixeList.addAll(flottanteList);

        CommodityTypeEnum commodityTypeEnum = CommodityTypeEnum.getInstance(Integer.valueOf(params.get("commodityType").toString()));

        if (CommodityTypeEnum.SELL == commodityTypeEnum){
            //商户售卖时，从小到大排序
            Collections.sort(fixeList, new Comparator<CommodityInfoVO>(){
                @Override
                public int compare(CommodityInfoVO v1, CommodityInfoVO v2) {
                    return v1.getCurrencyPrice().compareTo(v2.getCurrencyPrice());
                }
            });

        }else if ( CommodityTypeEnum.BUY == commodityTypeEnum){
            //商户收购时，从大到小排序
            Collections.sort(fixeList, new Comparator<CommodityInfoVO>(){
                @Override
                public int compare(CommodityInfoVO v1, CommodityInfoVO v2) {
                    return v2.getCurrencyPrice().compareTo(v1.getCurrencyPrice());
                }
            });
        }

//        fixeList = fixeList.subList((pageIndex - 1) * pageSize, fixeList.size());
//       Result<Map<Long, UserInfoVO>> userInfoVOMap = oauthService.getUserInfoByUserIdList(userIdList);
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);
        for (CommodityInfoVO commodityInfoVO : fixeList) {
//            commodityInfoVO.setNickName(userInfoVOMap.getData().get(commodityInfoVO.getUserId()).getUserName());
            UserDTO userDTO = userDTOMap.get(commodityInfoVO.getUserId());
            if (userDTO != null) {
                commodityInfoVO.setNickName(userDTO.getUserName());
            }
        }

        pageInfo.setPageSize(pageSize);
        pageInfo.setPageNum(pageIndex);
        if (pageSize.compareTo(fixeList.size()) > 0){
            pageInfo.setList(fixeList);
            pageInfo.setTotal(fixeList.size());
        }else{
            pageInfo.setList(fixeList.subList( 0 ,pageSize));
            pageInfo.setTotal(pageSize);
        }

        return  pageInfo;
    }


    public static void main(String[] args) {
            List<String> list = new ArrayList<>();
        System.out.println(list.subList(2,4));
    }

    @Transactional
    @Override
    public Long addCommodity(CommodityExtendVO.CommodityVOParam commodityVO, Long userId){

        //对于用户身份信息
        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);
        UserOtcTypeEnum userOtcTypeEnum = UserOtcTypeEnum.getInstance((int)userIdentity.getUserOtcType());
        if(userOtcTypeEnum != UserOtcTypeEnum.SELLER ){
            BizException.fail(MessageCode.NO_AUTHORITY,"该用户不是商户，无权发布商品");
        }

        //判断资金密码是否正确
        userIdentityService.checkCapitalCipher(commodityVO.getCapitalCipher(), userIdentity.getCapitalCipher(), userId);

        //支付方式校验
       if(commodityVO.getPayMethodListList().size() == 0){
           BizException.fail(ApiResponseCode.PARA_ERR,"支付方式必须选择");
       }

        //根据币种的简称获取对象
        Currency currency = currencyService.getCurrency(commodityVO.getKind());
        if (Objects.isNull(currency)){
            BizException.fail(ApiResponseCode.PARA_ERR,"不支持该币种");
        }

        if (commodityVO.getCurrencyAmount() <= 0) {
            BizException.fail(MessageCode.POSITIVE_QUANTITY_ERR,"交易数量需为正数");
        }

        if (commodityVO.getTradeMethod().getNumber() == CommodityExtendVO.TradeMethod.TRADE_MARKET_VALUE && !OtcUtils.floatBandCheck(commodityVO.getTradePrice()) ) {
            BizException.fail(MessageCode.PRICE_AREA_ERR,"浮动价格区间不合理");
        }else if (commodityVO.getTradeMethod().getNumber() == CommodityExtendVO.TradeMethod.TRADE_CUSTOME_VALUE && commodityVO.getTradePrice() <= 0 ){
            BizException.fail(MessageCode.POSITIVE_PRICE_ERR,"交易价格需为正数");
        }

        Commodity commodity = new Commodity();
        commodity.setCommodityType(commodityVO.getCommodityTypeValue());
        commodity.setCountry(commodityVO.getCountry());
        commodity.setKind(commodityVO.getKind());
        commodity.setTradeMethod(commodityVO.getTradeMethodValue());
        commodity.setRelevantKind(commodityVO.getRelevantKind());
        commodity.setCurrencyPrice(new BigDecimal(Double.toString(commodityVO.getTradePrice())).setScale(2,BigDecimal.ROUND_DOWN));
        commodity.setCurrencyQuantity(new BigDecimal(Double.toString(commodityVO.getCurrencyAmount())).setScale(6,BigDecimal.ROUND_DOWN));
        commodity.setMinimumAmount(new BigDecimal(Double.toString(commodityVO.getMinimumAmount())).setScale(2,BigDecimal.ROUND_DOWN));
        commodity.setMaximumAmount(new BigDecimal(Double.toString(commodityVO.getMaximumAmount())).setScale(2,BigDecimal.ROUND_DOWN));
        commodity.setTradeTimeType(commodityVO.getTradeTimeValue());
        commodity.setPayMethodList(commodityVO.getPayMethodListList().toString());
        List<Integer> list = new ArrayList<>();
        for (int i = 0;i < commodityVO.getPayMethodListList().size(); i++){
            Long userAccountId = Long.valueOf(commodityVO.getPayMethodListList().get(i).toString());
            UserAccount userAccount = userAccountService.getUserAccount(userAccountId);
            if (!Objects.isNull(userAccount)){
                if(userId.compareTo(userAccount.getUserId()) != 0 || userAccount.getPayStatus() == 0){
                    BizException.fail(MessageCode.PAYMENT_ERR,"非法的支付方式");
                }
                list.add(userAccount.getPayType());
            }else {
                BizException.fail(ApiResponseCode.DATA_ERR,"账户id错误");
            }
        }
        commodity.setPayMethodTypeList(list.toString());
        commodity.setTradeCurb(commodityVO.getTradeCurb().toByteString().toStringUtf8());
        commodity.setLeaveMessage(commodityVO.getLeaveMessage());
        commodity.setUserId(userId);
        commodity.setVersionNumber(0);
        commodity.setCurrencyId(currency.getCurrencyId());
        commodity.setFeeQuantity(BigDecimal.ZERO);
        BigDecimal totalMoney = commodity.getCurrencyQuantity();
        if (commodityVO.getCommodityTypeValue() == CommodityTypeEnum.SELL.getValue()){

            //todo 判断用户最小交易额
//            if (new BigDecimal( 100 ).compareTo(commodity.getMaximumAmount()) < 0){
//                BizException.fail(MessageCode.MIN_TRANSACTION_MONEY_ERR,"用户最小交易额设置不对");
//            }

            //对于用户资金信息
            CapitalInfoVO capitalInfoVO = capitalService.getCapitalInfoByUserIdAndCurrencyId(userId, currency.getCurrencyId());

            //计算手续费
            BigDecimal fee = feeRuleService.calculateFee(FeeRuleTypeEnum.SELL.getValue(), commodity.getCurrencyId(), commodity.getCurrencyQuantity());

            totalMoney = totalMoney.add(fee);

            BigDecimal maxSellMoney = feeRuleService.calculateMaxSellMoney(FeeRuleTypeEnum.SELL.getValue(),commodity.getCurrencyId(), capitalInfoVO.getCapitalAvailable());
            //todo 判断用户本次发布商品需要币的数量不大于用户的可用资金及最大可售币数量
            if (capitalInfoVO.getCapitalAvailable().compareTo(totalMoney) < 0 || maxSellMoney.compareTo(commodity.getCurrencyQuantity()) < 0){
                BizException.fail(MessageCode.MAX_ENOUGH_ERR,maxSellMoney.toString());
            }

            //用户的资金进行冻结操作
            capitalInfoVO.setCapitalFrozen(capitalInfoVO.getCapitalFrozen().add(totalMoney));
            capitalInfoVO.setCapitalAvailable(capitalInfoVO.getCapitalAvailable().subtract(totalMoney));
            capitalService.updateCapitalInfo(capitalInfoVO);

            commodity.setFeeQuantity(fee);
        }
        commodity.setCreateTime(new Date());
        commodity.setUpdateTime(new Date());
        commodityMapper.insertSelective(commodity);
        Long commodityId = commodity.getCommodityId();

        //记录资金变化
        if (commodityVO.getCommodityTypeValue() == CommodityTypeEnum.SELL.getValue()) {
            CapitalLog capitalLog = new CapitalLog();
            capitalLog.setUserId(userId);
            capitalLog.setType(CapitalLogTypeEnum.FROZEN.getValue().byteValue());
            capitalLog.setCurrencyId(currency.getCurrencyId());
            capitalLog.setAvailableNumber(totalMoney.negate());
            capitalLog.setFrozenNumber(totalMoney);
            capitalLog.setComment("发布卖出商品:" + commodityId);
            capitalLog.setOrderId(commodityId);
            capitalLog.setCreateTime(new Date());
            capitalLogMapper.insertSelective(capitalLog);
        }
        return commodityId;
    }

    /**
     * 下单
     * @param commodityId           商品编号
     * @param transactionVolume     购入交易量(用户购买的货币数量)
     * @param transactionAmount     购入金额(用户消费的金额)
     * @param userId                用户编号
     * @param capitalCipher         资金密码
     * @return
     */
    @Transactional
    @Override
    public Long buyCommodity(Long commodityId, BigDecimal transactionVolume, BigDecimal transactionAmount, Long userId,String capitalCipher) {

        //统计取消次数
        String key = Contants.REDIS_USER_TRADE_CANCLE + userId;
        String count = redisUtil.getStringValue(key);
        if (StringUtils.isNoneEmpty(count) ){
            if (count.compareTo("3") >= 0){
                BizException.fail(MessageCode.CANCEL_ORDER_OVER, "取消交易次数过多，交易权限24小时内受限");
            }
        }

        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);

        if ( commodity.getStatus() == 1){
            BizException.fail(MessageCode.COMMODITY_OFFLINE, "商品已不存在");
        }

        if ( userId.compareTo(commodity.getUserId()) == 0){
            BizException.fail(MessageCode.BUY_SELF_ERR, "不能购买自身发布的商品");
        }

        //获取价格
        BigDecimal price = null;
        if (commodity.getTradeMethod().equals(0)) {
            //固定价格
            price = commodity.getCurrencyPrice();
        } else {
            //浮动价格
            ExchangeInfoVO exchangeInfo = transactionPairService.getExchangeInfoVO(commodity.getKind());

            Double marketPrice = 0d;
            if (commodity.getRelevantKind().equals(RelevantKindEnum.CNY.getValue())) {
                marketPrice = exchangeInfo.getPriceCNY();
            } else if (commodity.getRelevantKind().equals(RelevantKindEnum.USD.getValue())) {
                marketPrice = exchangeInfo.getPriceUSD();
            } else if (commodity.getRelevantKind().equals(RelevantKindEnum.CAD.getValue())) {
                marketPrice = exchangeInfo.getPriceCAD();
            }

            price = BigDecimal.valueOf(marketPrice * 0.01 * (100 + commodity.getCurrencyPrice().doubleValue())).setScale(8, BigDecimal.ROUND_DOWN);
        }

/*        //计算交易数量
        BigDecimal tradeQuantity = transactionAmount.divide(price, 8, BigDecimal.ROUND_HALF_UP);
        //交易数量变动超过0.5%，报错重新下单
        if (tradeQuantity.divide(transactionVolume, 8, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.ONE).abs().compareTo(BigDecimal.valueOf(0.005)) > 0) {
            BizException.fail(MessageCode.COMMODITY_STATUS_ERR, "商品价格已变更，请重新刷新下单");
        }*/

        if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0 || transactionVolume.compareTo(BigDecimal.ZERO) <= 0) {
            BizException.fail(MessageCode.POSITIVE_QUANTITY_ERR, "交易数量需为正数");
        }

        if ( transactionVolume.compareTo(commodity.getCurrencyQuantity()) > 0){
            BizException.fail(MessageCode.NOT_ENOUGH_GOODS, "商品余额不足");
        }

        //判断是否彼此被屏蔽
        List<Long> blackList = userBlackListMapper.selectAllTargetIdByUserId(userId);
        if (blackList.contains(commodity.getUserId())) {
            //为了提示友好，报商品下架错误
            BizException.fail(MessageCode.COMMODITY_OFFLINE, "此商品已下架");
        }

        CommodityTypeEnum commodityTypeEnum = CommodityTypeEnum.getInstance(commodity.getCommodityType());
        if (commodityTypeEnum == CommodityTypeEnum.BUY){
            //判断卖家资金是否足够
            CapitalInfoVO capitalInfoVO = capitalService.getCapitalInfoByUserIdAndCurrencyId(userId, commodity.getCurrencyId());
            if (capitalInfoVO.getCapitalAvailable().compareTo(transactionVolume) < 0){
                BizException.fail(MessageCode.NOT_ENOUGH_ERR,"用户当前币种数量不够");
            }
            //用户的资金进行冻结操作
            capitalInfoVO.setCapitalFrozen(capitalInfoVO.getCapitalFrozen().add(transactionVolume));
            capitalInfoVO.setCapitalAvailable(capitalInfoVO.getCapitalAvailable().subtract(transactionVolume));
            capitalService.updateCapitalInfo(capitalInfoVO);

            //对于用户身份信息
            UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);
            BizException.isNull(capitalCipher, "没有输入资金密码");
            //判断资金密码是否正确
            userIdentityService.checkCapitalCipher(capitalCipher, userIdentity.getCapitalCipher(), userId);
        }

        //交易信息
        CapitalDetail capitalDetail = new CapitalDetail();
        capitalDetail.setTransactionAmount(transactionAmount);
        capitalDetail.setTransactionVolume(transactionVolume);
        capitalDetail.setTransactionPrice(transactionAmount.divide(transactionVolume, 2, BigDecimal.ROUND_HALF_UP));
        capitalDetail.setStatus(OrderStateEnum.UNPAID.getValue());
        capitalDetail.setOrderNumber(CommonUtils.getRandomFileId().toString());
        capitalDetail.setReferenceNumber(CommonUtils.createRandomPWD(6));
        capitalDetail.setCommodityId(commodityId);
        capitalDetail.setCommodityType(commodity.getCommodityType());
        capitalDetail.setCurrencyId(commodity.getCurrencyId());
        capitalDetail.setRelevantKind(commodity.getRelevantKind());
        capitalDetail.setCrateTime(new Date());
        capitalDetail.setUpdateTime(new Date());
        capitalDetail.setPayTime(new Date());
        //手续费
        BigDecimal fee = BigDecimal.ZERO;
        switch (commodityTypeEnum) {
            case SELL:
                capitalDetail.setBuyUserId(userId);
                capitalDetail.setSellUserId(commodity.getUserId());
                if(commodity.getCurrencyQuantity().compareTo(transactionVolume) == 0){
                    //买完所有剩余的量
                    fee = commodity.getFeeQuantity();
                }else {
                    //买了一部分
                    fee = feeRuleService.calculateFee(FeeRuleTypeEnum.SELL.getValue(), commodity.getCurrencyId(), transactionVolume);
                }
                capitalDetail.setServiceFee(fee);
                break;
            case BUY:
                capitalDetail.setBuyUserId(commodity.getUserId());
                capitalDetail.setSellUserId(userId);
                fee = feeRuleService.calculateFee(FeeRuleTypeEnum.BUY.getValue(), commodity.getCurrencyId(), transactionVolume);
                capitalDetail.setServiceFee(fee);
                break;
        }

        //创建环信群组
        String groupName = "trade_" + capitalDetail.getOrderNumber();
        String groupId = easemobService.createChatGroups(commodity.getUserId(), userId, groupName, commodity.getLeaveMessage());
        capitalDetail.setEasemobGroupId(groupId);
        //初始环信提示订单状态
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put( "code",  MessageCode.ORDER_UNPAID);
        easemobService.sendOrderMessage(commodity.getUserId(), groupId, JSONObject.toJSONString(jsonObject2),commodity.getKind());
        //创建交易
        capitalDetailMapper.insertSelective(capitalDetail);

        //商品数量冻结
        BigDecimal commodityFee = BigDecimal.ZERO;
        if (commodityTypeEnum.equals(CommodityTypeEnum.SELL)) {
            commodityFee = fee.negate();
        }
        int res = commodityMapper.updateQuantity(commodityId, transactionVolume.negate(), transactionVolume, commodityFee ,commodity.getVersion());

        if (res == 0){
            BizException.fail(ApiResponseCode.METHOD_UN_IMPL, null);
        }

        //记录资金变化
        if (commodityTypeEnum == CommodityTypeEnum.BUY){
            CapitalLog capitalLog = new CapitalLog();
            capitalLog.setUserId(userId);
            capitalLog.setType(CapitalLogTypeEnum.FROZEN.getValue().byteValue());
            capitalLog.setCurrencyId(commodity.getCurrencyId());
            capitalLog.setAvailableNumber(transactionVolume.negate());
            capitalLog.setFrozenNumber(transactionVolume);
            capitalLog.setComment("卖商品,订单号:" + capitalDetail.getCapitalDetailId());
            capitalLog.setOrderId(capitalDetail.getCapitalDetailId());
            capitalLog.setCreateTime(new Date());
            capitalLogMapper.insertSelective(capitalLog);
        }

        //判断当前商品可以交易的数量是否不足，如果不足则进行下架
        executor.execute(new Runnable() {
            @Override
            public void run() {
                LanguageEnum languageEnum = LanguageEnum.CHINESE;
                List<Long> userIdList = new ArrayList<>();
                userIdList.add(capitalDetail.getBuyUserId());
                userIdList.add(capitalDetail.getSellUserId());
                Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);
                //对于买卖双方进行短信通知
                UserDTO buyer = userService.getUserInfoByUserId(capitalDetail.getBuyUserId());
                UserDTO seller = userService.getUserInfoByUserId(capitalDetail.getSellUserId());
                if (buyer != null && seller != null) {

                    if (commodityTypeEnum.equals(CommodityTypeEnum.SELL)){
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
                        sendOtcMessageRequest.setCoins(capitalDetail.getTransactionVolume().setScale(6, BigDecimal.ROUND_DOWN).toString());
                        sendOtcMessageRequest.setPrice(capitalDetail.getTransactionPrice().toString());
                        sendOtcMessageRequest.setOrderNumber(capitalDetail.getOrderNumber());
                        sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.ORDER_GENERATED_BUY_WARN.getValue());
                        sendOtcMessageRequest.setUserName(buyer.getUserName());
                        sendOtcMessageRequest.setAppType(languageEnum.getValue());
                        try {
                            messageService.sendOtcMessage(sendOtcMessageRequest);
                        } catch (Exception e) {
                            logger.error("商品买卖短信通知出错，订单id= "+capitalDetail.getCapitalDetailId());
                            e.printStackTrace();

                        }
                    }else if (commodityTypeEnum.equals(CommodityTypeEnum.BUY)){
                        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(capitalDetail.getBuyUserId());

                        if (!Objects.isNull(userIdentity.getLanguageType())){
                            languageEnum = LanguageEnum.getInstance(Integer.valueOf(userIdentity.getLanguageType()));
                            if (Objects.isNull(languageEnum)) {
                                languageEnum = LanguageEnum.CHINESE;
                            }
                        }
                        SendOtcMessageRequest sendOtcMessageRequest = new SendOtcMessageRequest();
                        sendOtcMessageRequest.setAccount(buyer.getCountryCode() + buyer.getMobile());
                        sendOtcMessageRequest.setKind(commodity.getKind());
                        sendOtcMessageRequest.setRelevantKind(commodity.getRelevantKind());
                        sendOtcMessageRequest.setCoins(capitalDetail.getTransactionVolume().setScale(6, BigDecimal.ROUND_DOWN).toString());
                        sendOtcMessageRequest.setPrice(capitalDetail.getTransactionPrice().toString());
                        sendOtcMessageRequest.setOrderNumber(capitalDetail.getOrderNumber());
                        sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.ORDER_GENERATED_SELL_WARN.getValue());
                        sendOtcMessageRequest.setUserName(seller.getUserName());
                        sendOtcMessageRequest.setAppType(languageEnum.getValue());
                        try {
                            messageService.sendOtcMessage(sendOtcMessageRequest);
                        } catch (Exception e) {
                            logger.error("商品买卖短信通知出错，商品id= "+capitalDetail.getCapitalDetailId());
                            e.printStackTrace();
                        }
                    }
                }



            }
        });
        return capitalDetail.getCapitalDetailId();
    }


    @Transactional
    @Override
    public Boolean updateCommodity(CommodityExtendVO.CommodityVOParam commodityVO){
        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityVO.getCommodityId());

        if (Objects.isNull(commodity)) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "商品id无效");
        }
//        //对于用户身份信息
//        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(commodity.getUserId());
//        BizException.isNull(commodityVO.getCapitalCipher(),"没有输入资金密码");
//        if(!commodityVO.getCapitalCipher().equals(userIdentity.getCapitalCipher())){
//            BizException.fail(ApiResponseCode.PARA_ERR,"资金密码错误");
//        }
        //对于用户身份信息
        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(commodity.getUserId());
        BizException.isNull(commodityVO.getCapitalCipher(), "没有输入资金密码");
        //判断资金密码是否正确
        userIdentityService.checkCapitalCipher(commodityVO.getCapitalCipher(), userIdentity.getCapitalCipher(), commodity.getUserId());


        commodity.setCommodityId(commodityVO.getCommodityId());
        commodity.setMinimumAmount(BigDecimal.valueOf(commodityVO.getMinimumAmount()));
        commodity.setMaximumAmount(BigDecimal.valueOf(commodityVO.getMaximumAmount()));
        if (commodity.getCurrencyPrice().compareTo(BigDecimal.valueOf(commodityVO.getTradePrice())) != 0){
            commodity.setCurrencyPrice(BigDecimal.valueOf(commodityVO.getTradePrice()));
            commodity.setVersionNumber(commodity.getVersionNumber() + 1);
        }

        commodity.setUserId(commodity.getUserId());
        commodity.setPayMethodList(commodityVO.getPayMethodListList().toString());
        commodity.setLeaveMessage(commodityVO.getLeaveMessage());
        List<Integer> list = new ArrayList<>();
        for (int i = 0;i < commodityVO.getPayMethodListList().size(); i++){
            Long userAccountId = Long.valueOf(commodityVO.getPayMethodListList().get(i).toString());
            UserAccount userAccount = userAccountService.getUserAccount(userAccountId);
            if (!Objects.isNull(userAccount)){
                if(commodity.getUserId().compareTo(userAccount.getUserId()) != 0){
                    BizException.fail(MessageCode.PAYMENT_ERR,"非法的支付方式");
                }
                list.add(userAccount.getPayType());
            }else {
                BizException.fail(ApiResponseCode.DATA_ERR,"账户id错误");
            }
        }
        commodity.setPayMethodTypeList(list.toString());
        commodity.setTradeCurb(commodityVO.getTradeCurb().toByteString().toStringUtf8());
        commodity.setUpdateTime(new Date());
        Integer result = commodityMapper.updateByPrimaryKeySelective(commodity);
        if (result == 0){
            BizException.fail(ApiResponseCode.METHOD_UN_IMPL, null);
        }
        return result > 0;
    }

    @Transactional
    @Override
    public Boolean delCommodity(Long commodityId){

        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);
        if (commodity.getStatus() > 0){
            BizException.fail(MessageCode.COMMODITY_OFFLINE,"当前商品已卖完");
        }

        //设置为已卖完
        commodity.setStatus(1);
        int res = commodityMapper.updateByPrimaryKeySelective(commodity);

        if (res == 0){
            BizException.fail(ApiResponseCode.METHOD_UN_IMPL, null);
        }
        CommodityTypeEnum commodityTypeEnum = CommodityTypeEnum.getInstance(commodity.getCommodityType());
        // 如果取消的是卖单，对冻结资金进行操作
        if (commodityTypeEnum == CommodityTypeEnum.SELL) {
            //计算手续费
            BigDecimal fee =commodity.getFeeQuantity();

            //对于用户资金信息
            CapitalInfoVO capitalInfoVO = capitalService.getCapitalInfoByUserIdAndCurrencyId(commodity.getUserId(), commodity.getCurrencyId());
            //用户的资金进行冻结操作
            BigDecimal totalMoney = commodity.getCurrencyQuantity().add(fee);
            if (capitalInfoVO.getCapitalFrozen().compareTo(totalMoney) < 0) {
                BizException.fail(MessageCode.NOT_ENOUGH_FROZEN_MONEY,"冻结资金余额不足");
            }

            capitalInfoVO.setCapitalFrozen(capitalInfoVO.getCapitalFrozen().subtract(totalMoney));
            capitalInfoVO.setCapitalAvailable(capitalInfoVO.getCapitalAvailable().add(totalMoney));

            int result = capitalService.updateCapitalInfo(capitalInfoVO);

            //记录资金变化
            CapitalLog capitalLog = new CapitalLog();
            capitalLog.setUserId(commodity.getUserId());
            capitalLog.setType(CapitalLogTypeEnum.THAW.getValue().byteValue());
            capitalLog.setCurrencyId(commodity.getCurrencyId());
            capitalLog.setAvailableNumber(totalMoney);
            capitalLog.setFrozenNumber(totalMoney.negate());
            capitalLog.setComment("取消商品，商品编号:" + commodityId);
            capitalLog.setOrderId(commodityId);
            capitalLog.setCreateTime(new Date());
            capitalLogMapper.insertSelective(capitalLog);

            return result > 0;
        } else {
            return true;
        }
    }


    @Override
    public PageInfo<CommodityInfoVO> getUserCommodityList(Long userId,Integer pageIndex, Integer pageSize){
        return  this.getUserCommodityList( userId,null ,  pageIndex,  pageSize);
    }

    @Override
    public PageInfo<CommodityInfoVO> getUserCommodityList(Long userId,String kind, Integer pageIndex, Integer pageSize){

        Map<String, Object> params = new HashMap<>();
        params.put("userId",userId);
        params.put("kind",kind);
        params.put("status",0);
        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<CommodityInfoVO> commodityInfoVOList = commodityMapper.selectByParam(params);
//        Double price = transactionPairService.getRealTimeOnePoint(kindEnum,relevantKindEnum);
        PageInfo<CommodityInfoVO> pageInfo = new PageInfo(commodityInfoVOList);

        return pageInfo;
    }
    @Override
    public Long countCommodity( Map<String, Object> params){
        return commodityMapper.countCommodity(params);
    }

    @Override
    public Commodity getCommodityById(Long commodityId){
        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);

        return  commodity;
    }

    /**
     * 对于用户是否有资格购买商品进行校验
     * @param commodityId
     * @return true 商品有效   商品无效 false
     */
    @Override
    public Boolean validateCommodity(Long commodityId,Long userId,Integer versionNumber){

        //统计取消次数
        String key = Contants.REDIS_USER_TRADE_CANCLE + userId;
        String count = redisUtil.getStringValue(key);
        if (StringUtils.isNoneEmpty(count) ){
            if (count.compareTo("3") >= 0){
                BizException.fail(MessageCode.CANCEL_ORDER_OVER, "取消交易次数过多，交易权限24小时内受限");
            }
        }

        Commodity commodity = commodityMapper.selectByPrimaryKey(commodityId);
        if (commodity != null && commodity.getStatus() == 0){

            if(commodity.getVersionNumber().compareTo(versionNumber) != 0){
                BizException.fail(MessageCode.COMMODITY_UPDATED,"该订单商家已修改价格");
            }

            try {
                CommodityExtendVO.TradeCurbDTO tradeCurbDTO = CommodityExtendVO.TradeCurbDTO.parseFrom(commodity.getTradeCurb().getBytes());
                UserIdentity userIdentityVO = userIdentityService.getUserExtendIdentity(userId);
                IdentityStautsEnum identityStautsEnum = IdentityStautsEnum.getInstance((int)userIdentityVO.getStatus());

                if (identityStautsEnum != IdentityStautsEnum.PASS){
                    if (identityStautsEnum == IdentityStautsEnum.WAIT){
                        BizException.fail(MessageCode.WAIT_IDENTITY,"等待实名认证");
                    }else {
                        BizException.fail(MessageCode.NOT_IDENTITY,"你没有实名认证");
                    }
                 }

                CapitalDetailCount capitalDetailCount = capitalDetailCountService.getCapitalDetailCountInfo(userId);

                if (capitalDetailCount.getTotalTradeCount().compareTo((long)tradeCurbDTO.getTradeCount()) < 0){
                    BizException.fail(MessageCode.ORDER_COUNT_LIMIT,String.valueOf(tradeCurbDTO.getTradeCount()));
                }

                if (commodity.getCommodityType() == CommodityTypeEnum.BUY.getValue()){

                    if (StringUtils.isEmpty(userIdentityVO.getCapitalCipher())){
                        BizException.fail(MessageCode.CAPITAL_CIPHER_ERR_EMPTY,"你没有设置资金密码");
                    }

                    List<UserAccount> list = userAccountService.getUserAccountList(commodity.getUserId());
                    if (list == null || list.isEmpty()){
                        BizException.fail(MessageCode.NO_PAYMENT_ERR,"你需要添加支付方式");
                    }
                    CapitalInfoVO capitalInfoVO = new CapitalInfoVO();
                    capitalInfoVO.setUserId(userId);
                    capitalInfoVO.setCurrencyId(commodity.getCurrencyId());
                    CapitalInfoVO capitalInfoVO1 = capitalService.getCapitalInfoVO(capitalInfoVO);
                    if (capitalInfoVO1 ==null || capitalInfoVO1.getCapitalAvailable().compareTo(BigDecimal.ZERO) <= 0){
                        BizException.fail(MessageCode.NOT_ENOUGH_ERR,"你没有可卖货币");
                    }

                }

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            return  true;
        }else{
            BizException.fail(MessageCode.COMMODITY_OFFLINE,"此商品已下架");
        }

        return  false;
    }
}
