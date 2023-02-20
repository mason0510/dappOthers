package com.happy.otc.controller;

import com.alibaba.fastjson.JSONArray;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.vo.Result;
import com.github.pagehelper.PageInfo;
import com.happy.otc.annotation.CapitalCipherCheck;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.CapitalDetail;
import com.happy.otc.entity.Commodity;
import com.happy.otc.entity.UserAccount;
import com.happy.otc.enums.CommodityTypeEnum;
import com.happy.otc.enums.OrderStateEnum;
import com.happy.otc.enums.PayMethodEnums;
import com.happy.otc.enums.TradeTimeTypeTypeEnum;
import com.happy.otc.proto.*;
import com.happy.otc.service.*;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.util.OtcUtils;
import com.happy.otc.util.ProtoUtils;
import com.happy.otc.vo.CapitalDetailInfoVO;
import com.happy.otc.vo.CapitalLogInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "交易订单Api")
public class CapitalController {

    @Autowired
    ICapitalDetailService capitalDetailService;
    @Autowired
    ICommodityService commodityService;
    @Autowired
    private IOauthService oauthService;
    @Autowired
    private IUserAccountService userAccountService;
    @Autowired
    private ICapitalService capitalService;
    @Autowired
    private IEasemobService easemobService;
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "交易订单详情")
    @RequestMapping(value = "/capital-detail-info", method = RequestMethod.GET)
    public Order.CapitalDetailResDTO getCapitalDetailInfo(@ApiParam("订单id") @RequestParam(value = "capitalDetailId") Long capitalDetailId) {
        Long userId = LoginDataHelper.getUserId();

        CapitalDetail capitalDetail = capitalDetailService.getCapitalDetail(capitalDetailId);
        Commodity commodity = commodityService.getCommodityById(capitalDetail.getCommodityId());
        if (Objects.isNull(capitalDetail) || Objects.isNull(commodity)) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, null);
        }
        Order.CapitalDetailDTO.Builder capitalDetailDTO = Order.CapitalDetailDTO.newBuilder();
        capitalDetailDTO.setBuyUserId(capitalDetail.getBuyUserId());
        capitalDetailDTO.setCapitalDetailId(capitalDetail.getCapitalDetailId());
        capitalDetailDTO.setCommodityId(capitalDetail.getCommodityId());
        capitalDetailDTO.setCrateTime(String.valueOf(capitalDetail.getCrateTime().getTime()));
        capitalDetailDTO.setCurrencyPrice(capitalDetail.getTransactionPrice().doubleValue());
        capitalDetailDTO.setKind(commodity.getKind());
        capitalDetailDTO.setRelevantKind(commodity.getRelevantKind());
        capitalDetailDTO.setSellUserId(capitalDetail.getSellUserId());
        capitalDetailDTO.setStatusValue(capitalDetail.getStatus());
        capitalDetailDTO.setTransactionAmount(capitalDetail.getTransactionAmount().toString());
        capitalDetailDTO.setTransactionVolume(capitalDetail.getTransactionVolume().toString());
        capitalDetailDTO.setOrderNumber(capitalDetail.getOrderNumber());
        capitalDetailDTO.setTradeTimeTypeValue(commodity.getTradeTimeType());
        capitalDetailDTO.setReferenceNumber(capitalDetail.getReferenceNumber());
        capitalDetailDTO.setEasemobGroupId(capitalDetail.getEasemobGroupId());
        capitalDetailDTO.setCurrencyId(commodity.getCurrencyId());
        capitalDetailDTO.setUpdateTime(String.valueOf(capitalDetail.getUpdateTime().getTime()));
        Long temTime = 1000L;
        Long nowTime = (new Date()).getTime();
        TradeTimeTypeTypeEnum tradeTimeTypeTypeEnum = TradeTimeTypeTypeEnum.getInstance(commodity.getTradeTimeType());
        temTime = tradeTimeTypeTypeEnum.getLeaveTime() + capitalDetail.getCrateTime().getTime();
        capitalDetailDTO.setLeaveTime(temTime - nowTime);
        //判断到底是买方在查看详情，还是卖方在查看
        Integer lookType = OtcUtils.lookUserInfo(userId, capitalDetail.getBuyUserId(), capitalDetail.getSellUserId());
        if (Objects.isNull(lookType)) {
            BizException.fail(MessageCode.NO_AUTHORITY, "当前用户无权限操作");
        } else {
            capitalDetailDTO.setOrderRoleValue(lookType);
            if (lookType == 1) {
                UserDTO userDTO = userService.getUserInfoByUserId(capitalDetail.getSellUserId());
                capitalDetailDTO.setNickName(userDTO.getUserName());
            } else {
                UserDTO userDTO = userService.getUserInfoByUserId(capitalDetail.getBuyUserId());
                capitalDetailDTO.setNickName(userDTO.getUserName());
            }
        }
        capitalDetailDTO.setPayTime(String.valueOf(capitalDetail.getPayTime().getTime()));
        List<Payaccount.PayMethodVO> list = new ArrayList<>();

        if (commodity.getCommodityType().equals(CommodityTypeEnum.SELL.getValue())) {
            //卖单商品显示商品的支付方式
            JSONArray payMethodArray = JSONArray.parseArray(commodity.getPayMethodList());
            for (int i = 0; i < payMethodArray.size(); i++) {

                Long userAccountId = Long.valueOf(payMethodArray.get(i).toString());
                UserAccount userAccount = userAccountService.getUserAccount(userAccountId);

                Payaccount.PayMethodVO.Builder payMethodVO = Payaccount.PayMethodVO.newBuilder();
                if (!Objects.isNull(userAccount)) {
                    payMethodVO.setAddress(userAccount.getAddress());
                    if (PayMethodEnums.BANK_CARD.getValue() == userAccount.getPayType()) {
                        payMethodVO.setBankName(userAccount.getPaymentDetail().split(":")[0]);
                        payMethodVO.setBankBranchName(userAccount.getPaymentDetail().split(":")[1]);
                    }
                    payMethodVO.setAccount(userAccount.getAccount());
                    payMethodVO.setRealName(userAccount.getRealName());
                    payMethodVO.setPayTypeValue(userAccount.getPayType());
                    list.add(payMethodVO.build());
                }
            }
        } else {
            //买单商品显示卖家支付方式
            List<UserAccount> accounts = userAccountService.getUserAccountList(capitalDetail.getSellUserId());
            for (UserAccount userAccount : accounts) {
                //关闭的支付方式不显示
                if (userAccount.getPayStatus().equals(0)) {
                    continue;
                }
                Payaccount.PayMethodVO.Builder payMethodVO = Payaccount.PayMethodVO.newBuilder();
                payMethodVO.setAddress(userAccount.getAddress());
                if (PayMethodEnums.BANK_CARD.getValue() == userAccount.getPayType()) {
                    payMethodVO.setBankName(userAccount.getPaymentDetail().split(":")[0]);
                    payMethodVO.setBankBranchName(userAccount.getPaymentDetail().split(":")[1]);
                }
                payMethodVO.setAccount(userAccount.getAccount());
                payMethodVO.setRealName(userAccount.getRealName());
                payMethodVO.setPayTypeValue(userAccount.getPayType());
                list.add(payMethodVO.build());
            }
        }
        capitalDetailDTO.addAllPayMethod(list);
        Order.CapitalDetailResDTO result = Order.CapitalDetailResDTO.getDefaultInstance();
        return ProtoUtils.createResultSuccess(capitalDetailDTO.build(), result);
    }

    @ApiOperation(value = "用户交易订单列表")
    @RequestMapping(value = "/user-capital-detail-list", method = RequestMethod.GET)
    public Order.ListOrderDTO getCapitalDetailInfoList(@ApiParam("状态 1:未付款，2：已付款，3：申诉中，4：已取消，5：已完成,7:统合1，2，3的状态记录")
                                                       @RequestParam(value = "status", required = false) Integer status,
                                                       @RequestParam(value = "pageIndex") Integer pageIndex,
                                                       @RequestParam(value = "pageSize") Integer pageSize) {

        Long userId = LoginDataHelper.getUserId();
        BizException.isNull(pageIndex, "pageIndex");
        BizException.isNull(pageSize, "pageSize");

        OrderStateEnum orderStateEnum = OrderStateEnum.getInstance(status);
        if (Objects.isNull(orderStateEnum)) {
            status = null;
        }

        PageInfo<CapitalDetailInfoVO> pageInfo = capitalDetailService.getCapitalDetailList(status, userId, pageIndex, pageSize);

        if (pageInfo.getList().size() == 0) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "数据不存在");
        }

        List<Long> userIdList = new ArrayList<>();
        for (CapitalDetailInfoVO capitalDetailInfoVO : pageInfo.getList()) {
            if (!userIdList.contains(capitalDetailInfoVO.getBuyUserId())) {
                userIdList.add(capitalDetailInfoVO.getBuyUserId());
            }
            if (!userIdList.contains(capitalDetailInfoVO.getSellUserId())) {
                userIdList.add(capitalDetailInfoVO.getSellUserId());
            }
        }
//        Result<Map<Long, UserInfoVO>> userInfoVOMap = oauthService.getUserInfoByUserIdList(userIdList);
        Map<Long, UserDTO> userDTOMap = userService.UserInfoByUserIds(userIdList);

        Order.ListOrderDTO.Builder listOrderDTO = Order.ListOrderDTO.newBuilder();
        //请求数据的封装
        List<Order.OrderDTO> list = new ArrayList<>();
        Order.OrderDTO.Builder orderDTO = Order.OrderDTO.newBuilder();
        for (CapitalDetailInfoVO capitalDetailInfoVO : pageInfo.getList()) {
            orderDTO.setCapitalDetailId(capitalDetailInfoVO.getCapitalDetailId());
            //判断到底是买方在查看详情，还是卖方在查看
            Integer lookType = OtcUtils.lookUserInfo(userId, capitalDetailInfoVO.getBuyUserId(), capitalDetailInfoVO.getSellUserId());
            if (Objects.isNull(lookType)) {
                BizException.fail(MessageCode.NO_AUTHORITY, "当前用户无权限操作");
            } else {
                orderDTO.setOrderRoleValue(lookType);
                if (lookType == 1) {
                    orderDTO.setUserId(capitalDetailInfoVO.getSellUserId());
//                    orderDTO.setNickName(userInfoVOMap.getData().get(capitalDetailInfoVO.getSellUserId()).getUserName());
                    orderDTO.setNickName(userDTOMap.get(capitalDetailInfoVO.getSellUserId()).getUserName());
                } else {
                    orderDTO.setUserId(capitalDetailInfoVO.getBuyUserId());
//                    orderDTO.setNickName(userInfoVOMap.getData().get(capitalDetailInfoVO.getBuyUserId()).getUserName());
                    orderDTO.setNickName(userDTOMap.get(capitalDetailInfoVO.getBuyUserId()).getUserName());
                }
            }

            orderDTO.setCurrencyPrice(capitalDetailInfoVO.getTransactionPrice().doubleValue());
            orderDTO.setKind(capitalDetailInfoVO.getKind());
            orderDTO.setRelevantKind(capitalDetailInfoVO.getRelevantKind());
            orderDTO.setTransactionAmount(capitalDetailInfoVO.getTransactionAmount().doubleValue());
            orderDTO.setCrateTime(String.valueOf(capitalDetailInfoVO.getCrateTime().getTime()));
            orderDTO.setUpdateTime(String.valueOf(capitalDetailInfoVO.getUpdateTime().getTime()));
            orderDTO.setStatusValue(capitalDetailInfoVO.getStatus());
            orderDTO.setOrderNumber(capitalDetailInfoVO.getOrderNumber());
            orderDTO.setOrderMessage(OtcUtils.messageLave(OrderStateEnum.getInstance(capitalDetailInfoVO.getStatus()), capitalDetailInfoVO));
            orderDTO.setEasemobGroupId(capitalDetailInfoVO.getEasemobGroupId());
            orderDTO.setLeaveMessage(capitalDetailInfoVO.getLeaveMessage());

            list.add(orderDTO.build());
        }

        listOrderDTO.addAllData(list);

        Order.ListOrderDTO result = Order.ListOrderDTO.getDefaultInstance();
        return ProtoUtils.createPageResultSuccess(list, pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), result);
    }

    @ApiOperation(value = "修改交易订单详情")
    @RequestMapping(value = "/update-capital-detail", method = RequestMethod.POST)
    @CapitalCipherCheck
    public Result<Integer> updateCapitalDetailInfo(@ApiParam("订单id") @RequestParam(value = "capitalDetailId") Long capitalDetailId,
                                                   @ApiParam("状态 1:未付款，2：已付款，3：申诉中，4：已取消，5：已完成") @RequestParam(value = "orderStatus") Integer orderStatus,
                                                   @ApiParam("资金密码") @RequestParam(value = "capitalCipher", required = false) String capitalCipher) {
        Long userId = LoginDataHelper.getUserId();
        OrderStateEnum orderStateEnum = OrderStateEnum.getInstance(orderStatus);
        Integer result = capitalDetailService.updateCapitalDetail(capitalDetailId, orderStateEnum, capitalCipher, userId);
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "交易订单状态及倒计时间")
    @RequestMapping(value = "/check-capital-detail", method = RequestMethod.GET)
    public Order.CapitalDetailCheckResDTO checkCapitalDetailInfo(@ApiParam("订单id") @RequestParam(value = "capitalDetailId") Long capitalDetailId) {

        Long userId = LoginDataHelper.getUserId();

        CapitalDetail capitalDetail = capitalDetailService.getCapitalDetail(capitalDetailId);
        Commodity commodity = commodityService.getCommodityById(capitalDetail.getCommodityId());
        if (Objects.isNull(capitalDetail) || Objects.isNull(commodity)) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, null);
        }

        Order.CapitalDetailCheckDTO.Builder capitalDetailCheckDTO = Order.CapitalDetailCheckDTO.newBuilder();
        Long temTime = 1000L;
        Long nowTime = (new Date()).getTime();
        TradeTimeTypeTypeEnum tradeTimeTypeTypeEnum = TradeTimeTypeTypeEnum.getInstance(commodity.getTradeTimeType());
        temTime = tradeTimeTypeTypeEnum.getLeaveTime() + capitalDetail.getCrateTime().getTime();
        if (nowTime.compareTo(temTime) > 0 && OrderStateEnum.UNPAID.getValue() == capitalDetail.getStatus()) {
            capitalDetailCheckDTO.setStatusValue(OrderStateEnum.CANCELLED.getValue());
            capitalDetailCheckDTO.setLeaveTime(0L);
        } else {
            capitalDetailCheckDTO.setLeaveTime(temTime - nowTime);
            capitalDetailCheckDTO.setStatusValue(capitalDetail.getStatus());
        }
        Order.CapitalDetailCheckResDTO result = Order.CapitalDetailCheckResDTO.getDefaultInstance();
        return ProtoUtils.createResultSuccess(capitalDetailCheckDTO.build(), result);
    }

    @ApiOperation(value = "资产变动明细")
    @PostMapping(value = "/capital-log-list")
    public PageResultInfo.PageResultCapitalLogInfoVO capitalLogList(@RequestBody CapitalLogInfo.CapitalLogRequest request) {
        //参数判断
        BizException.isNull(request.getType(),"类型");
        BizException.isNull(request.getTime(),"时间");
        BizException.isNull(request.getPageIndex(), "页数");
        BizException.isNull(request.getPageSize(), "每页大小");

        //用户
        Long userId = LoginDataHelper.getUserId();

        PageInfo<CapitalLogInfoVO> capitalLogList = capitalService.getCapitalLogList(userId, request.getTypeValue(),
                request.getTime(), request.getTimeZone(), request.getKind(), request.getPageIndex(), request.getPageSize());
        List<CapitalLogInfo.CapitalLogVO> capitalLogVOS = new ArrayList<>();
        for (CapitalLogInfoVO capitalLog : capitalLogList.getList()) {
            CapitalLogInfo.CapitalLogVO.Builder builder = CapitalLogInfo.CapitalLogVO.newBuilder();
            builder.setKind(capitalLog.getKind());
            builder.setQuantity(capitalLog.getAvailableNumber().add(capitalLog.getFrozenNumber()).abs().doubleValue());
            builder.setType(CapitalLogInfo.CapitalLogType.forNumber(capitalLog.getType()));
            builder.setCreateTime(capitalLog.getCreateTime().getTime());
            if (capitalLog.getAddress() != null) {
                builder.setAddress(capitalLog.getAddress());
            }
            if (capitalLog.getFee() != null) {
                builder.setFee(capitalLog.getFee().doubleValue());
            }
            if (capitalLog.getPrice() != null) {
                builder.setPrice(capitalLog.getPrice().doubleValue());
            }
            if (capitalLog.getTargetUsername() != null) {
                builder.setTargetUsername(capitalLog.getTargetUsername());
            }
            if (StringUtils.isNotBlank(capitalLog.getRelevantKind())) {
                builder.setRelevantKind(capitalLog.getRelevantKind());
            }
            capitalLogVOS.add(builder.build());
        }
        PageResultInfo.PageResultCapitalLogInfoVO result = PageResultInfo.PageResultCapitalLogInfoVO.getDefaultInstance();
        return ProtoUtils.createPageResultSuccess(capitalLogVOS, capitalLogList.getPageNum(), capitalLogList.getPageSize(),
                capitalLogList.getTotal(), result);
    }

    @ApiOperation(value = "获取交易订单的环信群组ID")
    @GetMapping("/capital-detail-groupid")
    public ResultInfo.ResultStringVO getEasemobGroup(@ApiParam("交易单ID") @RequestParam("capitalDetailId") Long capitalDetailId) {

        BizException.isNull(capitalDetailId, "交易订单ID");

        //获取订单的环信群组ID
        String groupId = capitalDetailService.getEasemobGroupId(capitalDetailId);

        return ProtoUtils.createStringSuccess(groupId);
    }

}
