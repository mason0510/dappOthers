package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happy.otc.contants.Contants;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dao.CapitalDetailMapper;
import com.happy.otc.dao.CapitalLogMapper;
import com.happy.otc.dao.CapitalMapper;
import com.happy.otc.dao.CommodityMapper;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.*;
import com.happy.otc.enums.CapitalLogTypeEnum;
import com.happy.otc.enums.CommodityTypeEnum;
import com.happy.otc.enums.OrderStateEnum;
import com.happy.otc.service.*;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.vo.CapitalDetailInfoVO;
import com.happy.otc.vo.CapitalInfoVO;
import com.happy.otc.vo.CommodityInfoVO;
import com.happy.otc.vo.manager.CapitalDetailRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CapitalDetailServiceImpl implements ICapitalDetailService {

    private static final Logger logger = LogManager.getLogger(CapitalDetailServiceImpl.class);

    @Autowired
    private CapitalDetailMapper capitalDetailMapper;
    @Autowired
    private IUserAccountService userAccountService;
    @Autowired
    private CommodityMapper commodityMapper;
    @Autowired
    private IOauthService oauthService;
    @Autowired
    private ICapitalService capitalService;
    @Autowired
    private IUserIdentityService userIdentityService;
    @Autowired
    private IEasemobService easemobService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CapitalLogMapper capitalLogMapper;
    @Autowired
    private CapitalMapper capitalMapper;
    @Autowired
    private IUserMonitorService userMonitorService;
    @Autowired
    private ICommodityService commodityService;
    @Autowired
    private IUserService userService;
    @Value("${easemob.customerService.username}")
    private String serviceUsername;

    @Value("${capital.cipher.error.count}")
    private Integer cipherErrCount;

    @Override
    public PageInfo<CapitalDetailInfoVO> getCapitalDetailList(Integer status, Long userId, Integer pageIndex, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("status",status);
        params.put("userId",userId);

        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<CapitalDetailInfoVO> capitalDetailInfoVOList = capitalDetailMapper.selectByParam(params);
        PageInfo pageInfo = new PageInfo(capitalDetailInfoVOList);

        return  pageInfo;
    }

    @Override
    public CapitalDetail getCapitalDetail(Long capitalDetailId) {
        return capitalDetailMapper.selectByPrimaryKey(capitalDetailId);
    }

    @Transactional
    @Override
    public Integer updateCapitalDetail(Long capitalDetailId, OrderStateEnum orderStateEnum, String capitalCipher, Long userId) {
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalDetailId);
        int oreder_ststus=0;
        switch (orderStateEnum){
            case CANCELLED:
                //????????????????????????????????????
                if (!capitalDetail.getStatus().equals(OrderStateEnum.UNPAID.getValue())) {
                    BizException.fail(MessageCode.ORDER_STATUS_ERR, "?????????????????????????????????????????????");
                }
                //???????????????????????????
                if ((capitalDetail.getCommodityType().equals(CommodityTypeEnum.SELL) && !capitalDetail.getBuyUserId().equals(userId)) ||
                        (capitalDetail.getCommodityType().equals(CommodityTypeEnum.BUY) && !capitalDetail.getSellUserId().equals(userId))) {
                    BizException.fail(MessageCode.NO_AUTHORITY, "???????????????????????????");
                }
                //??????????????????
                String key = Contants.REDIS_USER_TRADE_CANCLE + userId;
                String count = redisUtil.getStringValue(key);
                if (StringUtils.isEmpty(count)) {
                    redisUtil.setStringValue(key, "1", 24 * 60 * 60);
                } else {
                    if (count.compareTo("2") == 0) {
                        redisUtil.setStringValue(key, String.valueOf(Integer.parseInt(count) + 1), 24 * 60 * 60);
                        UserMonitor userMonitor = new UserMonitor();
                        userMonitor.setUserId(userId);
                        userMonitor.setType(1);
                        userMonitor.setCreateTime(new Date());
                        userMonitorService.addUserMonitor(userMonitor);

                        //?????????????????????
                        Map<String, Object> delparams = new HashMap<>();
                        delparams.put("status",0);
                        delparams.put("userId",userId);
                        List<CommodityInfoVO> list = commodityMapper.selectByParam(delparams);
                        for (CommodityInfoVO commodity:list) {
                            try {
                                commodityService.delCommodity(commodity.getCommodityId());
                            }catch (Exception e){
                                logger.info("?????????????????????????????????id??? "+commodity.getCommodityId());
                            }
                        }
                    } else {
                        Jedis jedis = redisUtil.getResource();
                        Long time = jedis.ttl(key);
                        redisUtil.setStringValue(key, String.valueOf(Integer.parseInt(count) + 1), time.intValue());
                    }
                }
                //??????????????????
                oreder_ststus = MessageCode.ORDER_CANCELLED;
                break;
            case COMPLETED:
                //????????????????????????????????????
                if (!capitalDetail.getStatus().equals(OrderStateEnum.ALREADY_PAID.getValue())) {
                    BizException.fail(MessageCode.ORDER_STATUS_ERR, "?????????????????????????????????????????????");
                }
                //???????????????????????????
                if (!capitalDetail.getSellUserId().equals(userId)) {
                    BizException.fail(MessageCode.NO_AUTHORITY, "???????????????????????????");
                }
                //??????????????????????????????
                UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(capitalDetail.getSellUserId());

                if (StringUtils.isEmpty(capitalCipher)) {
                    BizException.fail(ApiResponseCode.PARA_MISSING_ERR, null);
                }
                //??????????????????????????????
                userIdentityService.checkCapitalCipher(capitalCipher, userIdentity.getCapitalCipher(), userId);

                capitalService.transferCapital(capitalDetailId);
                //??????????????????
                oreder_ststus = MessageCode.ORDER_COMPLETED;
                break;
            case ALREADY_PAID:
                //?????????????????????????????????
                if (!capitalDetail.getStatus().equals(OrderStateEnum.UNPAID.getValue())) {
                    BizException.fail(MessageCode.ORDER_STATUS_ERR, "?????????????????????????????????????????????");
                }
                //???????????????????????????
                if (!capitalDetail.getBuyUserId().equals(userId)) {
                    BizException.fail(MessageCode.NO_AUTHORITY, "???????????????????????????");
                }
               capitalDetail.setPayTime(new Date());
                oreder_ststus = MessageCode.ORDER_ALREADY_PAID;

                break;
            default:
                BizException.fail(ApiResponseCode.PARA_ERR,null);
                break;
        }
        capitalDetail.setStatus(orderStateEnum.getValue());
        capitalDetail.setUpdateTime(new Date());
        int result = capitalDetailMapper.updateByPrimaryKeySelective(capitalDetail);

        //??????????????????????????????????????????
        if (OrderStateEnum.CANCELLED.equals(orderStateEnum)) {
            Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());
            //????????????????????????????????????
            if (commodity.getStatus() == 0) {
                BigDecimal fee;
                if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL.getValue())) {
                    fee = capitalDetail.getServiceFee();
                } else {
                    fee = BigDecimal.ZERO;
                }
                int res = commodityMapper.updateQuantity(capitalDetail.getCommodityId(), capitalDetail.getTransactionVolume(),
                        capitalDetail.getTransactionVolume().negate(), fee, commodity.getVersion());
                if (res == 0) {
                    BizException.fail(ApiResponseCode.METHOD_UN_IMPL, null);
                }
            }
            //??????????????????????????????????????????
            thawNumber(capitalDetail);
        }

        if (oreder_ststus > 0) {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put( "code",  oreder_ststus);
            easemobService.sendOrderMessage(null, capitalDetail.getEasemobGroupId(), JSONObject.toJSONString(jsonObject2),null);
        }

//        //???????????????????????????????????????
//        if (OrderStateEnum.CANCELLED.equals(orderStateEnum) || OrderStateEnum.COMPLETED.equals(orderStateEnum)) {
//            easemobService.delChatGroups(capitalDetail.getEasemobGroupId());
//        }

        return capitalDetail.getStatus();
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
                Integer count = capitalMapper.updateByPrimaryKeySelective(updateCapital);
                if (count == 0) {
                    BizException.fail(ApiResponseCode.METHOD_UN_IMPL, "????????????");
                }

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

    /*@Transactional
    @Override
    public Boolean cancelOrder(Long capitalDetailId){

        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalDetailId);
        capitalDetail.setStatus(OrderStateEnum.CANCELLED.getValue());
        int result = capitalDetailMapper.updateByPrimaryKeySelective(capitalDetail);

//        //??????????????????
//        easemobService.delChatGroups(capitalDetail.getEasemobGroupId());

        //??????????????????????????????????????????
        Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());
       int res = commodityMapper.updateQuantity(capitalDetail.getCommodityId(), capitalDetail.getTransactionVolume(),
                capitalDetail.getTransactionVolume().negate(),commodity.getVersion());
        if (res == 0){
            BizException.fail(ApiResponseCode.METHOD_UN_IMPL, null);
        }
        //??????????????????????????????????????????
        thawNumber(capitalDetail);

        return  result > 0;
    }*/

    /**
     * ???????????????????????????
     * @param request
     * s@return
     */
    @Override
    public PageInfo<CapitalDetailInfoVO> getCapitalDetailPageList(CapitalDetailRequest request){
        Map<String, Object> params = new HashMap<>();
        params.put("status",request.getStatus());

        PageHelper.startPage(request.getCurrentPage(), request.getPageSize(), Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<CapitalDetailInfoVO> capitalDetailInfoVOList = capitalDetailMapper.selectByParam(params);
        Set<Long> idSet = new HashSet<>();
        for (CapitalDetailInfoVO capitalDetailInfoVO:capitalDetailInfoVOList) {
            idSet.add(capitalDetailInfoVO.getBuyUserId());
            idSet.add(capitalDetailInfoVO.getSellUserId());
        }
//        Result<Map<Long, UserInfoVO>>  userInfoVOMap = oauthService.getUserInfoByUserIdList(new ArrayList<>(idSet));
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(new ArrayList<>(idSet));
        for (CapitalDetailInfoVO capitalDetailInfoVO:capitalDetailInfoVOList) {
            if (capitalDetailInfoVO.getCommodityType() == 0){
//                capitalDetailInfoVO.setNickName(userInfoVOMap.getData().get(capitalDetailInfoVO.getSellUserId()).getUserName());
                capitalDetailInfoVO.setNickName(userDTOMap.get(capitalDetailInfoVO.getSellUserId()).getUserName());
            }else if (capitalDetailInfoVO.getCommodityType() == 1){
//                capitalDetailInfoVO.setNickName(userInfoVOMap.getData().get(capitalDetailInfoVO.getBuyUserId()).getUserName());
                capitalDetailInfoVO.setNickName(userDTOMap.get(capitalDetailInfoVO.getBuyUserId()).getUserName());
            }
        }

        return new PageInfo<>(capitalDetailInfoVOList);
    }

    /**
     * ??????????????????ID
     * @param capitalDetailId   ??????id
     * @return
     */
    @Override
    public String getEasemobGroupId(Long capitalDetailId) {
        //???????????????????????????ID
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalDetailId);
        if (capitalDetail == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "????????????");
        }
        String groupId = capitalDetail.getEasemobGroupId();

        //???????????????????????????
        if (StringUtils.isBlank(groupId)) {
            Commodity commodity = commodityMapper.selectByPrimaryKey(capitalDetail.getCommodityId());
            Long ownerId, orderId;
            if (commodity.getCommodityType().equals(CommodityTypeEnum.BUY.getValue())) {
                ownerId = capitalDetail.getBuyUserId();
                orderId = capitalDetail.getSellUserId();
            } else {
                ownerId = capitalDetail.getSellUserId();
                orderId = capitalDetail.getBuyUserId();
            }
            String groupName = "trade_" + capitalDetail.getOrderNumber();
            groupId = easemobService.createChatGroups(ownerId, orderId, groupName, commodity.getLeaveMessage());
            logger.error( "-=============???????????????====================" );
        }

        //??????????????????ID
        CapitalDetail update = new CapitalDetail();
        update.setCapitalDetailId(capitalDetailId);
        update.setEasemobGroupId(groupId);
        update.setUpdateTime(new Date());
        capitalDetailMapper.updateByPrimaryKeySelective(update);

        return groupId;
    }

    @Override
    public String getCustServiceAfterSale(Long capitalDetailId, Long userId) {
        //??????????????????
        CapitalDetail capitalDetail = capitalDetailMapper.selectByPrimaryKey(capitalDetailId);
        if (capitalDetail == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "????????????");
        }
        if (StringUtils.isBlank(capitalDetail.getEasemobGroupId()) || "0".equals(capitalDetail.getEasemobGroupId())) {
            BizException.fail(MessageCode.NO_CHAT_GROUP, "????????????????????????");
        }
        if (!capitalDetail.getBuyUserId().equals(userId) && !capitalDetail.getSellUserId().equals(userId)) {
            BizException.fail(MessageCode.NO_AUTHORITY, "???????????????????????????");
        }
        //??????????????????
        List<String> member = easemobService.getMember(capitalDetail.getEasemobGroupId());
        if (member == null) {
            BizException.fail(ApiResponseCode.EXTERNAL_SERVICE__EXP, null);
        }
        if (!member.contains(serviceUsername.toLowerCase())) {
            //??????????????????
            Boolean result = easemobService.addChatGroupsMember(capitalDetail.getEasemobGroupId(), serviceUsername);
            if (!result) {
                BizException.fail(ApiResponseCode.EXTERNAL_SERVICE__EXP, null);
            }
            //?????????????????????
            String message = "?????????????????????????????????????????????????????????";
            easemobService.sendMessage(serviceUsername, capitalDetail.getEasemobGroupId(), message);
        }
        return capitalDetail.getEasemobGroupId();
    }
}
