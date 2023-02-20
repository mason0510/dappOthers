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
     * 添加申诉
     * @param userAppeal    申诉单信息
     * @return
     */
    @Transactional
    @Override
    public Boolean addAppeal(UserAppeal userAppeal) {

        //获取订单信息
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(userAppeal.getCapitalDetailId());
        if (capitalDetail == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "该订单不存在");
        }

        //判读订单状态
        if (capitalDetail.getStatus().equals(OrderStateEnum.CANCELLED) || capitalDetail.getStatus().equals(OrderStateEnum.COMPLETED)) {
            BizException.fail(MessageCode.ORDER_STATUS_ERR, "订单状态已变更，请重新刷新获取");
        }

        //订单付款时间
        Date time = capitalDetail.getPayTime();
        if (time == null) {
            BizException.fail(MessageCode.APPEAL_TIME_ERR, "付款成功后5分钟才可以发起申诉");
        }

        /*//获取最近的同一用户同一交易的申诉单
        UserAppeal lastUserAppeal = userAppealMapper.selectLastAppealByUserIdAndCapitalDetailId(userAppeal.getUserId(), userAppeal.getCapitalDetailId());
        if (lastUserAppeal != null) {
            //如果已存在申诉单且未处理，则报错
            if (lastUserAppeal.getStatus().equals(AppealStatusEnum.WAIT.getValue())) {
                BizException.fail(ApiResponseCode.DATA_EXIST, "订单已申诉");
            }
            //如果已存在申诉单且已取消，则取其取消时间
            if (lastUserAppeal.getStatus().equals(AppealStatusEnum.CANCEL.getValue())) {
                time = lastUserAppeal.getUpdateTime();
            }
        }*/

        Long nowTime = System.currentTimeMillis();
        //付款成功或取消申诉后5分钟才可以发起申诉
        if (nowTime - time.getTime() < 300000) {
            BizException.fail(MessageCode.APPEAL_TIME_ERR, "付款成功后5分钟才可以发起申诉");
        }

        //判断是否已存在申诉单
        Map<String, Object> params = new HashMap<>();
//        params.put("status", 0);
//        params.put("userId", userAppeal.getUserId());
        params.put("capitalDetailId", userAppeal.getCapitalDetailId());
        List<UserAppeal> appeals = userAppealMapper.selectByParam(params);
        if (appeals != null && appeals.size() > 0) {
            BizException.fail(MessageCode.ALREADY_APPEAL, "订单已申诉");
        }

        //创建新的申诉
        userAppeal.setStatus(0);
        userAppeal.setCreateTime(new Date());
        Integer result1 = userAppealMapper.insert(userAppeal);

        //改变订单状态
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
                //对于卖方进行短信通知
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

                //客服加入群组
                easemobService.addChatGroupsMember(capitalDetail.getEasemobGroupId(), customerService);
            }
        });
        return result1 * result2 > 0;
    }

    /**
     * 获取申诉信息详情
     * @param appealId
     * @return
     */
    @Override
    public UserAppealInfoVO selectAppealInfoById(Long appealId) {
        //获取申诉单信息
        UserAppeal userAppeal = userAppealMapper.selectByPrimaryKey(appealId);
        if (userAppeal == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "该申诉单不存在");
        }
        //获取交易单信息
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(userAppeal.getCapitalDetailId());
        //获取买家卖家信息
        List<Long> userIdList = new ArrayList<>();
        userIdList.add(capitalDetail.getBuyUserId());
        userIdList.add(capitalDetail.getSellUserId());
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);
        UserDTO buyerInfo = userDTOMap.get(capitalDetail.getBuyUserId());
        UserDTO sellerInfo = userDTOMap.get(capitalDetail.getSellUserId());
        //获取商品信息
        Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());

        UserAppealInfoVO infoVO = new UserAppealInfoVO();
        //申诉单信息
        infoVO.setAppealUserId(userAppeal.getUserId());
        infoVO.setAppealUserName(userDTOMap.get(userAppeal.getUserId()).getUserName());
        infoVO.setAppealType(userAppeal.getType());
        infoVO.setAppealReason(userAppeal.getReason());
        infoVO.setAppealStatus(userAppeal.getStatus());
        infoVO.setAppealTime(userAppeal.getCreateTime());

        //买家卖家信息
        infoVO.setBuyUserId(buyerInfo.getUserId());
        infoVO.setBuyUserName(buyerInfo.getUserName());
        infoVO.setBuyUserEmail(buyerInfo.getEmail());
        infoVO.setBuyUserMobile(buyerInfo.getMobile());
        infoVO.setSellUserId(sellerInfo.getUserId());
        infoVO.setSellUserName(sellerInfo.getUserName());
        infoVO.setSellUserEmail(sellerInfo.getEmail());
        infoVO.setSellUserMobile(sellerInfo.getMobile());

        //交易单信息
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
     * 修改申诉单状态
     *
     * @param userAppealId 申诉单ID
     * @param status       状态 0：处理中，1：胜诉 ，2:败诉  3:取消
     * @return
     */
    @Override
    @Transactional
    public void changeAppealStatus(Long userAppealId, Integer status) {

        //申诉状态
        AppealStatusEnum appealStatusEnum = AppealStatusEnum.getInstance(status);
        if (appealStatusEnum == null) {
            BizException.fail(ApiResponseCode.PARA_ERR, "状态不正确");
        }

        //修改申诉单状态
        UserAppeal userAppeal = userAppealMapper.selectByPrimaryKey(userAppealId);
        if (userAppeal == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "无此申诉单");
        }

        userAppeal.setStatus(status);
        userAppeal.setUpdateTime(new Date());
        userAppealMapper.updateByPrimaryKeySelective(userAppeal);

        //获取订单信息
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(userAppeal.getCapitalDetailId());

        OrderStateEnum orderStateEnum;
        Boolean buyerWin;
        //买家胜诉
        if ((capitalDetail.getBuyUserId().equals(userAppeal.getUserId()) && appealStatusEnum.equals(AppealStatusEnum.SUCC)) ||
                (capitalDetail.getSellUserId().equals(userAppeal.getUserId()) && appealStatusEnum.equals(AppealStatusEnum.FAIL))) {
            //放币处理
            exeCurrency(userAppeal.getCapitalDetailId());
            orderStateEnum = OrderStateEnum.COMPLETED;
            buyerWin = true;
        }
        //卖家胜诉
        else {
            orderStateEnum = OrderStateEnum.CANCELLED;
            buyerWin = false;
            Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());
            if (commodity.getStatus() == 0) {
                //--------商品在售---------

                //商品冻结数量恢复
                BigDecimal fee;
                if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL.getValue())) {
                    fee = capitalDetail.getServiceFee();
                } else {
                    fee = BigDecimal.ZERO;
                }
                int res = commodityMapper.updateQuantity(capitalDetail.getCommodityId(), capitalDetail.getTransactionVolume(),
                        capitalDetail.getTransactionVolume().negate(), fee, commodity.getVersion());
                if (res == 0) {
                    BizException.fail(ApiResponseCode.METHOD_UN_IMPL, "操作无效");
                }

                //买单，需返回卖家冻结金额
                if (commodity.getCommodityType() == CommodityTypeEnum.BUY.getValue()) {
                    thawNumber(capitalDetail);
                }
            } else {
                //-------商品已下线----------

                //解冻卖家资金
                thawNumber(capitalDetail);
            }

            //累计用户取消订单数
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

                    //下架用户的商品
                    Map<String, Object> delparams = new HashMap<>();
                    delparams.put("status", 0);
                    delparams.put("userId", capitalDetail.getBuyUserId());
                    List<CommodityInfoVO> list = commodityMapper.selectByParam(delparams);
                    for (CommodityInfoVO item : list) {
                        try {
                            commodityService.delCommodity(item.getCommodityId());
                        } catch (Exception e) {
                            logger.info("下架商品出现问题，商品id为 " + commodity.getCommodityId());
                        }
                    }
                } else {
                    Jedis jedis = redisUtil.getResource();
                    Long time = jedis.ttl(key);
                    redisUtil.setStringValue(key, String.valueOf(Integer.parseInt(count) + 1), time.intValue());
                }
            }

        }

        //修改订单状态
        CapitalDetail detail = new CapitalDetail();
        detail.setCapitalDetailId(userAppeal.getCapitalDetailId());
        detail.setStatus(orderStateEnum.getValue());
        detail.setUpdateTime(new Date());
        capitalDetailMapper.updateByPrimaryKeySelective(detail);

        //--------------买家统计---------------
        CapitalDetailCount buyerCount = capitalDetailCountMapper.selectByUserId(capitalDetail.getBuyUserId());
        //成交次数
        Long totalTradeCount = buyerCount.getTotalTradeCount() + 1;
        buyerCount.setTotalTradeCount(totalTradeCount);
        //成功成交次数
        Long successTradeCount = buyerCount.getSuccessTradeCount();
        if (buyerWin) {
            successTradeCount = successTradeCount + 1;
            buyerCount.setSuccessTradeCount(successTradeCount);
            //申述成功次数
            buyerCount.setSuccessAppealCount(buyerCount.getSuccessAppealCount() + 1);
        }
        //成交率
        buyerCount.setCloseRate(successTradeCount * 100.0 / totalTradeCount);
        buyerCount.setUpdateTime(new Date());
        //申述总数
        buyerCount.setAppealCount(buyerCount.getAppealCount() + 1);
        capitalDetailCountMapper.updateByPrimaryKey(buyerCount);

        //-------------卖家统计-----------------
        CapitalDetailCount sellerCount = capitalDetailCountMapper.selectByUserId(capitalDetail.getSellUserId());
        //成交次数
        totalTradeCount = sellerCount.getTotalTradeCount() + 1;
        sellerCount.setTotalTradeCount(totalTradeCount);
        //成功成交次数
        successTradeCount = sellerCount.getSuccessTradeCount();
        if (!buyerWin) {
            successTradeCount = successTradeCount + 1;
            sellerCount.setSuccessTradeCount(successTradeCount);
            //申述成功次数
            sellerCount.setSuccessAppealCount(sellerCount.getSuccessAppealCount() + 1);
        }
        //成交率
        sellerCount.setCloseRate(successTradeCount * 100.0 / totalTradeCount);
        sellerCount.setUpdateTime(new Date());
        //申述总数
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
                //申述成功给双方发消息
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

//        //删除环信群组
//        easemobService.delChatGroups(capitalDetail.getEasemobGroupId());
    }

    /**
     * 发币处理
     * @param capitalDetailId   交易单ID
     */
    private void exeCurrency(Long capitalDetailId){

        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalDetailId);
        if (capitalDetail == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "不存在此订单");
        }

        Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());

        //判断商户的准备或者购买的数量是否足够
        BigDecimal transactionVolume = capitalDetail.getTransactionVolume();
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

        /*//计算商品的冻结手续费
        if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL)) {
            commodity.setFeeQuantity(commodity.getFeeQuantity().subtract(capitalDetail.getServiceFee()));
        }*/
        //修正商品的属性
        int res = commodityMapper.updateByPrimaryKeySelective(commodity);

        if (res == 0){
            BizException.fail(ApiResponseCode.METHOD_UN_IMPL,null);
        }
        //对于用户之间的资金进行转移
        Map<String, Object> params = new HashMap<>();
        params.put("userId", capitalDetail.getSellUserId());
        params.put("currencyId", commodity.getCurrencyId());
        List<CapitalInfoVO> capitalInfoVOList = capitalMapper.selectByParam(params);
        CapitalInfoVO sellUserIdCapital = capitalInfoVOList.size() > 0 ? capitalInfoVOList.get(0) : null;
        params.put("userId", capitalDetail.getBuyUserId());
        capitalInfoVOList = capitalMapper.selectByParam(params);
        CapitalInfoVO buyUserIdCapital = capitalInfoVOList.size() > 0 ? capitalInfoVOList.get(0) : null;

        //卖家支付的金额
        BigDecimal sellMoney;
        if (capitalDetail.getCommodityType() == CommodityTypeEnum.SELL.getValue()) {
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
        if (capitalDetail.getCommodityType() == CommodityTypeEnum.SELL.getValue()) {
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
    }

    /**
     * 取消买订单或商品已取消时，解冻卖家的资金
     */
    private void thawNumber(CapitalDetail capitalDetail){

        Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());
        if (commodity.getCommodityType().equals(CommodityTypeEnum.BUY.getValue()) || commodity.getStatus().equals(1)) {
            //获取卖家资产信息
            Map<String, Object> capitalParams = new HashMap<>();
            capitalParams.put("userId", capitalDetail.getSellUserId());
            capitalParams.put("currencyId", commodity.getCurrencyId());
            List<CapitalInfoVO> capitalInfoVOList = capitalMapper.selectByParam(capitalParams);
            if (capitalInfoVOList != null && capitalInfoVOList.size() > 0) {
                CapitalInfoVO sellerCapitalInfo = capitalInfoVOList.get(0);

                //解冻卖家资产
                BigDecimal sellMoney = capitalDetail.getTransactionVolume();
                if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL.getValue()) && commodity.getStatus().equals(1)) {
                    //卖单商品已下线时，订单冻结金+手续费 解冻
                    sellMoney = sellMoney.add(capitalDetail.getServiceFee());
                }
                CapitalInfoVO updateCapital = new CapitalInfoVO();
                updateCapital.setCapitalId(sellerCapitalInfo.getCapitalId());
                updateCapital.setCapitalAvailable(sellerCapitalInfo.getCapitalAvailable().add(sellMoney));
                updateCapital.setCapitalFrozen(sellerCapitalInfo.getCapitalFrozen().subtract(sellMoney));
                updateCapital.setVersion(sellerCapitalInfo.getVersion());
                capitalMapper.updateByPrimaryKeySelective(updateCapital);

                //记录资产变化
                CapitalLog capitalLog = new CapitalLog();
                capitalLog.setUserId(capitalDetail.getSellUserId());
                capitalLog.setType(CapitalLogTypeEnum.THAW.getValue().byteValue());
                capitalLog.setCurrencyId(commodity.getCurrencyId());
                capitalLog.setAvailableNumber(sellMoney);
                capitalLog.setFrozenNumber(sellMoney.negate());
                capitalLog.setComment("卖出订单取消，订单号:" + capitalDetail.getCapitalDetailId());
                capitalLog.setOrderId(capitalDetail.getCapitalDetailId());
                capitalLog.setCreateTime(new Date());
                capitalLogMapper.insertSelective(capitalLog);
            }
        }
    }

    /**
     * 取消申诉
     * @param userAppealId  申诉ID
     * @param userId        用户ID
     * @return
     */
    @Override
    public Boolean cancelAppeal(Long userAppealId, Long userId) {
        //判断是否为自己的取消单
        UserAppeal userAppeal = userAppealMapper.selectByPrimaryKey(userAppealId);
        if (userAppeal == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "该申诉单不存在");
        }
        if (!userId.equals(userAppeal.getUserId())) {
            BizException.fail(MessageCode.NO_AUTHORITY, "这不是您的申诉单");
        }
        //取消申诉
        userAppeal.setStatus(AppealStatusEnum.CANCEL.getValue());    //取消状态
        userAppeal.setUpdateTime(new Date());
        Integer result = userAppealMapper.updateByPrimaryKeySelective(userAppeal);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //获取订单信息
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
                //对于卖方进行短信通知
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
     * 获取申诉单列表
     * @param searchVO
     * @return
     */
    @Override
    public PageInfo<UserAppealInfoVO> getPageList(UserAppealSearchVO searchVO) {
        //获取申述单信息
        Map<String, Object> params = new HashMap<>();
        params.put("status",searchVO.getStatus());
        params.put("orderNumber", searchVO.getOrderNumber());
        PageHelper.startPage(searchVO.getCurrentPage(), searchVO.getPageSize(), Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<UserAppealInfoVO> list = userAppealMapper.selectInfoByParam(params);

        //获取买家卖家信息
        Set<Long> userIds = new HashSet<>();
        for (UserAppealInfoVO item : list) {
            userIds.add(item.getBuyUserId());
            userIds.add(item.getSellUserId());
        }
        List<Long> userList = new ArrayList<>(userIds);
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userList);
        //设置用户买家卖家信息
        for (UserAppealInfoVO item : list) {
            UserDTO user = userDTOMap.get(item.getAppealUserId());
            UserDTO buyer = userDTOMap.get(item.getBuyUserId());
            UserDTO seller = userDTOMap.get(item.getSellUserId());
            //用户
            item.setAppealUserName(user.getUserName());
            //买家
            item.setBuyUserName(buyer.getUserName());
            item.setBuyUserMobile(buyer.getMobile());
            item.setBuyUserEmail(buyer.getEmail());
            //卖家
            item.setSellUserName(seller.getUserName());
            item.setSellUserMobile(seller.getMobile());
            item.setSellUserEmail(seller.getEmail());
        }

        PageInfo<UserAppealInfoVO> pageInfo = new PageInfo(list);
        return pageInfo;
    }
}
