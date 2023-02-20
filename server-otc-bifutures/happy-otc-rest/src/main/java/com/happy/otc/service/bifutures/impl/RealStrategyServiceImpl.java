package com.happy.otc.service.bifutures.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.CloseReasion;
import com.happy.otc.bifutures.FundDetailTemplate;
import com.happy.otc.bifutures.dto.FuturesFundDetailDto;
import com.happy.otc.bifutures.dto.FuturesStrategyDto;
import com.happy.otc.bifutures.dto.UserAssetsDto;
import com.happy.otc.bifutures.entity.FuturesStrategy;
import com.happy.otc.bifutures.entity.MaxAndMin;
import com.happy.otc.bifutures.entity.UserAssets;
import com.happy.otc.bifutures.enums.FundDetailDetailEnum;
import com.happy.otc.bifutures.enums.FundDetailExplainEnum;
import com.happy.otc.bifutures.pojo.FuturesStrategyQuery;
import com.happy.otc.bifutures.utill.CollectionUtil;
import com.happy.otc.bifutures.utill.NumberUtil;
import com.happy.otc.bifutures.vo.BiFuturesUserInfo;
import com.happy.otc.bifutures.vo.FuturesStrategyVo;
import com.happy.otc.contants.Contants;
import com.happy.otc.dao.BiFuturesStrategyMapper;
import com.happy.otc.dao.BiFuturesUserAssetsDetailMapper;
import com.happy.otc.dao.BiFuturesUserAssetsMapper;
import com.happy.otc.service.IUserIdentityService;
import com.happy.otc.service.bifutures.MetaRiskService;
import com.happy.otc.service.bifutures.RealStrategyService;
import com.happy.otc.service.bifutures.UserAssetsService;
import com.happy.otc.vo.UserIdentityVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2018\11\21 0021.
 */
@Service
public class RealStrategyServiceImpl implements RealStrategyService {

    @Autowired
    private UserAssetsService userAssetsService;
    @Autowired
    private IUserIdentityService iUserIdentityService;
    @Autowired
    private MetaRiskService metaRiskService;
    @Autowired
    private BiFuturesStrategyMapper biFuturesStrategyMapper;
    @Autowired
    private BiFuturesUserAssetsMapper biFuturesUserAssetsMapper;
    @Autowired
    BiFuturesUserAssetsDetailMapper biFuturesUserAssetsDetailMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户实盘点买数据标识
     */
    String USERSALEFUTURESSTRATEGY = "USERSALEFUTURESSTRATEGY_";

    @Override
    @Transactional
    public FuturesStrategyVo createRealStrategy(FuturesStrategyDto dto) throws IOException, SAXException, ParserConfigurationException {
        Integer result = null;
        Integer result1= null;
        Integer result2= null;
        Integer result3= null;
        UserIdentityVO userIdentityVO = iUserIdentityService.getByUserId(dto.getUserId());
        if (userIdentityVO == null){
            throw new IllegalArgumentException("无当前用户");
        }
        metaRiskService.checkCreateValue(dto);
        BiFuturesUserInfo userInfo = userAssetsService.getUserInfoById(dto.getUserId());
        dto.setUserName(userInfo.getUserName());
        if (dto.getMold() == 1){
            dto.setStatus(2);
            dto.setBuyDealTime(new Date());
            dto.setBuyOrderTime(new Date());
        }else if (dto.getMold() == 2){
            dto.setStatus(1);
            dto.setBuyOrderTime(new Date());
        }

        result = biFuturesStrategyMapper.insert(dto);

         Long strategyId = dto.getId();

        UserAssets userAssets = biFuturesUserAssetsMapper.selectByUserId(dto.getUserId());
        if (userAssets==null || userAssets.getBalance().compareTo(dto.getPrincipal().add(dto.getServiceCharge()))<0){
            BizException.fail(111,"账户资金不足，请及时充值！");
        }else if ( userAssets.getBalance().compareTo(dto.getPrincipal().add(dto.getServiceCharge()))>=0){
            UserAssetsDto userAssetsDto = new UserAssetsDto();
            userAssetsDto.setUserId(dto.getUserId());
            userAssetsDto.setBalance(userAssets.getBalance().subtract(dto.getPrincipal()).subtract(dto.getServiceCharge()));
            userAssetsDto.setFundOut(dto.getPrincipal().add(dto.getServiceCharge()));
            userAssetsDto.setTime(new Date());
            result1 = biFuturesUserAssetsMapper.updateAssetsByUserAssetsDto(userAssetsDto);

            String detail1 = FundDetailDetailEnum.FUTURES_STRATEGY_CREATE_OUT.getValue().replace("#{strategyId}", "" +dto.getId()).
                    replace("#{margin}",numberFormat1(dto.getPrincipal()));
            FuturesFundDetailDto futuresFundDetailDto =new FuturesFundDetailDto();
            futuresFundDetailDto.setBalance(userAssets.getBalance().subtract(dto.getPrincipal()));
            futuresFundDetailDto.setType(FundDetailTemplate.OUT);
            futuresFundDetailDto.setUserId(dto.getUserId());
            futuresFundDetailDto.setMoney(dto.getPrincipal());
            futuresFundDetailDto.setExplain(FundDetailExplainEnum.BUY_STRATEGY_SUCCESS_OUT.getValue());
            futuresFundDetailDto.setDetail(detail1);
            futuresFundDetailDto.setSourceId(dto.getId());
            futuresFundDetailDto.setTime(new Date());
            result2= biFuturesUserAssetsDetailMapper.insert(futuresFundDetailDto);

            String detail2 = FundDetailDetailEnum.FUTURES_STRATEGY_SERVIC_CHARGE_OUT.getValue().replace("#{strategyId}", "" + dto.getId()).
                    replace("#{serviceCharge}",numberFormat1(dto.getServiceCharge()));
            FuturesFundDetailDto futuresFundDetailDto1 = new FuturesFundDetailDto();
            futuresFundDetailDto1.setBalance(userAssets.getBalance().subtract(dto.getPrincipal()).subtract(dto.getServiceCharge()));
            futuresFundDetailDto1.setMoney(dto.getServiceCharge());
            futuresFundDetailDto1.setExplain(FundDetailExplainEnum.STRATEGY_SERVICE_CHARGE_OUT.getValue());
            futuresFundDetailDto1.setDetail(detail2);
            futuresFundDetailDto1.setType(FundDetailTemplate.OUT);
            futuresFundDetailDto1.setUserId(dto.getUserId());
            futuresFundDetailDto1.setSourceId(dto.getId());
            futuresFundDetailDto1.setTime(new Date());

            result3 = biFuturesUserAssetsDetailMapper.insert(futuresFundDetailDto1);
        }
        redisUtil.delete(USERSALEFUTURESSTRATEGY+dto.getUserId());
        redisUtil.delete(USERSALEFUTURESSTRATEGY+dto.getUserId()+"BuyDeal");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+dto.getUserId()+"Order");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+dto.getUserId()+"Sell");
            if (result==1&&result1==1&&result2==1&&result3==1){
                return new FuturesStrategyVo(1,strategyId);
            }else{
                return new FuturesStrategyVo(2,strategyId);
            }

    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, readOnly = true)
    public List<FuturesStrategy> selectByUserId(FuturesStrategyQuery futuresStrategyQuery,String bbc) {
        String futruesStrategy = null;
        List<FuturesStrategy> futuresStrategyList = null;
        if (bbc.equals("1")){
             futruesStrategy = redisUtil.getStringValue(USERSALEFUTURESSTRATEGY+futuresStrategyQuery.getUserId()+"BuyDeal");
        }else if (bbc.equals("2")){
             futruesStrategy = redisUtil.getStringValue(USERSALEFUTURESSTRATEGY+futuresStrategyQuery.getUserId()+"Order");
        }else if (bbc.equals("3")){
             futruesStrategy = redisUtil.getStringValue(USERSALEFUTURESSTRATEGY+futuresStrategyQuery.getUserId()+"Sell");
        }

        if (StringUtils.isNotBlank(futruesStrategy)){
            futuresStrategyList = JSONObject.parseArray(futruesStrategy, FuturesStrategy.class);
                return futuresStrategyList;
        }else {
            if (bbc.equals("1")){
                 futuresStrategyList = biFuturesStrategyMapper.selectBuyDeal(futuresStrategyQuery);
                redisUtil.setStringValue(USERSALEFUTURESSTRATEGY+futuresStrategyQuery.getUserId()+"BuyDeal", JSON.toJSONString(futuresStrategyList));

            }else if (bbc.equals("2")){
                futuresStrategyList = biFuturesStrategyMapper.selectOrder(futuresStrategyQuery);
                redisUtil.setStringValue(USERSALEFUTURESSTRATEGY+futuresStrategyQuery.getUserId()+"Order", JSON.toJSONString(futuresStrategyList));

            }else if (bbc.equals("3")){
                futuresStrategyQuery.setStatus(CollectionUtil.asList(3,4));
                futuresStrategyList = biFuturesStrategyMapper.selectSell(futuresStrategyQuery);
                ArrayList<FuturesStrategy> futuresStrategyList3= new ArrayList<>();
                ArrayList<FuturesStrategy> futuresStrategyList4= new ArrayList<>();
                for (FuturesStrategy s : futuresStrategyList) {
                    if (s.getStatus()==3){
                        futuresStrategyList3.add(s);
                    }
                    if (s.getStatus()==4){
                        futuresStrategyList4.add(s);
                    }
                }
                FuturesStrategy[] temp = new FuturesStrategy[futuresStrategyList.size()];
                FuturesStrategy[] s3 = (FuturesStrategy[]) futuresStrategyList3.toArray(new FuturesStrategy[futuresStrategyList3.size()]);
                FuturesStrategy[] s4 = (FuturesStrategy[]) futuresStrategyList4.toArray(new FuturesStrategy[futuresStrategyList4.size()]);
                int i = 0;
                int j = 0;
                int t = 0;
                while (i<=s3.length-1 && j<=s4.length-1){
                    if (s3[i].getSellDealTime().getTime()>=s4[j].getCancelTime().getTime()){
                        temp[t++] = s3[i++];
                    }else{
                        temp[t++] = s4[j++];
                    }
                }

                while (i<=s3.length-1){
                    temp[t++] = s3[i++];
                }

                while (j<=s4.length-1){
                    temp[t++] = s4[j++];
                }
                t = 0;

                futuresStrategyList = Arrays.asList(temp);
                redisUtil.setStringValue(USERSALEFUTURESSTRATEGY+futuresStrategyQuery.getUserId()+"Sell", JSON.toJSONString(futuresStrategyList));

            }
        }
        return futuresStrategyList;
    }

    @Override
    public PageInfo<FuturesStrategy> selectByPage(FuturesStrategyQuery futuresStrategyQuery, Integer pageIndex, Integer pageSize) {

        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
            List<FuturesStrategy> futuresStrategyList = biFuturesStrategyMapper.selectByUserId(futuresStrategyQuery);
            PageInfo pageInfo = new PageInfo(futuresStrategyList);
            return pageInfo;
        }


    @Override
    @Transactional
    public String closeStrategy(String ids, Long userId) {

        if (StringUtils.isEmpty(ids)){
            BizException.fail(501,"id不能为空");
        }
        int a = 0;
        String[] strategyIdsArr = ids.split(",");
        int b = strategyIdsArr.length;

        //去重处理
        TreeSet<Integer> strategyIds = new TreeSet<Integer>();
        for(String strategyId : strategyIdsArr) {
            if(!StringUtils.isBlank(strategyId)) {
                strategyIds.add(Integer.valueOf(strategyId));
            }
        }
        for (Integer strategyId : strategyIds) {
            Integer result = closeCurrentStrategy(Integer.valueOf(strategyId),userId);
            if (result == 1){
                a++;
            }
        }

        redisUtil.delete(USERSALEFUTURESSTRATEGY+userId);
        redisUtil.delete(USERSALEFUTURESSTRATEGY+userId+"BuyDeal");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+userId+"Order");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+userId+"Sell");
        return "发起卖出"+b+"条策略，卖出成功"+a+"条";
    }

    @Override
    @Transactional
    public Integer setQuitGainLoss(FuturesStrategyDto futuresStrategyDto) {
        FuturesStrategy fs = biFuturesStrategyMapper.selectById(futuresStrategyDto.getId());

        if (fs ==null){
            BizException.fail(501,"不存在该策略");
        }
        if (!fs.getUserId().equals(futuresStrategyDto.getUserId())){
            BizException.fail(501,"策略所属人错误");
        }
        if (fs.getStatus()!=2) {
            BizException.fail(501,"策略状态错误");
        }
        Integer result = biFuturesStrategyMapper.update(futuresStrategyDto);
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId());
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"BuyDeal");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"Order");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"Sell");
        return result;
    }

    @Override
    @Transactional
    public Integer setPrincipal(FuturesStrategyDto dto) {
        Integer result1= null;
        Integer result2= null;
        Integer result3= null;
        FuturesStrategy fs = biFuturesStrategyMapper.selectById(dto.getId());
        if (fs ==null){
            BizException.fail(501,"不存在该策略");
        }
        if (!fs.getUserId().equals(dto.getUserId())){
            BizException.fail(501,"策略所属人错误");
        }
        if (fs.getStatus()!=2) {
            BizException.fail(501,"策略状态错误");
        }
        dto.setPrincipal(dto.getPrincipal().add(fs.getPrincipal()));
         result3 = biFuturesStrategyMapper.update(dto);

        UserAssets userAssets = biFuturesUserAssetsMapper.selectByUserId(dto.getUserId());
        if (userAssets==null || userAssets.getBalance().compareTo(dto.getPrincipal().subtract(fs.getPrincipal()))<0){
            BizException.fail(111,"账户资金不足，请及时充值");
        }else if ( userAssets.getBalance().compareTo(dto.getPrincipal())>=0){
            UserAssetsDto userAssetsDto = new UserAssetsDto();
            userAssetsDto.setUserId(dto.getUserId());
            userAssetsDto.setBalance(userAssets.getBalance().subtract(dto.getPrincipal()).add(fs.getPrincipal()));
            userAssetsDto.setFundOut(dto.getPrincipal());
            userAssetsDto.setTime(new Date());
            result1 = biFuturesUserAssetsMapper.updateAssetsByUserAssetsDto(userAssetsDto);

            String detail1 = FundDetailDetailEnum.FUTURES_STRATEGY_SET_PRINCIPAL_OUT.getValue().replace("#{strategyId}", "" +dto.getId()).
                    replace("#{margin}",numberFormat1(dto.getPrincipal().subtract(fs.getPrincipal())));
            FuturesFundDetailDto futuresFundDetailDto =new FuturesFundDetailDto();
            futuresFundDetailDto.setBalance(userAssets.getBalance().subtract(dto.getPrincipal()).add(fs.getPrincipal()));
            futuresFundDetailDto.setType(FundDetailTemplate.OUT);
            futuresFundDetailDto.setUserId(dto.getUserId());
            futuresFundDetailDto.setMoney(dto.getPrincipal().subtract(fs.getPrincipal()));
            futuresFundDetailDto.setExplain(FundDetailExplainEnum.UPDATE_STRATEGY_PRINCIPAL_OUT.getValue());
            futuresFundDetailDto.setDetail(detail1);
            futuresFundDetailDto.setSourceId(dto.getId());
            futuresFundDetailDto.setTime(new Date());
            result2= biFuturesUserAssetsDetailMapper.insert(futuresFundDetailDto);

        }
        redisUtil.delete(USERSALEFUTURESSTRATEGY+dto.getUserId());
        redisUtil.delete(USERSALEFUTURESSTRATEGY+dto.getUserId()+"BuyDeal");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+dto.getUserId()+"Order");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+dto.getUserId()+"Sell");
        if (result1==1&&result2==1&&result3==1){
            return 1;
        }else{
            return 2;
        }

    }

    @Override
    public FuturesStrategy selectById(Long id) {

        FuturesStrategy ft = biFuturesStrategyMapper.selectById(id);
        return ft;
    }

    @Override
    public Integer cancelStrategy(FuturesStrategyDto futuresStrategyDto) {
        FuturesStrategy fs = biFuturesStrategyMapper.selectById(futuresStrategyDto.getId());

        if (fs ==null){
            BizException.fail(501,"不存在该条策略");
        }
        if (!fs.getUserId().equals(futuresStrategyDto.getUserId())){
            BizException.fail(501,"策略所属人错误");
        }
        if (fs.getStatus()!=1) {
            BizException.fail(501,"策略状态错误");
        }
            Integer result = biFuturesStrategyMapper.update(futuresStrategyDto);
            redisUtil.delete(USERSALEFUTURESSTRATEGY + futuresStrategyDto.getUserId());
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"BuyDeal");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"Order");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"Sell");
        UserAssets userAssets = biFuturesUserAssetsMapper.selectByUserId(fs.getUserId());

        String explain = FundDetailExplainEnum.SELL_LIMITE_CURRENT_IN.getValue();
        String detail = FundDetailDetailEnum.FUTURES_STRATEGY_REFUND_STRATEGY_IN.getValue().replace("#{strategyId}", "" +fs.getId()).
                replace("#{margin}",numberFormat1(fs.getPrincipal())+"").replace("#{serviceCharge}",numberFormat1(fs.getServiceCharge())+"");
        fundPlay(fs.getUserId(),userAssets.getBalance().add(fs.getPrincipal()).add(fs.getServiceCharge()),
                fs.getPrincipal().add(fs.getServiceCharge()),explain,detail,1,fs.getId());
            return result;

    }

    @Override
    public Integer setIsDefer(FuturesStrategyDto futuresStrategyDto) {
        FuturesStrategy fs = biFuturesStrategyMapper.selectById(futuresStrategyDto.getId());
        if (fs ==null){
            BizException.fail(501,"不存在策略");
        }
        if (!fs.getUserId().equals(futuresStrategyDto.getUserId())){
            BizException.fail(501,"策略所属人错误");
        }
        if (fs.getStatus()!=2) {
            BizException.fail(501,"策略状态错误");
        }
        Integer result = biFuturesStrategyMapper.update(futuresStrategyDto);
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId());
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"BuyDeal");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"Order");
        redisUtil.delete(USERSALEFUTURESSTRATEGY+futuresStrategyDto.getUserId()+"Sell");
        return result;
    }

    @Override
    public MaxAndMin getAmount(FuturesStrategyQuery futuresStrategyQuery) {
        FuturesStrategyQuery futuresStrategyQuery1 = new FuturesStrategyQuery();
        futuresStrategyQuery1.setUserId(futuresStrategyQuery.getUserId());
        futuresStrategyQuery1.setBiCode(futuresStrategyQuery.getBiCode());
        futuresStrategyQuery1.setStatus(futuresStrategyQuery.getStatus());
        futuresStrategyQuery1.setDirection("B");
        futuresStrategyQuery1.setType(1);
        Integer b = biFuturesStrategyMapper.selectAmount(futuresStrategyQuery1);
        FuturesStrategyQuery futuresStrategyQuery2 = new FuturesStrategyQuery();
        futuresStrategyQuery2.setUserId(futuresStrategyQuery.getUserId());
        futuresStrategyQuery2.setBiCode(futuresStrategyQuery.getBiCode());
        futuresStrategyQuery2.setStatus(futuresStrategyQuery.getStatus());
        futuresStrategyQuery2.setDirection("S");
        futuresStrategyQuery2.setType(1);
        Integer s = biFuturesStrategyMapper.selectAmount(futuresStrategyQuery2);
        MaxAndMin maxAndMin = new MaxAndMin();
        maxAndMin.setBamount(b== null ? 0:b);
        maxAndMin.setSamount(s== null ? 0:s);
        return maxAndMin;
    }

    public String numberFormat1(BigDecimal number) {
        return NumberUtil.format(number, "#,##0.000");
    }

    public Integer closeCurrentStrategy(Integer id , Long userId){

        BigDecimal positionPol = BigDecimal.ZERO;
        Long strategyId = id.longValue();

        FuturesStrategy fs = biFuturesStrategyMapper.selectById(strategyId);
        BigDecimal newPrice = userAssetsService.getNewPrice(fs.getBiCode());

        if (fs ==null){
            BizException.fail(501,"不存在该策略");
        }
        if (!fs.getUserId().equals(userId)){
            BizException.fail(501,"策略所属人错误");
        }

        if (fs.getStatus()!=2) {
            BizException.fail(501,"策略状态错误");
        }

        FuturesStrategyDto dto = new FuturesStrategyDto();
        dto.setId(fs.getId());
        dto.setSellPriceDeal(newPrice);
        dto.setStatus(3);
        dto.setCloseReason(CloseReasion.TRIGGER_SELF_SELL);
        dto.setSellDealTime(new Date());
        biFuturesStrategyMapper.update(dto);

        if (fs.getDirection().equals("B")) {
            positionPol = (newPrice.subtract(fs.getBuyPriceDeal())).multiply(new BigDecimal
                    (fs.getAmount())).multiply(fs.getContractValue());
        } else if (fs.getDirection().equals("S")) {
            positionPol = (fs.getBuyPriceDeal().subtract(newPrice)).multiply(new BigDecimal
                    (fs.getAmount())).multiply(fs.getContractValue());
        }
        UserAssets userAssets = biFuturesUserAssetsMapper.selectByUserId(fs.getUserId());
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal money = BigDecimal.ZERO;
        String explain = FundDetailExplainEnum.SELL_STRATEGY_SUCCESS_IN.getValue();
        String detail = null;
        if(positionPol.compareTo(new BigDecimal(0))>=0){
            detail = FundDetailDetailEnum.FUTURES_STRATEGY_FINISH_GAIN.getValue().replace("#{strategyId}", "" +fs.getId()).
                    replace("#{profit}",numberFormat1(positionPol)+"").replace("#{margin}",numberFormat1(fs.getPrincipal())+"");
            balance = userAssets.getBalance().add(positionPol).add(fs.getPrincipal());
            money = positionPol.add(fs.getPrincipal());
        }else if (positionPol.compareTo(new BigDecimal(0))<0){
            detail = FundDetailDetailEnum.FUTURES_STRATEGY_FINISH_LOSE.getValue().replace("#{strategyId}", "" +fs.getId()).
                    replace("#{profit}",numberFormat1(positionPol.abs())+"").replace("#{margin}",numberFormat1(positionPol.abs())+"").replace("#{money}", "" +numberFormat1(fs.getPrincipal().add(positionPol)));
            balance = userAssets.getBalance().add(fs.getPrincipal()).subtract(positionPol);
            money = positionPol.add(fs.getPrincipal());
        }
        Integer result = fundPlay(fs.getUserId(),balance,money,explain,detail,1,fs.getId());
        return result;
    }

    public Integer fundPlay(Long userId, BigDecimal balance, BigDecimal money, String explain, String detail, Integer type, Long id){
        Integer result1 =null;
        Integer result2 =null;
        UserAssetsDto userAssetsDto = new UserAssetsDto();
        userAssetsDto.setUserId(userId);
        userAssetsDto.setBalance(balance);
        if (type == 1){
            userAssetsDto.setFundIn(money);
        }else if (type == 2){
            userAssetsDto.setFundOut(money);
        }
        userAssetsDto.setTime(new Date());
        result1 = biFuturesUserAssetsMapper.updateAssetsByUserAssetsDto(userAssetsDto);
        FuturesFundDetailDto futuresFundDetailDto =new FuturesFundDetailDto();
        futuresFundDetailDto.setBalance(balance);
        futuresFundDetailDto.setType(type);
        futuresFundDetailDto.setUserId(userId);
        futuresFundDetailDto.setMoney(money);
        futuresFundDetailDto.setExplain(explain);
        futuresFundDetailDto.setDetail(detail);
        futuresFundDetailDto.setSourceId(id);
        futuresFundDetailDto.setTime(new Date());
        result2= biFuturesUserAssetsDetailMapper.insert(futuresFundDetailDto);

        if (result1==1&&result2==1){
            return 1;
        }else{
            return 2;
        }
    }
}
