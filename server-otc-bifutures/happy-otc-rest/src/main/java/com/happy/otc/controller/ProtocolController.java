package com.happy.otc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "协议Api")
public class ProtocolController {


    /**
     * 获取用户服务协议
     *
     * @return
     */
    @ApiOperation(value = "获取用户服务协议中文版")
    @RequestMapping(value = "/user-service-protocol-cn", method = RequestMethod.GET)
    public String userServiceProtocolCN(){

        return "userServiceProtocolCN";
    }

    @ApiOperation(value = "获取用户服务协议英文版")
    @RequestMapping(value = "/user-service-protocol-en", method = RequestMethod.GET)
    public String userServiceProtocolEN(){

        return "userServiceProtocolEN";
    }

    @ApiOperation(value = "获取商家服务协议中文版")
    @RequestMapping(value = "/user-authenticate-protocol-cn", method = RequestMethod.GET)
    public String authenticatedMerchantProtocolCN(){

        return "authenticatedMerchantProtocolCN";
    }

    @ApiOperation(value = "获取商家服务协议英文版")
    @RequestMapping(value = "/user-authenticate-protocol-en", method = RequestMethod.GET)
    public String authenticatedMerchantProtocolEN(){

        return "authenticatedMerchantProtocolEN";
    }

    @ApiOperation(value = "获取交易规则英文版")
    @RequestMapping(value = "/user-transaction-regulations-en", method = RequestMethod.GET)
    public String transactionRegulationsEN(){

        return "transactionRegulationsEN";
    }

    @ApiOperation(value = "获取交易规则中文版")
    @RequestMapping(value = "/user-transaction-regulations-cn", method = RequestMethod.GET)
    public String transactionRegulationsCN(){

        return "transactionRegulationsCN";
    }

}
