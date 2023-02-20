package com.happy.otc.controller;

import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.vo.Result;
import com.bitan.market.vo.OriginalExchangeInfoVO;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.vo.BiFuturesOtc;
import com.happy.otc.enums.KindEnum;
import com.happy.otc.proto.Asset;
import com.happy.otc.service.ICapitalService;
import com.happy.otc.service.ITransactionPairService;
import com.happy.otc.service.bifutures.UserAssetsService;
import com.happy.otc.util.MoneyUtil;
import com.happy.otc.vo.CapitalInfoVO;
import com.happy.otc.vo.FundVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "用户资产Api")
public class UserCapitalController {

    @Autowired
    ICapitalService capitalService;
    @Autowired
    ITransactionPairService transactionPairService;
    @Autowired
    UserAssetsService userAssetsService;

    @ApiOperation(value = "用户资产详情")
    @RequestMapping(value = "/user-capital-info", method = RequestMethod.GET)
    public Result<FundVO> getCapitalDetailInfo() throws ParserConfigurationException, SAXException, ParseException, IOException {
        Long userId = LoginDataHelper.getUserId();
        FundVO fundVO = capitalService.buildFundVO(userId);

        return Result.createSuccess(fundVO);
    }

}
