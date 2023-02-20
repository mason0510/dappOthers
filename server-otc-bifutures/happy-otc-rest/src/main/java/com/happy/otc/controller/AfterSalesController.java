package com.happy.otc.controller;

import com.bitan.common.login.LoginDataHelper;
import com.happy.otc.proto.ResultInfo;
import com.happy.otc.service.ICapitalDetailService;
import com.happy.otc.util.ProtoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "售后服务Api")
public class AfterSalesController {

    @Autowired
    private ICapitalDetailService capitalDetailService;

    @ApiOperation(value = "获取售后客服")
    @GetMapping("/cust-serv-after-sale")
    public ResultInfo.ResultStringVO getCustomerServiceAfterSale(@ApiParam("订单编号")
                                                                  @RequestParam("capitalDetailId") Long capitalDetailId){
        Long userId = LoginDataHelper.getUserId();

        String groupId = capitalDetailService.getCustServiceAfterSale(capitalDetailId, userId);

        return ProtoUtils.createStringSuccess(groupId);
    }
}
