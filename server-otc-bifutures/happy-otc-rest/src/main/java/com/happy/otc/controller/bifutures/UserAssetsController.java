package com.happy.otc.controller.bifutures;

import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.vo.PageResult;
import com.bitan.common.vo.Result;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.entity.FuturesFundDetail;
import com.happy.otc.bifutures.enums.FundDetailExplainEnum;
import com.happy.otc.bifutures.pojo.FundDetailQuery;
import com.happy.otc.bifutures.utill.CollectionUtil;
import com.happy.otc.bifutures.vo.BiFuturesOtc;
import com.happy.otc.bifutures.vo.BiFuturesUserInfo;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.service.IUserIdentityService;
import com.happy.otc.service.bifutures.BiFuturesService;
import com.happy.otc.service.bifutures.UserAssetsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuy on 2018\11\16 0016.
 */
@RestController
@RequestMapping(value = "/bi-rest")
@Api(value = "/bi-rest", description = "币期货用户信息")
public class UserAssetsController {

    @Autowired
    BiFuturesService biFuturesService;
    @Autowired
    UserAssetsService userAssetsService;
    @Autowired
    private IUserIdentityService iUserIdentityService;

    @ApiOperation(value = "币期货用户账户信息返回")
    @RequestMapping(value = "/getUserAssets",method = {RequestMethod.GET})
    public Result<BiFuturesOtc> getUserAssets(@ApiParam("用户id") @RequestParam("userId") Long userId) throws ParserConfigurationException, SAXException, ParseException, IOException {

        UserIdentity userRealInfo = iUserIdentityService.getUserExtendIdentity(userId);
        if(userRealInfo == null){
            return Result.createUnknowFail(501,"无该用户信息！");
        }
        BiFuturesOtc userAssets = userAssetsService.getAssetsByUserId(userId);
        if (userAssets !=null ) {
            return Result.createSuccess(userAssets);
        }else {
            return Result.createUnknowFail(999,"该用户尚未在币期货创建账户！");
        }
    }

    @ApiOperation(value = "修改用户账户信息")
    @RequestMapping(value = "/updateUserAssets",method = {RequestMethod.POST})
    public Result<Integer> updateUserAssets(@ApiParam("用户id") @RequestParam("userId") Long userId,
                                            @ApiParam("划转金额") @RequestParam("money") BigDecimal money,
                                            @ApiParam("划转方向：1，转入子账户；2，转出子账户") @RequestParam("direction") Integer direction){

        UserIdentity userRealInfo = iUserIdentityService.getUserExtendIdentity(userId);
        if(userRealInfo == null){
            return Result.createUnknowFail(501,"无该用户信息！");
        }
        Integer result = userAssetsService.updateAssetsByUserAssetsDto(userId,money,direction);
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "币期货用户用户信息返回")
    @RequestMapping(value = "/getUserInfo",method = {RequestMethod.GET})
    public Result<BiFuturesUserInfo> getUserInfo(@ApiParam("用户id") @RequestParam("userId") Long userId){

        UserIdentity userRealInfo = iUserIdentityService.getUserExtendIdentity(userId);
        if(userRealInfo == null){
            return Result.createUnknowFail(501,"无该用户信息！");
        }
        BiFuturesUserInfo biFuturesUserInfo = userAssetsService.getUserInfoById(userId);
        return Result.createSuccess(biFuturesUserInfo);
    }

    @ApiOperation(value = "用户资金明细")
    @RequestMapping(value = "/getUserAssetsDetail",method = {RequestMethod.GET})
    public PageResult<FuturesFundDetail> getUserAssetsDetail(@RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                                             @RequestParam(value = "pageSize",required = false) Integer pageSize,
                                                             @ApiParam("资金进出类型：fundIn,充值；fundOut，提现；fund，收入；loan，支出；all，全部")
                                                   @RequestParam("explain") String explain){

        Long userId = LoginDataHelper.getUserId();
        //Long userId = 10075l;
        BizException.isNull(pageIndex, "pageIndex");
        BizException.isNull(pageSize, "pageSize");

        PageInfo<FuturesFundDetail> pageList = userAssetsService.selectPageByUserId(convertQuery(explain,userId),pageIndex,pageSize);
        return new PageResult(pageList.getList(), pageList.getPageNum()
                , pageList.getPageSize(), pageList.getTotal());
    }

    private FundDetailQuery convertQuery(String explain, Long userId){
        FundDetailQuery fundDetailQuery = new FundDetailQuery();
        fundDetailQuery.setUserId(userId);
        if ("fundIn".equals(explain)){
            fundDetailQuery.setExplain(CollectionUtil.asList(FundDetailExplainEnum.CHARGE_IN.getValue()));
        }else if ("fundOut".equals(explain)){
            fundDetailQuery.setExplain(CollectionUtil.asList(FundDetailExplainEnum.WITHDRAW_OUT.getValue()));
        }else if ("fund".equals(explain)){
            fundDetailQuery.setExplain(CollectionUtil.asList(FundDetailExplainEnum.SELL_STRATEGY_SUCCESS_IN.getValue(),FundDetailExplainEnum.BUY_STRATEGY_FAIL_IN.getValue()
            ,FundDetailExplainEnum.OTHER_IN.getValue(),FundDetailExplainEnum.SELL_STRATEGY_DEFER_CHARGE_IN.getValue(),FundDetailExplainEnum.SELL_CLOSE_STRATEGY_SUCCESS_IN.getValue()
            ,FundDetailExplainEnum.SELL_GAIN_STRATEGY_SUCCESS_IN.getValue(),FundDetailExplainEnum.SELL_LIMITE_STRATEGY_IN.getValue(),FundDetailExplainEnum.SELL_LOSS_STRATEGY_SUCCESS_IN.getValue()
            ,FundDetailExplainEnum.SELL_LIMITE_CURRENT_IN.getValue()));
        }else if ("loan".equals(explain)){
            fundDetailQuery.setExplain(CollectionUtil.asList(FundDetailExplainEnum.PRINCIPAL_APPEND_OUT.getValue(),FundDetailExplainEnum.BUY_STRATEGY_SUCCESS_OUT.getValue()
            ,FundDetailExplainEnum.PRINCIPAL_APPEND_OUT.getValue(),FundDetailExplainEnum.STRATEGY_DEFER_CHARGE_OUT.getValue(),FundDetailExplainEnum.UPDATE_STRATEGY_PRINCIPAL_OUT.getValue()
            ,FundDetailExplainEnum.OTHER_OUT.getValue(),FundDetailExplainEnum.BUY_LIMITE_STRATEGY_OUT.getValue(),FundDetailExplainEnum.STRATEGY_SERVICE_CHARGE_OUT.getValue()));
        } else {
            fundDetailQuery.setExplain(excludeFundDetailExplainEnums());
        }
        return fundDetailQuery;
    }

    private List<String> excludeFundDetailExplainEnums() {
        List<FundDetailExplainEnum> fundDetailExplainList = CollectionUtil.asList(FundDetailExplainEnum.values());
        List<String> result = new ArrayList<String>();
        for (FundDetailExplainEnum typeDetailExplainEnum : fundDetailExplainList) {
            result.add(typeDetailExplainEnum.getValue());
        }
        return result;
    }
}
