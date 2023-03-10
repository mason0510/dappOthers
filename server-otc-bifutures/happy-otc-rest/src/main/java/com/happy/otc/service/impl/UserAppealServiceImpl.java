package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.bitan.message.enums.OtcMessageEnum;
import com.bitan.message.vo.SendOtcMessageRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happy.otc.contants.Contants;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dao.*;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.*;
import com.happy.otc.enums.*;
import com.happy.otc.service.*;
import com.happy.otc.service.remote.IMessageService;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.vo.CapitalInfoVO;
import com.happy.otc.vo.CommodityInfoVO;
import com.happy.otc.vo.UserAppealInfoVO;
import com.happy.otc.vo.manager.UserAppealSearchVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.*;

@Service
public class UserAppealServiceImpl implements IUserAppealService {

    private static final Logger logger = LoggerFactory.getLogger(UserAppealServiceImpl.class);
    @Autowired
    private UserAppealMapper userAppealMapper;
    @Autowired
    private CapitalDetailMapper capitalDetailMapper;
    @Autowired
    private CommodityMapper commodityMapper;
    @Autowired
    private IOauthService oauthService;
    @Autowired
    private CapitalMapper capitalMapper;
    @Autowired
    private CapitalDetailCountMapper capitalDetailCountMapper;
    @Autowired
    private IEasemobService easemobService;
    @Autowired
    private CapitalLogMapper capitalLogMapper;
    @Autowired
    IMessageService messageService;
    @Autowired
    IUserIdentityService userIdentityService;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserMonitorService userMonitorService;
    @Autowired
    private ICommodityService commodityService;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${easemob.customerService.username}")
    private String customerService;

    /**
     * ????????????
     * @param userAppeal    ???????????????
     * @return
     */
    @Transactional
    @Override
    public Boolean addAppeal(UserAppeal userAppeal) {

        //??????????????????
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(userAppeal.getCapitalDetailId());
        if (capitalDetail == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "??????????????????");
        }

        //??????????????????
        if (capitalDetail.getStatus().equals(OrderStateEnum.CANCELLED) || capitalDetail.getStatus().equals(OrderStateEnum.COMPLETED)) {
            BizException.fail(MessageCode.ORDER_STATUS_ERR, "?????????????????????????????????????????????");
        }

        //??????????????????
        Date time = capitalDetail.getPayTime();
        if (time == null) {
            BizException.fail(MessageCode.APPEAL_TIME_ERR, "???????????????5???????????????????????????");
        }

        /*//???????????????????????????????????????????????????
        UserAppeal lastUserAppeal = userAppealMapper.selectLastAppealByUserIdAndCapitalDetailId(userAppeal.getUserId(), userAppeal.getCapitalDetailId());
        if (lastUserAppeal != null) {
            //????????????????????????????????????????????????
            if (lastUserAppeal.getStatus().equals(AppealStatusEnum.WAIT.getValue())) {
                BizException.fail(ApiResponseCode.DATA_EXIST, "???????????????");
            }
            //????????????????????????????????????????????????????????????
            if (lastUserAppeal.getStatus().equals(AppealStatusEnum.CANCEL.getValue())) {
                time = lastUserAppeal.getUpdateTime();
            }
        }*/

        Long nowTime = System.currentTimeMillis();
        //??????????????????????????????5???????????????????????????
        if (nowTime - time.getTime() < 300000) {
            BizException.fail(MessageCode.APPEAL_TIME_ERR, "???????????????5???????????????????????????");
        }

        //??????????????????????????????
        Map<String, Object> params = new HashMap<>();
//        params.put("status", 0);
//        params.put("userId", userAppeal.getUserId());
        params.put("capitalDetailId", userAppeal.getCapitalDetailId());
        List<UserAppeal> appeals = userAppealMapper.selectByParam(params);
        if (appeals != null && appeals.size() > 0) {
            BizException.fail(MessageCode.ALREADY_APPEAL, "???????????????");
        }

        //??????????????????
        userAppeal.setStatus(0);
        userAppeal.setCreateTime(new Date());
        Integer result1 = userAppealMapper.insert(userAppeal);

        //??????????????????
        CapitalDetail newCapitalDetail = new CapitalDetail();
        newCapitalDetail.setCapitalDetailId(capitalDetail.getCapitalDetailId());
        newCapitalDetail.setStatus(OrderStateEnum.APPEAL.getValue());
        newCapitalDetail.setUpdateTime(new Date());
        Integer result2 = capitalDetailMapper.updateByPrimaryKeySelective(newCapitalDetail);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Long appelleeUserId = -1L;
                Long otherUserId = -1L;
                LanguageEnum languageEnum = LanguageEnum.CHINESE;
                if (userAppeal.getUserId().compareTo(capitalDetail.getBuyUserId()) == 0){
                    appelleeUserId = capitalDetail.getSellUserId();
                    otherUserId = capitalDetail.getBuyUserId();
                }else {
                    appelleeUserId = capitalDetail.getBuyUserId();
                    otherUserId = capitalDetail.getSellUserId();
                }

                List<Long> userIdList = new ArrayList<>();
                userIdList.add(capitalDetail.getBuyUserId());
                userIdList.add(capitalDetail.getSellUserId());
                Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);
                //??????????????????????????????
                UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(appelleeUserId);

                if (!Objects.isNull(userIdentity.getLanguageType())){
                    languageEnum = LanguageEnum.getInstance(Integer.valueOf(userIdentity.getLanguageType()));
                    if (Objects.isNull(languageEnum)) {
                        languageEnum = LanguageEnum.CHINESE;
                    }
                }
                SendOtcMessageRequest sendOtcMessageRequest = new SendOtcMessageRequest();
                sendOtcMessageRequest.setAccount(userDTOMap.get(appelleeUserId).getCountryCode()+userDTOMap.get(appelleeUserId).getMobile());
                sendOtcMessageRequest.setOrderNumber(capitalDetail.getOrderNumber());
                sendOtcMessageRequest.setUserName(userDTOMap.get(otherUserId).getUserName());
                sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.SEND_APPEL.getValue());
                sendOtcMessageRequest.setAppType(languageEnum.getValue());
                try {
                    messageService.sendOtcMessage(sendOtcMessageRequest);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put( "userId", userAppeal.getUserId());
                    jsonObject.put( "type",  userAppeal.getType());
                    jsonObject.put( "status",  userAppeal.getStatus());
                    jsonObject.put( "reason",  userAppeal.getReason());
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put( "data",  jsonObject);
                    jsonObject2.put( "code",  MessageCode.ORDER_APPEAL);
                    easemobService.sendOrderMessage( null, capitalDetail.getEasemobGroupId(),JSONObject.toJSONString(jsonObject2),null);
                }catch (Exception e){

                }

                //??????????????????
                easemobService.addChatGroupsMember(capitalDetail.getEasemobGroupId(), customerService);
            }
        });
        return result1 * result2 > 0;
    }

    /**
     * ????????????????????????
     * @param appealId
     * @return
     */
    @Override
    public UserAppealInfoVO selectAppealInfoById(Long appealId) {
        //?????????????????????
        UserAppeal userAppeal = userAppealMapper.selectByPrimaryKey(appealId);
        if (userAppeal == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "?????????????????????");
        }
        //?????????????????????
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(userAppeal.getCapitalDetailId());
        //????????????????????????
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(capitalDetail.getBuyUserId());
        userIdList.add(capitalDetail.getSellUserId());
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);
        UserDTO buyerInfo = userDTOMap.get(capitalDetail.getBuyUserId());
        UserDTO sellerInfo = userDTOMap.get(capitalDetail.getSellUserId());
        //??????????????????
        Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());

        UserAppealInfoVO infoVO = new UserAppealInfoVO();
        //???????????????
        infoVO.setAppealUserId(userAppeal.getUserId());
        infoVO.setAppealUserName(userDTOMap.get(userAppeal.getUserId()).getUserName());
        infoVO.setAppealType(userAppeal.getType());
        infoVO.setAppealReason(userAppeal.getReason());
        infoVO.setAppealStatus(userAppeal.getStatus());
        infoVO.setAppealTime(userAppeal.getCreateTime());

        //??????????????????
        infoVO.setBuyUserId(buyerInfo.getUserId());
        infoVO.setBuyUserName(buyerInfo.getUserName());
        infoVO.setBuyUserEmail(buyerInfo.getEmail());
        infoVO.setBuyUserMobile(buyerInfo.getMobile());
        infoVO.setSellUserId(sellerInfo.getUserId());
        infoVO.setSellUserName(sellerInfo.getUserName());
        infoVO.setSellUserEmail(sellerInfo.getEmail());
        infoVO.setSellUserMobile(sellerInfo.getMobile());

        //???????????????
        infoVO.setCommodityId(capitalDetail.getCommodityId());
        infoVO.setCurrencyPrice(commodity.getCurrencyPrice());
        infoVO.setKind(commodity.getKind());
        infoVO.setCommodityType(commodity.getCommodityType());
        infoVO.setCapitalDetailId(capitalDetail.getCapitalDetailId());
        infoVO.setTransactionAmount(capitalDetail.getTransactionAmount());
        infoVO.setTransactionVolume(capitalDetail.getTransactionVolume());
        infoVO.setPayTime(capitalDetail.getPayTime());
        infoVO.setOrderNumber(capitalDetail.getOrderNumber());

        return infoVO;
    }

    /**
     * ?????????????????????
     *
     * @param userAppealId ?????????ID
     * @param status       ?????? 0???????????????1????????? ???2:??????  3:??????
     * @return
     */
    @Override
    @Transactional
    public void changeAppealStatus(Long userAppealId, Integer status) {

        //????????????
        AppealStatusEnum appealStatusEnum = AppealStatusEnum.getInstance(status);
        if (appealStatusEnum == null) {
            BizException.fail(ApiResponseCode.PARA_ERR, "???????????????");
        }

        //?????????????????????
        UserAppeal userAppeal = userAppealMapper.selectByPrimaryKey(userAppealId);
        if (userAppeal == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "???????????????");
        }

        userAppeal.setStatus(status);
        userAppeal.setUpdateTime(new Date());
        userAppealMapper.updateByPrimaryKeySelective(userAppeal);

        //??????????????????
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(userAppeal.getCapitalDetailId());

        OrderStateEnum orderStateEnum;
        Boolean buyerWin;
        //????????????
        if ((capitalDetail.getBuyUserId().equals(userAppeal.getUserId()) && appealStatusEnum.equals(AppealStatusEnum.SUCC)) ||
                (capitalDetail.getSellUserId().equals(userAppeal.getUserId()) && appealStatusEnum.equals(AppealStatusEnum.FAIL))) {
            //????????????
            exeCurrency(userAppeal.getCapitalDetailId());
            orderStateEnum = OrderStateEnum.COMPLETED;
            buyerWin = true;
        }
        //????????????
        else {
            orderStateEnum = OrderStateEnum.CANCELLED;
            buyerWin = false;
            Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());
            if (commodity.getStatus() == 0) {
                //--------????????????---------

                //????????????????????????
                BigDecimal fee;
                if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL.getValue())) {
                    fee = capitalDetail.getServiceFee();
                } else {
                    fee = BigDecimal.ZERO;
                }
                int res = commodityMapper.updateQuantity(capitalDetail.getCommodityId(), capitalDetail.getTransactionVolume(),
                        capitalDetail.getTransactionVolume().negate(), fee, commodity.getVersion());
                if (res == 0) {
                    BizException.fail(ApiResponseCode.METHOD_UN_IMPL, "????????????");
                }

                //????????????????????????????????????
                if (commodity.getCommodityType() == CommodityTypeEnum.BUY.getValue()) {
                    thawNumber(capitalDetail);
                }
            } else {
                //-------???????????????----------

                //??????????????????
                thawNumber(capitalDetail);
            }

            //???????????????????????????
            String key = Contants.REDIS_USER_TRADE_CANCLE + capitalDetail.getBuyUserId();
            String count = redisUtil.getStringValue(key);
            if (StringUtils.isEmpty(count)) {
                redisUtil.setStringValue(key, "1", 24 * 60 * 60);
            } else {
                if (count.compareTo("2") == 0) {
                    redisUtil.setStringValue(key, String.valueOf(Integer.parseInt(count) + 1), 24 * 60 * 60);
                    UserMonitor userMonitor = new UserMonitor();
                    userMonitor.setUserId(capitalDetail.getBuyUserId());
                    userMonitor.setType(1);
                    userMonitor.setCreateTime(new Date());
                    userMonitorService.addUserMonitor(userMonitor);

                    //?????????????????????
                    Map<String, Object> delparams = new HashMap<>();
                    delparams.put("status", 0);
                    delparams.put("userId", capitalDetail.getBuyUserId());
                    List<CommodityInfoVO> list = commodityMapper.selectByParam(delparams);
                    for (CommodityInfoVO item : list) {
                        try {
                            commodityService.delCommodity(item.getCommodityId());
                        } catch (Exception e) {
                            logger.info("?????????????????????????????????id??? " + commodity.getCommodityId());
                        }
                    }
                } else {
                    Jedis jedis = redisUtil.getResource();
                    Long time = jedis.ttl(key);
                    redisUtil.setStringValue(key, String.valueOf(Integer.parseInt(count) + 1), time.intValue());
                }
            }

        }

        //??????????????????
        CapitalDetail detail = new CapitalDetail();
        detail.setCapitalDetailId(userAppeal.getCapitalDetailId());
        detail.setStatus(orderStateEnum.getValue());
        detail.setUpdateTime(new Date());
        capitalDetailMapper.updateByPrimaryKeySelective(detail);

        //--------------????????????---------------
        CapitalDetailCount buyerCount = capitalDetailCountMapper.selectByUserId(capitalDetail.getBuyUserId());
        //????????????
        Long totalTradeCount = buyerCount.getTotalTradeCount() + 1;
        buyerCount.setTotalTradeCount(totalTradeCount);
        //??????????????????
        Long successTradeCount = buyerCount.getSuccessTradeCount();
        if (buyerWin) {
            successTradeCount = successTradeCount + 1;
            buyerCount.setSuccessTradeCount(successTradeCount);
            //??????????????????
            buyerCount.setSuccessAppealCount(buyerCount.getSuccessAppealCount() + 1);
        }
        //?????????
        buyerCount.setCloseRate(successTradeCount * 100.0 / totalTradeCount);
        buyerCount.setUpdateTime(new Date());
        //????????????
        buyerCount.setAppealCount(buyerCount.getAppealCount() + 1);
        capitalDetailCountMapper.updateByPrimaryKey(buyerCount);

        //-------------????????????-----------------
        CapitalDetailCount sellerCount = capitalDetailCountMapper.selectByUserId(capitalDetail.getSellUserId());
        //????????????
        totalTradeCount = sellerCount.getTotalTradeCount() + 1;
        sellerCount.setTotalTradeCount(totalTradeCount);
        //??????????????????
        successTradeCount = sellerCount.getSuccessTradeCount();
        if (!buyerWin) {
            successTradeCount = successTradeCount + 1;
            sellerCount.setSuccessTradeCount(successTradeCount);
            //??????????????????
            sellerCount.setSuccessAppealCount(sellerCount.getSuccessAppealCount() + 1);
        }
        //?????????
        sellerCount.setCloseRate(successTradeCount * 100.0 / totalTradeCount);
        sellerCount.setUpdateTime(new Date());
        //????????????
        sellerCount.setAppealCount(sellerCount.getAppealCount() + 1);
        capitalDetailCountMapper.updateByPrimaryKey(sellerCount);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Long> userIdList = new ArrayList<>();
                userIdList.add(capitalDetail.getBuyUserId());
                userIdList.add(capitalDetail.getSellUserId());
                Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);
                LanguageEnum buyLanguageEnum = LanguageEnum.CHINESE;
                UserIdentity  buyerIdentity = userIdentityService.getUserExtendIdentity(capitalDetail.getBuyUserId());
                if (!Objects.isNull(buyerIdentity.getLanguageType())){
                    buyLanguageEnum = LanguageEnum.getInstance(Integer.valueOf(buyerIdentity.getLanguageType()));
                    if (Objects.isNull(buyLanguageEnum)) {
                        buyLanguageEnum = LanguageEnum.CHINESE;
                    }
                }
                LanguageEnum sellLanguageEnum = LanguageEnum.CHINESE;
                UserIdentity  sellIdentity = userIdentityService.getUserExtendIdentity(capitalDetail.getSellUserId());
                if (!Objects.isNull(sellIdentity.getLanguageType())){
                    sellLanguageEnum = LanguageEnum.getInstance(Integer.valueOf(sellIdentity.getLanguageType()));
                    if (Objects.isNull(sellLanguageEnum)) {
                        sellLanguageEnum = LanguageEnum.CHINESE;
                    }
                }
                //??????????????????????????????
                if (appealStatusEnum.equals(AppealStatusEnum.SUCC)){
                    Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());
                    SendOtcMessageRequest sendOtcMessageRequest = new SendOtcMessageRequest();
                    sendOtcMessageRequest.setAccount(userDTOMap.get(capitalDetail.getBuyUserId()).getCountryCode() + userDTOMap.get(capitalDetail.getBuyUserId()).getMobile());
                    sendOtcMessageRequest.setOrderNumber(capitalDetail.getOrderNumber());
                    sendOtcMessageRequest.setCoins(capitalDetail.getTransactionVolume().setScale(6, BigDecimal.ROUND_DOWN).toString());
                    if (userAppeal.getUserId().equals(capitalDetail.getBuyUserId())){
                        sendOtcMessageRequest.setUserName(userDTOMap.get(capitalDetail.getBuyUserId()).getUserName());
                    }else {
                        sendOtcMessageRequest.setUserName(userDTOMap.get(capitalDetail.getSellUserId()).getUserName());
                    }
                    sendOtcMessageRequest.setKind(commodity.getKind());
                    sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.APPEL_SUCCESS.getValue());
                    sendOtcMessageRequest.setAppType(buyLanguageEnum.getValue());
                    try {
                        messageService.sendOtcMessage(sendOtcMessageRequest);
                        sendOtcMessageRequest.setAccount(userDTOMap.get(capitalDetail.getSellUserId()).getCountryCode() + userDTOMap.get(capitalDetail.getSellUserId()).getMobile());
                        sendOtcMessageRequest.setAppType(sellLanguageEnum.getValue());
                        messageService.sendOtcMessage(sendOtcMessageRequest);
                    }catch (Exception e){

                    }

                }else{
                    SendOtcMessageRequest sendOtcMessageRequest = new SendOtcMessageRequest();
                    sendOtcMessageRequest.setAccount(userDTOMap.get(capitalDetail.getBuyUserId()).getCountryCode() + userDTOMap.get(capitalDetail.getBuyUserId()).getMobile());
                    sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.APPEL_BUYER_FAIL.getValue());
                    sendOtcMessageRequest.setOrderNumber(capitalDetail.getOrderNumber());
                    sendOtcMessageRequest.setAppType(buyLanguageEnum.getValue());
                    try {
                        messageService.sendOtcMessage(sendOtcMessageRequest);
                        sendOtcMessageRequest.setAccount(userDTOMap.get(capitalDetail.getSellUserId()).getCountryCode() + userDTOMap.get(capitalDetail.getSellUserId()).getMobile());
                        sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.APPEL_SELLER_FAIL.getValue());
                        sendOtcMessageRequest.setAppType(sellLanguageEnum.getValue());
                        messageService.sendOtcMessage(sendOtcMessageRequest);
                    }catch (Exception e){

                    }
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put( "userId", userAppeal.getUserId());
                jsonObject.put( "type",  userAppeal.getType());
                jsonObject.put( "status",  userAppeal.getStatus());
                jsonObject.put( "reason",  userAppeal.getReason());
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put( "data",  jsonObject);
                jsonObject2.put( "code",  MessageCode.ORDER_APPEAL);
                easemobService.sendOrderMessage( null, capitalDetail.getEasemobGroupId(),JSONObject.toJSONString(jsonObject2),null);

            }
        });

//        //??????????????????
//        easemobService.delChatGroups(capitalDetail.getEasemobGroupId());
    }

    /**
     * ????????????
     * @param capitalDetailId   ?????????ID
     */
    private void exeCurrency(Long capitalDetailId){

        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalDetailId);
        if (capitalDetail == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "??????????????????");
        }

        Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());

        //??????????????????????????????????????????????????????
        BigDecimal transactionVolume = capitalDetail.getTransactionVolume();
        if (commodity.getFrozenQuantity().compareTo(transactionVolume) < 0){
            BizException.fail(MessageCode.NOT_ENOUGH_FROZEN_GOODS,"???????????????????????????");
        }
        commodity.setFrozenQuantity(commodity.getFrozenQuantity().subtract(
                transactionVolume));
        //????????????????????????????????????
        if (commodity.getCurrencyQuantity().compareTo(BigDecimal.ZERO) == 0 &&
                commodity.getFrozenQuantity().compareTo(BigDecimal.ZERO) == 0) {
            commodity.setStatus(1);
        }
        commodity.setUpdateTime(new Date());

        /*//??????????????????????????????
        if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL)) {
            commodity.setFeeQuantity(commodity.getFeeQuantity().subtract(capitalDetail.getServiceFee()));
        }*/
        //?????????????????????
        int res = commodityMapper.updateByPrimaryKeySelective(commodity);

        if (res == 0){
            BizException.fail(ApiResponseCode.METHOD_UN_IMPL,null);
        }
        //???????????????????????????????????????
        Map<String, Object> params = new HashMap<>();
        params.put("userId", capitalDetail.getSellUserId());
        params.put("currencyId", commodity.getCurrencyId());
        List<CapitalInfoVO> capitalInfoVOList = capitalMapper.selectByParam(params);
        CapitalInfoVO sellUserIdCapital = capitalInfoVOList.size() > 0 ? capitalInfoVOList.get(0) : null;
        params.put("userId", capitalDetail.getBuyUserId());
        capitalInfoVOList = capitalMapper.selectByParam(params);
        CapitalInfoVO buyUserIdCapital = capitalInfoVOList.size() > 0 ? capitalInfoVOList.get(0) : null;

        //?????????????????????
        BigDecimal sellMoney;
        if (capitalDetail.getCommodityType() == CommodityTypeEnum.SELL.getValue()) {
            //??????,???????????????????????????+?????????
            sellMoney = transactionVolume.add(capitalDetail.getServiceFee());
        } else {
            //???????????????????????????????????????
            sellMoney = transactionVolume;
        }

        //?????????????????????????????????????????????
        if (sellUserIdCapital.getCapitalFrozen().compareTo(sellMoney) < 0) {
            BizException.fail(MessageCode.NOT_ENOUGH_FROZEN_MONEY, "??????????????????");
        }
        sellUserIdCapital.setCapitalFrozen(sellUserIdCapital.getCapitalFrozen().subtract(sellMoney));
        capitalMapper.updateByPrimaryKeySelective(sellUserIdCapital);

        //????????????????????????
        CapitalLog capitalLog = new CapitalLog();
        capitalLog.setUserId(capitalDetail.getSellUserId());
        capitalLog.setType(CapitalLogTypeEnum.SELL.getValue().byteValue());
        capitalLog.setCurrencyId(commodity.getCurrencyId());
        capitalLog.setFrozenNumber(sellMoney.negate());
        capitalLog.setComment("????????????????????????:" + capitalDetailId);
        capitalLog.setOrderId(capitalDetailId);
        capitalLog.setCreateTime(new Date());
        capitalLogMapper.insertSelective(capitalLog);

        //?????????????????????
        BigDecimal buyMoney;
        if (capitalDetail.getCommodityType() == CommodityTypeEnum.SELL.getValue()) {
            //??????,????????????????????????
            buyMoney = transactionVolume;
        } else {
            //?????????????????????????????????-?????????
            buyMoney = transactionVolume.subtract(capitalDetail.getServiceFee());
        }

        //???????????????????????????????????????
        buyUserIdCapital.setCapitalAvailable(buyUserIdCapital.getCapitalAvailable().add(buyMoney));
        capitalMapper.updateByPrimaryKeySelective(buyUserIdCapital);

        //????????????????????????
        capitalLog = new CapitalLog();
        capitalLog.setUserId(capitalDetail.getBuyUserId());
        capitalLog.setType(CapitalLogTypeEnum.BUY.getValue().byteValue());
        capitalLog.setCurrencyId(commodity.getCurrencyId());
        capitalLog.setAvailableNumber(buyMoney);
        capitalLog.setComment("????????????????????????:" + capitalDetailId);
        capitalLog.setOrderId(capitalDetailId);
        capitalLog.setCreateTime(new Date());
        capitalLogMapper.insertSelective(capitalLog);
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    private void thawNumber(CapitalDetail capitalDetail){

        Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());
        if (commodity.getCommodityType().equals(CommodityTypeEnum.BUY.getValue()) || commodity.getStatus().equals(1)) {
            //????????????????????????
            Map<String, Object> capitalParams = new HashMap<>();
            capitalParams.put("userId", capitalDetail.getSellUserId());
            capitalParams.put("currencyId", commodity.getCurrencyId());
            List<CapitalInfoVO> capitalInfoVOList = capitalMapper.selectByParam(capitalParams);
            if (capitalInfoVOList != null && capitalInfoVOList.size() > 0) {
                CapitalInfoVO sellerCapitalInfo = capitalInfoVOList.get(0);

                //??????????????????
                BigDecimal sellMoney = capitalDetail.getTransactionVolume();
                if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL.getValue()) && commodity.getStatus().equals(1)) {
                    //??????????????????????????????????????????+????????? ??????
                    sellMoney = sellMoney.add(capitalDetail.getServiceFee());
                }
                CapitalInfoVO updateCapital = new CapitalInfoVO();
                updateCapital.setCapitalId(sellerCapitalInfo.getCapitalId());
                updateCapital.setCapitalAvailable(sellerCapitalInfo.getCapitalAvailable().add(sellMoney));
                updateCapital.setCapitalFrozen(sellerCapitalInfo.getCapitalFrozen().subtract(sellMoney));
                updateCapital.setVersion(sellerCapitalInfo.getVersion());
                capitalMapper.updateByPrimaryKeySelective(updateCapital);

                //??????????????????
                CapitalLog capitalLog = new CapitalLog();
                capitalLog.setUserId(capitalDetail.getSellUserId());
                capitalLog.setType(CapitalLogTypeEnum.THAW.getValue().byteValue());
                capitalLog.setCurrencyId(commodity.getCurrencyId());
                capitalLog.setAvailableNumber(sellMoney);
                capitalLog.setFrozenNumber(sellMoney.negate());
                capitalLog.setComment("??????????????????????????????:" + capitalDetail.getCapitalDetailId());
                capitalLog.setOrderId(capitalDetail.getCapitalDetailId());
                capitalLog.setCreateTime(new Date());
                capitalLogMapper.insertSelective(capitalLog);
            }
        }
    }

    /**
     * ????????????
     * @param userAppealId  ??????ID
     * @param userId        ??????ID
     * @return
     */
    @Override
    public Boolean cancelAppeal(Long userAppealId, Long userId) {
        //?????????????????????????????????
        UserAppeal userAppeal = userAppealMapper.selectByPrimaryKey(userAppealId);
        if (userAppeal == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "?????????????????????");
        }
        if (!userId.equals(userAppeal.getUserId())) {
            BizException.fail(MessageCode.NO_AUTHORITY, "????????????????????????");
        }
        //????????????
        userAppeal.setStatus(AppealStatusEnum.CANCEL.getValue());    //????????????
        userAppeal.setUpdateTime(new Date());
        Integer result = userAppealMapper.updateByPrimaryKeySelective(userAppeal);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //??????????????????
                CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(userAppeal.getCapitalDetailId());
                Long appelleeUserId = -1L;
                Long otherUserId = -1L;
                LanguageEnum languageEnum = LanguageEnum.CHINESE;
                if (userAppeal.getUserId().compareTo(capitalDetail.getBuyUserId()) == 0){
                    appelleeUserId = capitalDetail.getSellUserId();
                    otherUserId = capitalDetail.getBuyUserId();
                }else {
                    appelleeUserId = capitalDetail.getBuyUserId();
                    otherUserId = capitalDetail.getSellUserId();
                }

                List<Long> userIdList = new ArrayList<>();
                userIdList.add(capitalDetail.getBuyUserId());
                userIdList.add(capitalDetail.getSellUserId());
                Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);
                //??????????????????????????????
                UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(appelleeUserId);
                if (!Objects.isNull(userIdentity.getLanguageType())){
                    languageEnum = LanguageEnum.getInstance(Integer.valueOf(userIdentity.getLanguageType()));
                    if (Objects.isNull(languageEnum)) {
                        languageEnum = LanguageEnum.CHINESE;
                    }
                }
                SendOtcMessageRequest sendOtcMessageRequest = new SendOtcMessageRequest();
                sendOtcMessageRequest.setAccount(userDTOMap.get(appelleeUserId).getCountryCode() +userDTOMap.get(appelleeUserId).getMobile());
                sendOtcMessageRequest.setOrderNumber(capitalDetail.getOrderNumber());
                sendOtcMessageRequest.setUserName(userDTOMap.get(otherUserId).getUserName());
                sendOtcMessageRequest.setMessageTemplate(OtcMessageEnum.APPEL_CANCELLED.getValue());
                sendOtcMessageRequest.setAppType(languageEnum.getValue());
                try {
                    messageService.sendOtcMessage(sendOtcMessageRequest);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put( "userId", userAppeal.getUserId());
                    jsonObject.put( "type",  userAppeal.getType());
                    jsonObject.put( "status",  userAppeal.getStatus());
                    jsonObject.put( "reason",  userAppeal.getReason());
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put( "data",  jsonObject);
                    jsonObject2.put( "code",  MessageCode.ORDER_APPEAL);
                    easemobService.sendOrderMessage( null, capitalDetail.getEasemobGroupId(),JSONObject.toJSONString(jsonObject2),null);
                }catch (Exception e){

                }
            }
        });
        return result > 0;
    }

    /**
     * ?????????????????????
     * @param searchVO
     * @return
     */
    @Override
    public PageInfo<UserAppealInfoVO> getPageList(UserAppealSearchVO searchVO) {
        //?????????????????????
        Map<String, Object> params = new HashMap<>();
        params.put("status",searchVO.getStatus());
        params.put("orderNumber", searchVO.getOrderNumber());
        PageHelper.startPage(searchVO.getCurrentPage(), searchVO.getPageSize(), Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<UserAppealInfoVO> list = userAppealMapper.selectInfoByParam(params);

        //????????????????????????
        Set<Long> userIds = new HashSet<>();
        for (UserAppealInfoVO item : list) {
            userIds.add(item.getBuyUserId());
            userIds.add(item.getSellUserId());
        }
        List<Long> userList = new ArrayList<>(userIds);
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userList);
        //??????????????????????????????
        for (UserAppealInfoVO item : list) {
            UserDTO user = userDTOMap.get(item.getAppealUserId());
            UserDTO buyer = userDTOMap.get(item.getBuyUserId());
            UserDTO seller = userDTOMap.get(item.getSellUserId());
            //??????
            item.setAppealUserName(user.getUserName());
            //??????
            item.setBuyUserName(buyer.getUserName());
            item.setBuyUserMobile(buyer.getMobile());
            item.setBuyUserEmail(buyer.getEmail());
            //??????
            item.setSellUserName(seller.getUserName());
            item.setSellUserMobile(seller.getMobile());
            item.setSellUserEmail(seller.getEmail());
        }

        PageInfo<UserAppealInfoVO> pageInfo = new PageInfo(list);
        return pageInfo;
    }
}
