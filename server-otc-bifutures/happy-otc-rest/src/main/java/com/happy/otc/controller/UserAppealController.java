package com.happy.otc.controller;

import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.happy.otc.entity.UserAppeal;
import com.happy.otc.proto.AppealInfo;
import com.happy.otc.proto.ResultInfo;
import com.happy.otc.service.IUserAppealService;
import com.happy.otc.util.ProtoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "用户申诉Api")
public class UserAppealController {

    @Autowired
    private IUserAppealService userAppealService;

    @ApiOperation(value = "申诉处理")
    @PostMapping(value = "/add-appeal")
    public ResultInfo.ResultBooleanVO addAppeal(@RequestBody AppealInfo.AppealInfoVO appealInfoVO){

        //参数校验
        BizException.isNull(appealInfoVO.getCapitalDetailId(), "交易ID");
        BizException.isNull(appealInfoVO.getType(), "申诉类型");
        BizException.isNull(appealInfoVO.getReason(), "申诉理由");

        //申诉处理
        Long userId = LoginDataHelper.getUserId();
        UserAppeal userAppeal = new UserAppeal();
        userAppeal.setCapitalDetailId(appealInfoVO.getCapitalDetailId());
        userAppeal.setType((byte) appealInfoVO.getType().getNumber());
        userAppeal.setReason(appealInfoVO.getReason());
        userAppeal.setUserId(userId);
        Boolean result = userAppealService.addAppeal(userAppeal);

        //返回结果
        return ProtoUtils.createBoolSuccess(result);
    }

    @ApiOperation(value = "取消申诉")
    @PostMapping(value = "/cancel-appeal")
    public ResultInfo.ResultBooleanVO cancelAppeal(@ApiParam("申诉ID") @RequestParam("userAppealId") Long userAppealId){
        //参数校验
        BizException.isNull(userAppealId, "申诉ID");

        //取消处理
        Long userId = LoginDataHelper.getUserId();
        Boolean result = userAppealService.cancelAppeal(userAppealId, userId);

        return ProtoUtils.createBoolSuccess(result);
    }
}
