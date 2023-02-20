package com.happy.otc.controller;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.vo.Result;
import com.happy.otc.annotation.CapitalCipherCheck;
import com.happy.otc.entity.UserAccount;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.enums.PayMethodEnums;
import com.happy.otc.proto.Payaccount;
import com.happy.otc.service.IUserAccountService;
import com.happy.otc.service.IUserIdentityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "用户账户Api")
public class UserAccountController {

    @Autowired
    private IUserAccountService userAccountService;
    @Autowired
    private IUserIdentityService userIdentityService;

    /**
     * 获取单个用户账户列表信息
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "获取单个用户账户列表信息")
    @RequestMapping(value = "/user-account-list", method = RequestMethod.GET)
    public Payaccount.PayMethodListResult getUserAccountList() throws BizException {
        Long userId = LoginDataHelper.getUserId();

        Payaccount.PayMethodListResult.Builder payMethodResult = Payaccount.PayMethodListResult.newBuilder();
        List<Payaccount.PayMethodVO> list = new ArrayList<>();
        List<UserAccount> userAccountList = userAccountService.getUserAccountList(userId);
        Payaccount.PayMethodVO.Builder pmv = Payaccount.PayMethodVO.newBuilder();
        for (UserAccount userAccount:userAccountList) {
            pmv.setAccount(userAccount.getAccount());
            pmv.setAddress(userAccount.getAddress());
            pmv.setRealName(userAccount.getRealName());
            pmv.setPayTypeValue(userAccount.getPayType());
            pmv.setUserAccountId(userAccount.getUserAccountId().intValue());
            pmv.setPayStatusValue(userAccount.getPayStatus());
            PayMethodEnums payMethodEnums = PayMethodEnums.getInstance(userAccount.getPayType());
            if (payMethodEnums.BANK_CARD == payMethodEnums){
                pmv.setBankName(userAccount.getPaymentDetail().split(":")[0]);
                pmv.setBankBranchName(userAccount.getPaymentDetail().split(":")[1]);
            }
            list.add(pmv.build());
        }
        payMethodResult.addAllData(list);
        //对于返回数据统计记录
        if (userAccountList.size() == 0){
            payMethodResult.setSuccess(false);
            payMethodResult.setCode(ApiResponseCode.DATA_NOT_EXIST.get());
            payMethodResult.setMessage(ApiResponseCode.DATA_NOT_EXIST.getName());
        }else {
            payMethodResult.setSuccess(true);
            payMethodResult.setCode(ApiResponseCode.SUCCESS.get());
            payMethodResult.setMessage(ApiResponseCode.SUCCESS.getName());
        }
        payMethodResult.setSuccess(true);

        return payMethodResult.build();
    }
    /**
     * 查看单个用户账户信息
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "获取单个用户账户信息")
    @RequestMapping(value = "/user-account-info", method = RequestMethod.GET)
    public Payaccount.PayMethodResult getUserAccountInfo(@ApiParam("用户账户id") @RequestParam("userAccountId") Long userAccountId) throws BizException {
        Long userId = LoginDataHelper.getUserId();
        UserAccount userAccount = userAccountService.getUserAccount(userAccountId);
        Payaccount.PayMethodVO.Builder pmv = Payaccount.PayMethodVO.newBuilder();
        Payaccount.ResultVO.Builder result = Payaccount.ResultVO.newBuilder();
        Payaccount.PayMethodResult.Builder payMethodResult = Payaccount.PayMethodResult.newBuilder();
        pmv.setUserAccountId(userAccountId);
        pmv.setAccount(userAccount.getAccount());
        pmv.setAddress(userAccount.getAddress());
        pmv.setPayTypeValue(userAccount.getPayType());
        pmv.setRealName(userAccount.getRealName());
        pmv.setPayStatusValue(userAccount.getPayStatus());
        PayMethodEnums payMethodEnums = PayMethodEnums.getInstance(userAccount.getPayType());
        if (payMethodEnums.BANK_CARD == payMethodEnums){
            pmv.setBankName(userAccount.getPaymentDetail().split(":")[0]);
            pmv.setBankBranchName(userAccount.getPaymentDetail().split(":")[1]);
        }
        payMethodResult.setCode(ApiResponseCode.SUCCESS.get());
        payMethodResult.setMessage(ApiResponseCode.SUCCESS.getName());
        payMethodResult.setSuccess(true);
        payMethodResult.setPayMethod(pmv.build());
        return payMethodResult.build();
    }

    /**
     * 添加用户账户信息
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "添加账户")
    @RequestMapping(value = "/add-account", method = RequestMethod.POST)
    @CapitalCipherCheck
    public Result<Long> addAccount(@RequestBody Payaccount.PayMethodVO payMethodVO) throws BizException {
        Long userId = LoginDataHelper.getUserId();

        BizException.isNull(payMethodVO.getCapitalCipher(),"资金密码");

        PayMethodEnums payMethodEnums = PayMethodEnums.getInstance(payMethodVO.getPayType().getNumber());
        if (Objects.isNull(payMethodEnums)){
            BizException.fail(ApiResponseCode.PARA_ERR,null);
        }

        //当前支付方式不需要账户
        if (payMethodEnums.XILIAN_REMITTANCE != payMethodEnums
                && payMethodEnums.SWIFT_REMITTANCE != payMethodEnums){
            BizException.isNull(payMethodVO.getAccount(),"支付账户");
        }

        //对于用户身份信息
        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);
        //判断资金密码是否正确
        userIdentityService.checkCapitalCipher(payMethodVO.getCapitalCipher(), userIdentity.getCapitalCipher(), userId);

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userId);
        userAccount.setRealName(payMethodVO.getRealName());
        userAccount.setPayType(payMethodVO.getPayType().getNumber());
        if(StringUtils.isNoneEmpty(payMethodVO.getAccount())) {
            userAccount.setAccount(payMethodVO.getAccount());
        }
        if(StringUtils.isNoneEmpty(payMethodVO.getAddress())){
            userAccount.setAddress(payMethodVO.getAddress());
        }
        if(StringUtils.isNoneEmpty(payMethodVO.getBankName())){
            userAccount.setPaymentDetail(payMethodVO.getBankName()+":"+payMethodVO.getBankBranchName());
        }
        Long res = userAccountService.addUserAccount(userAccount);
        return Result.createSuccess(res);
    }


    /**
     * 修改用户账户信息
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "修改账户")
    @RequestMapping(value = "/update-account", method = RequestMethod.POST)
    @CapitalCipherCheck
    public Result<Boolean> updateAccount(@RequestBody Payaccount.PayMethodVO payMethodVO) throws BizException {

        BizException.isNull(payMethodVO.getUserAccountId(),"账户唯一id");
        BizException.isNull(payMethodVO.getCapitalCipher(),"资金密码");

        Long userId = LoginDataHelper.getUserId();
        //对于用户身份信息
        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);
        //判断资金密码是否正确
        userIdentityService.checkCapitalCipher(payMethodVO.getCapitalCipher(), userIdentity.getCapitalCipher(), userId);

        UserAccount userAccount = new UserAccount();
        userAccount.setUserAccountId((long) payMethodVO.getUserAccountId());
        userAccount.setUserId(userId);
        if(StringUtils.isNoneEmpty(payMethodVO.getAccount())) {
            userAccount.setAccount(payMethodVO.getAccount());
        }

        if(StringUtils.isNoneEmpty(payMethodVO.getAddress())){
            userAccount.setAddress(payMethodVO.getAddress());
        }
        if(StringUtils.isNoneEmpty(payMethodVO.getBankName())){
            userAccount.setPaymentDetail(payMethodVO.getBankName()+":"+payMethodVO.getBankBranchName());
        }
        boolean res = userAccountService.updateUserAccount(userAccount);
        return Result.createSuccess(res);
    }
    /**
     * 删除用户账户信息
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "删除用户账户")
    @RequestMapping(value = "/del-account", method = RequestMethod.POST)
    @CapitalCipherCheck
    public Result<Boolean> delAccount(@RequestBody Payaccount.PayMethodVO payMethodVO) throws BizException {
        BizException.isNull(payMethodVO.getUserAccountId(),"账户唯一id");
        BizException.isNull(payMethodVO.getCapitalCipher(),"资金密码");
        Long userId = LoginDataHelper.getUserId();

        //对于用户身份信息
        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);
        //判断资金密码是否正确
        userIdentityService.checkCapitalCipher(payMethodVO.getCapitalCipher(), userIdentity.getCapitalCipher(), userId);

        boolean res = userAccountService.delUserAccount(payMethodVO.getUserAccountId(),userId);
        return Result.createSuccess(res);
    }


    /**
     * 设置用户账户的支付状态
     *  支付状态 0： 关 1：开
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "设置用户账户的支付状态 0： 关 1：开 ")
    @RequestMapping(value = "/change-account-status", method = RequestMethod.POST)
    public Result<Boolean> changeAccountStatus(@RequestBody Payaccount.PayMethodStatusParam payMethodVO) throws BizException {
        Long userId = LoginDataHelper.getUserId();

        BizException.isNull(payMethodVO.getUserAccountId(),"账号的唯一id");
        UserAccount userAccount = new UserAccount();
        userAccount.setUserAccountId(payMethodVO.getUserAccountId());
        userAccount.setPayStatus(payMethodVO.getPayStatusValue());
        boolean res = userAccountService.changeAccountStatus(userAccount);
        return Result.createSuccess(res);
    }
}
