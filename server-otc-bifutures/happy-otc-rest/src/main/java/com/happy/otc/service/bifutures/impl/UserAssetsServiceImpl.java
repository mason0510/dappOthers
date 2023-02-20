package com.happy.otc.service.bifutures.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitan.common.exception.BizException;
import com.bitan.common.vo.Result;
import com.bitan.market.vo.OriginalExchangeInfoVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.FundDetailTemplate;
import com.happy.otc.bifutures.dto.FuturesFundDetailDto;
import com.happy.otc.bifutures.dto.UserAssetsDto;
import com.happy.otc.bifutures.entity.FuturesFundDetail;
import com.happy.otc.bifutures.entity.FuturesStrategy;
import com.happy.otc.bifutures.entity.UserAssets;
import com.happy.otc.bifutures.enums.BiFuturesKindEnum;
import com.happy.otc.bifutures.enums.FundDetailDetailEnum;
import com.happy.otc.bifutures.enums.FundDetailExplainEnum;
import com.happy.otc.bifutures.pojo.FundDetailQuery;
import com.happy.otc.bifutures.pojo.FuturesStrategyQuery;
import com.happy.otc.bifutures.utill.CollectionUtil;
import com.happy.otc.bifutures.vo.BiFuturesOtc;
import com.happy.otc.bifutures.vo.BiFuturesUserInfo;
import com.happy.otc.contants.Contants;
import com.happy.otc.dao.BiFuturesStrategyMapper;
import com.happy.otc.dao.BiFuturesUserAssetsDetailMapper;
import com.happy.otc.dao.BiFuturesUserAssetsMapper;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.service.IUserIdentityService;
import com.happy.otc.service.IUserService;
import com.happy.otc.service.bifutures.MetaRiskContext;
import com.happy.otc.service.bifutures.RealStrategyService;
import com.happy.otc.service.bifutures.UserAssetsService;
import com.happy.otc.service.remote.IMarketService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
@Service
public class UserAssetsServiceImpl implements UserAssetsService {
    private static final Logger logger = LogManager.getLogger(UserAssetsServiceImpl.class);
    @Autowired
    private BiFuturesUserAssetsMapper biFuturesUserAssetsMapper;

    @Autowired
    private IUserIdentityService iUserIdentityService;

    @Autowired
    IUserService userService;

    @Autowired
    BiFuturesUserAssetsDetailMapper biFuturesUserAssetsDetailMapper;

    @Autowired
    RealStrategyService realStrategyService;
    @Autowired
    private MetaRiskContext metaRiskContext;
    @Autowired
    private IMarketService iMarketService;
    @Autowired
    private BiFuturesStrategyMapper biFuturesStrategyMapper;

    @Override
    public BiFuturesOtc getAssetsByUserId(Long userId) throws ParseException, ParserConfigurationException, SAXException, IOException {
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal principal= BigDecimal.ZERO;
        BigDecimal usedPrincipal= BigDecimal.ZERO;
        BigDecimal positionPol= BigDecimal.ZERO;
        BigDecimal todayCellPol= BigDecimal.ZERO;
        FuturesStrategyQuery futuresStrategyQuery = new FuturesStrategyQuery();
        futuresStrategyQuery.setType(1);
        futuresStrategyQuery.setUserId(userId);
        futuresStrategyQuery.setStatus(CollectionUtil.asList(1,2,3));
        List<FuturesStrategy> futuresStrategy = biFuturesStrategyMapper.selectByUserId(futuresStrategyQuery);
        for (FuturesStrategy strategy : futuresStrategy) {
            String risk = metaRiskContext.getMetaRisk(BiFuturesKindEnum.getByValue(strategy.getBiCode()));
            JSONObject jsonObject = JSON.parseObject(risk);
            String time =jsonObject.getJSONObject("tradingTimeLimit").getString("value");
           /* MetaRisk metaRisk = metaRiskContext.getMetaRisk(BiFuturesKindEnum.getByValue(strategy.getBiCode()));
            SchemeRisk schemeRisk = metaRisk.findSchemeRiskByType(1);*/
            //String time = schemeRisk.getRiskParameter("tradingTimeLimit").getValue();
            BigDecimal newPrice = getNewPrice(strategy.getBiCode());
            if (strategy.getStatus() == 2) {
                if (strategy.getDirection().equals("B")) {
                    positionPol = positionPol.add((newPrice.subtract(strategy.getBuyPriceDeal())).multiply(new BigDecimal
                            (strategy.getAmount())).multiply(strategy.getContractValue()));
                } else if (strategy.getDirection().equals("S")) {
                    positionPol = positionPol.add((strategy.getBuyPriceDeal().subtract(newPrice)).multiply(new BigDecimal
                            (strategy.getAmount())).multiply(strategy.getContractValue()));
                }

            }
            if (strategy.getStatus()==1||strategy.getStatus()==2){
                usedPrincipal = usedPrincipal.add(strategy.getPrincipal());
            }
            if (strategy.getStatus()==3&&isBetweenTime(strategy.getSellDealTime())) {
                if (strategy.getDirection().equals("B")) {
                    todayCellPol = todayCellPol.add((strategy.getSellPriceDeal().subtract(strategy.getBuyPriceDeal())).multiply(new BigDecimal
                            (strategy.getAmount())).multiply(strategy.getContractValue()));
                } else if (strategy.getDirection().equals("S")) {
                    todayCellPol = todayCellPol.add((strategy.getBuyPriceDeal().subtract(strategy.getSellPriceDeal())).multiply(new BigDecimal
                            (strategy.getAmount())).multiply(strategy.getContractValue()));
                }
            }
        }

        UserAssets userAssets = biFuturesUserAssetsMapper.selectByUserId(userId);
        BiFuturesOtc biFuturesOtc = new BiFuturesOtc();
        if (userAssets != null) {
            principal = userAssets.getBalance();
            balance = principal.add(usedPrincipal).add(positionPol);
            biFuturesOtc.setBalance(balance.setScale(6, BigDecimal.ROUND_HALF_DOWN));
            biFuturesOtc.setPositionPol(positionPol.setScale(6, BigDecimal.ROUND_HALF_DOWN));
            biFuturesOtc.setTodayCellPol(todayCellPol.setScale(6, BigDecimal.ROUND_HALF_DOWN));
            biFuturesOtc.setPrincipal(principal.setScale(6, BigDecimal.ROUND_HALF_DOWN));
            biFuturesOtc.setUsedPrincipal(usedPrincipal.setScale(6, BigDecimal.ROUND_HALF_DOWN));
        }else {
            biFuturesOtc.setBalance(new BigDecimal(0));
            biFuturesOtc.setPositionPol(new BigDecimal(0));
            biFuturesOtc.setTodayCellPol(new BigDecimal(0));
            biFuturesOtc.setPrincipal(new BigDecimal(0));
            biFuturesOtc.setUsedPrincipal(new BigDecimal(0));
        }
        return biFuturesOtc;
    }

    @Override
    @Transactional
    public Integer updateAssetsByUserAssetsDto(Long userId, BigDecimal money,Integer isInOut) {

        Integer result = null;
        Integer insertResult = null;
        UserAssets userAssets = biFuturesUserAssetsMapper.selectByUserId(userId);

        if(userAssets != null) {
            UserAssetsDto dto = new UserAssetsDto();
            dto.setUserId(userId);
            dto.setTime(new Date());
            if (isInOut == 1) {
                dto.setFundIn(userAssets.getFundIn().add(money));
                dto.setBalance(userAssets.getBalance().add(money));
            } else if (isInOut == 2) {
                dto.setFundOut(userAssets.getFundOut().add(money));
                dto.setBalance(userAssets.getBalance().subtract(money));
            }
            result = biFuturesUserAssetsMapper.updateAssetsByUserAssetsDto(dto);
        FuturesFundDetailDto futuresFundDetailDto =new FuturesFundDetailDto();
        futuresFundDetailDto.setUserId(userId);
        futuresFundDetailDto.setMoney(money);
        futuresFundDetailDto.setTime(new Date());
        if (isInOut == 1) {
            futuresFundDetailDto.setExplain(FundDetailExplainEnum.CHARGE_IN.getValue());
            futuresFundDetailDto.setDetail(FundDetailDetailEnum.FUTURES_CHARGE_IN.getValue());
            futuresFundDetailDto.setType(FundDetailTemplate.IN);
            futuresFundDetailDto.setBalance(userAssets.getBalance().add(money));
        }else if (isInOut == 2){
            futuresFundDetailDto.setExplain(FundDetailExplainEnum.WITHDRAW_OUT.getValue());
            futuresFundDetailDto.setDetail(FundDetailDetailEnum.FUTURES_CHARGE_OUT.getValue());
            futuresFundDetailDto.setType(FundDetailTemplate.OUT);
            futuresFundDetailDto.setBalance(userAssets.getBalance().subtract(money));
        }
        insertResult = biFuturesUserAssetsDetailMapper.insert(futuresFundDetailDto);
        }else {
            UserAssetsDto dto = new UserAssetsDto();
            dto.setUserId(userId);
            dto.setTime(new Date());
            dto.setFundIn(money);
            dto.setFundOut(new BigDecimal(0.0000));            dto.setBalance(money);
            result = biFuturesUserAssetsMapper.insert(dto);
            FuturesFundDetailDto futuresFundDetailDto =new FuturesFundDetailDto();
            futuresFundDetailDto.setUserId(userId);
            futuresFundDetailDto.setMoney(money);
            futuresFundDetailDto.setTime(new Date());
            futuresFundDetailDto.setExplain(FundDetailExplainEnum.CHARGE_IN.getValue());
            futuresFundDetailDto.setDetail(FundDetailDetailEnum.FUTURES_CHARGE_IN.getValue());
            futuresFundDetailDto.setType(FundDetailTemplate.IN);
            futuresFundDetailDto.setBalance(money);
            insertResult = biFuturesUserAssetsDetailMapper.insert(futuresFundDetailDto);
        }
        if (result==1 && insertResult==1){
            return 1;
        }else {
            return 2;
        }
    }

    @Override
    public Integer updateByUserId(UserAssetsDto dto) {
        return  biFuturesUserAssetsMapper.updateAssetsByUserAssetsDto(dto);
    }

    @Override
    public BiFuturesUserInfo getUserInfoById(Long userId) {
        //实名认证的用户信息
        UserIdentity userRealInfo = iUserIdentityService.getUserExtendIdentity(userId);
        //平台用户信息
        UserDTO userInfo = userService.getUserInfoByUserId(userId);
        BiFuturesUserInfo user = new BiFuturesUserInfo();
        user.setUserId(userId);
        user.setUserName(userInfo.getUserName());
        user.setUserRealName(userRealInfo.getRealName());
        user.setMobile(userInfo.getMobile());
        return user;
    }

    @Override
    public PageInfo<FuturesFundDetail> selectPageByUserId(FundDetailQuery fundDetailQuery, Integer pageIndex, Integer pageSize) {

        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<FuturesFundDetail> datail = biFuturesUserAssetsDetailMapper.selectPageByUserId(fundDetailQuery);

        PageInfo pageInfo = new PageInfo(datail);
        return pageInfo;
    }

    @Override
    public BigDecimal getNewPrice(String biCode) {
        List<String> kindList = CollectionUtil.asList(biCode+"/USDT");
        Result<List<OriginalExchangeInfoVO>> result = iMarketService.getRealTimeOnePoint(2,kindList);
        if (!result.isSuccess()) {
            BizException.fail(999, "获取实时行情信息失败");
        }
        List<OriginalExchangeInfoVO> originalExchangeInfoVOs = result.getData();
        BigDecimal newPrice = BigDecimal.ZERO;
        for (OriginalExchangeInfoVO originalExchangeInfoVO : originalExchangeInfoVOs) {
            if (biCode.equals("EOS")||biCode.equals("XRP")) {
                newPrice = new BigDecimal(originalExchangeInfoVO.getPrice()).setScale(4,BigDecimal.ROUND_HALF_UP);
            }else {
                newPrice = new BigDecimal(originalExchangeInfoVO.getPrice()).setScale(2,BigDecimal.ROUND_HALF_UP);
            }
        }

        return newPrice;

    }
    public static Boolean isBetweenTime(Date current) throws ParseException {
        //logger.info("当前系统时间"+new Date());
        String times = "06:00,24:00|00:00,05:55";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY,8);
        //logger.info("当前时间加8小时"+calendar);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(current);
        currentCalendar.add(Calendar.HOUR_OF_DAY,8);
        //logger.info("卖出时间"+currentCalendar);
        SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm");
        String datePrefix = null;
       // Date time = new Date();
        String datePrefix1 = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Boolean bool1 = false;
        Boolean bool2 = false;
        Boolean bool = false;
        String[] times_1 = times.split("\\|");
        String[] times_2 = times_1[0].split(",");
        String[] times_3 = times_1[1].split(",");
        if (dateFormatTime.parse(dateFormatTime.format(calendar.getTime())).after(dateFormatTime.parse(times_2[0]))&&dateFormatTime.parse(dateFormatTime.format(calendar.getTime())).before(dateFormatTime.parse(times_2[1]))){
             datePrefix1 = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH)+1) + " ";
            datePrefix = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " ";
          Date time_2_b = dateFormat.parse(datePrefix + times_2[0]);
          Date time_2_a = dateFormat.parse(datePrefix + times_2[1]);
          if (currentCalendar.getTime().after(time_2_b) && currentCalendar.getTime().before(time_2_a)) {
              bool1 = true;
          }
        Date time_3_b = dateFormat.parse(datePrefix1 + times_3[0]);
        Date time_3_a = dateFormat.parse(datePrefix1 + times_3[1]);
        if (currentCalendar.getTime().after(time_3_b) && currentCalendar.getTime().before(time_3_a)) {
            bool2 = true;
        }

        if (bool1 || bool2){
            bool = true;
        }
        }
        if (dateFormatTime.parse(dateFormatTime.format(calendar.getTime())).after(dateFormatTime.parse(times_3[0]))&&dateFormatTime.parse(dateFormatTime.format(calendar.getTime())).before(dateFormatTime.parse(times_3[1]))){
            datePrefix1 = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " ";
            datePrefix = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH)-1) + " ";
            Date time_2_b = dateFormat.parse(datePrefix + times_2[0]);
            Date time_2_a = dateFormat.parse(datePrefix + times_2[1]);
            if (currentCalendar.getTime().after(time_2_b) && currentCalendar.getTime().before(time_2_a)) {
                bool1 = true;
            }
            Date time_3_b = dateFormat.parse(datePrefix1 + times_3[0]);
            Date time_3_a = dateFormat.parse(datePrefix1 + times_3[1]);
            if (currentCalendar.getTime().after(time_3_b) && currentCalendar.getTime().before(time_3_a)) {
                bool2 = true;
            }

            if (bool1 || bool2){
                bool = true;
            }
        }

        return bool;
    }
}
