package com.happy.otc.controller.manager;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.bitan.common.vo.PageResult;
import com.bitan.common.vo.Result;
import com.happy.otc.contants.Contants;
import com.happy.otc.enums.AppealStatusEnum;
import com.happy.otc.service.ICapitalDetailService;
import com.happy.otc.service.IUserAppealService;
import com.happy.otc.vo.CapitalDetailInfoVO;
import com.happy.otc.vo.UserAppealInfoVO;
import com.happy.otc.vo.manager.AppealCheckRequest;
import com.happy.otc.vo.manager.UserAppealSearchVO;
import com.happy.otc.vo.manager.UserIdListRequest;
import com.happy.otc.service.ICapitalService;
import com.happy.otc.vo.manager.CapitalDetailRequest;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "otc-rest/m")
@Api(value = "otc-rest/m", description = "后台管理 交易处理 API")
public class CapitalManagerController {
    private final Logger logger = LoggerFactory.getLogger(CapitalManagerController.class);

    @Autowired
    private IUserAppealService userAppealService;
    @Autowired
    private ICapitalService capitalService;
    @Autowired
    private ICapitalDetailService capitalDetailService;
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "申诉列表")
    @PostMapping("/user-appeal-list")
    public PageResult<UserAppealInfoVO> getAppealList(@RequestBody UserAppealSearchVO searchVO) {
        PageInfo<UserAppealInfoVO> pageList = userAppealService.getPageList(searchVO);

        return new PageResult(pageList.getList(), pageList.getPageNum()
                , pageList.getPageSize(), pageList.getTotal());
    }

    @ApiOperation(value = "申诉信息详情")
    @GetMapping("/user-appeal-info")
    public Result<UserAppealInfoVO> getAppealInfo(@ApiParam("申诉ID") @RequestParam("userAppealId") Long userAppealId) {
        //参数校验
        BizException.isNull(userAppealId, "申诉ID");

        //获取申诉信息
        UserAppealInfoVO userAppealInfoVO = userAppealService.selectAppealInfoById(userAppealId);
        return Result.createSuccess(userAppealInfoVO);
    }

    @ApiOperation(value = "审核申诉")
    @PostMapping("/user-appeal-check")
    public Result<Boolean> appealCheck(@RequestBody AppealCheckRequest request){
        //参数校验
        BizException.isNull(request.getUserAppealId(), "申诉ID");
        BizException.isNull(request.getStatus(), "审核结果");
        if (request.getStatus() != AppealStatusEnum.SUCC.getValue() && request.getStatus() != AppealStatusEnum.FAIL.getValue()) {
            BizException.fail(ApiResponseCode.PARA_ERR, null);
        }
        //处理审核结果
        userAppealService.changeAppealStatus(request.getUserAppealId(), request.getStatus());
        return Result.createSuccess(true);
    }

/*    @ApiOperation(value = "执行发币")
    @PostMapping("/execute-currency")
    public Result<Boolean> executeCurrency(@RequestBody ExecuteCurrencyRequest request){
        capitalService.transferCapital(request.getCapitalDetailId());
        return Result.createSuccess();
    }*/

    @ApiOperation(value = "用户交易订单列表")
    @PostMapping(value = "/user-capital-detail-list")
    public PageResult<CapitalDetailInfoVO> getCapitalDetailInfoList(@RequestBody CapitalDetailRequest request){


        BizException.isNull(request.getCurrentPage(), "pageIndex");
        BizException.isNull(request.getPageSize(), "pageSize");

        PageInfo<CapitalDetailInfoVO> pageInfo =  capitalDetailService.getCapitalDetailPageList(request);

        return  new PageResult(pageInfo.getList(), pageInfo.getPageNum()
                , pageInfo.getPageSize(), pageInfo.getTotal());
    }

    @ApiOperation(value = "清空用户资金密码错误次数")
    @PostMapping(value = "/clear-capital-cipher-err-count")
    public Result<Boolean> clearCapitalCipherErrCount(@RequestBody UserIdListRequest request) {
        BizException.isNull(request.getUserIdList(), "用户ID");
        Boolean result = true;
        for (Long userId : request.getUserIdList()) {
            Boolean bool = redisUtil.delete(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId);
            if (!bool) {
                result = false;
            }
            logger.info("清空用户资金密码错误次数,userId={}", userId);
        }
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "清空用户订单取消次数")
    @PostMapping(value = "/clear-trade-cancel-count")
    public Result<Boolean> clearTradeCancelCount(@RequestBody UserIdListRequest request) {
        BizException.isNull(request.getUserIdList(), "用户ID");
        Boolean result = true;
        for (Long userId : request.getUserIdList()) {
            Boolean bool = redisUtil.delete(Contants.REDIS_USER_TRADE_CANCLE + userId);
            if (!bool) {
                result = false;
            }
            logger.info("清空用户订单取消次数,userId={}", userId);
        }
        return Result.createSuccess(result);
    }
}
