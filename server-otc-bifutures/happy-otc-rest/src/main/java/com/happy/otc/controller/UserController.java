package com.happy.otc.controller;

import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.utils.RedisUtil;
import com.bitan.common.vo.Result;
import com.bitan.oauth.vo.LoginRequest;
import com.happy.otc.bifutures.vo.BiFuturesOtc;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.CapitalDetailCount;
import com.happy.otc.entity.UserAccount;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.enums.IdentityStautsEnum;
import com.happy.otc.proto.Account;
import com.happy.otc.service.*;
import com.happy.otc.service.bifutures.UserAssetsService;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "用户Api")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ICapitalDetailCountService capitalDetailCountService;
    @Autowired
    private IUserIdentityService userIdentityService;
    @Autowired
    private IOauthService oauthService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    ICapitalService capitalService;
    @Autowired
    IUserAccountService userAccountService;

    @Value("${env}")
    private String env;
    /**
     * 获取单个用户信息
     *
     * @param userId
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "获取单个用户信息")
    @RequestMapping(value = "/user-info", method = RequestMethod.GET)
    public Result<UserShowInfoVO> getUserInfo(@RequestParam("userId") Long userId) throws BizException {
        UserDTO userDTO = userService.getUserInfoByUserId(userId);
        if (userDTO == null) {
            BizException.fail(MessageCode.NO_USER, "该用户不存在");
        }
        CapitalDetailCount capitalDetailCount = capitalDetailCountService.getCapitalDetailCountInfo(userId);
        UserShowInfoVO userShowInfoVO = new UserShowInfoVO();
        userShowInfoVO.setUserName(userDTO.getUserName());
        userShowInfoVO.setMobile(userDTO.getMobile());
        userShowInfoVO.setEmail(userDTO.getEmail());
        userShowInfoVO.setCloseRate(capitalDetailCount.getCloseRate());
        userShowInfoVO.setTotalTradeCount(capitalDetailCount.getTotalTradeCount());
        userShowInfoVO.setExchangeHour(capitalDetailCount.getExchangeHour());

        UserIdentityVO userIdentityVO = userIdentityService.getByUserId(userId);
        userShowInfoVO.setIdentificationStatus(userIdentityVO.getStatus().intValue());
        return Result.createSuccess(userShowInfoVO);
    }


    /**
     * 获取用户中心信息
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "获取用户中心信息")
    @RequestMapping(value = "/user-center-info", method = RequestMethod.GET)
    public Result<UserCenterInfoVO> getUserCenterInfo() throws BizException, ParserConfigurationException, SAXException, ParseException, IOException {

        Long userId = LoginDataHelper.getUserId();

        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);
        if (userIdentity == null){
            BizException.fail(MessageCode.NO_USER, "该用户不存在");
        }
        UserCenterInfoVO userCenterInfoVO = new UserCenterInfoVO();
        userCenterInfoVO.setTotalCapital(capitalService.buildFundVO( userId ).getAssets());
        userCenterInfoVO.setSellerStatus(userIdentity.getUserOtcType() == 1);
        userCenterInfoVO.setRealStatus((int)userIdentity.getStatus());
        userCenterInfoVO.setCapitalPasswordStatus(StringUtils.isEmpty(userIdentity.getCapitalCipher())? false : true);
        return Result.createSuccess(userCenterInfoVO);
    }

    /**
     * 修改资金密码
     *
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "修改资金密码")
    @RequestMapping(value = "/upload-user-extend-info", method = RequestMethod.POST)
    public Result<Boolean> updateUserExtendInfo(@RequestBody Account.UpdateCapitalCipherParamVO updateCapitalCipherParamVO) throws BizException {

        BizException.isNull(updateCapitalCipherParamVO.getCapitalCipher(),"资金密码");
        BizException.isNull(updateCapitalCipherParamVO.getCaptcha(),"验证码");
        BizException.isNull(updateCapitalCipherParamVO.getMobile(),"手机号");
        Long userId = LoginDataHelper.getUserId();
        boolean result = userIdentityService.addUserExtendIdentity(userId,updateCapitalCipherParamVO);
        return Result.createSuccess(result);
    }

    /**
     * 用户注册
     *
     * @param userOtcRequest
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result<UserOtcInfoVO> register(@ApiParam("应用编号") @RequestHeader("applicationId") Long applicationId,
                                          @ApiParam("客户端类型 1 web 2 ios 3 android") @RequestHeader("applicationClientType") Integer applicationClientType,
                                          @ApiParam("设备UUID") @RequestHeader("deviceUUID") String deviceUUID,
                                          @ApiParam("用户来源") @RequestHeader("userFrom") String userFrom,
                                          @RequestBody RegisterOtcUserRequest userOtcRequest) throws BizException {
        BizException.isNull(userFrom, "用户来源");
        BizException.isNull(userOtcRequest.getMobile(), "手机号");
        BizException.isNull(userOtcRequest.getCaptcha(), "验证码");
        BizException.isNull(userOtcRequest.getPassword(), "密码");
        BizException.isNull(userOtcRequest.getUserName(), "用户名昵称");
        //BizException.isNull(userOtcRequest.getCapitalCipher(),"资金密码");

        if (userOtcRequest.getPassword().equals(userOtcRequest.getCapitalCipher())) {
            BizException.fail(MessageCode.PASSWORD_REPAT_ERR, "登录密码不可与资金密码一致");
        }

        //校验昵称是否合规格
        if (userOtcRequest.getUserName().length() > 20) {
            BizException.fail(MessageCode.NICK_NAME_ERR, "昵称格式不正确");
        }
        if (userOtcRequest.getUserName().startsWith(" ") || userOtcRequest.getUserName().endsWith(" ")) {
            BizException.fail(MessageCode.NICK_NAME_ERR, "昵称格式不正确");
        }
        char[] charArray = userOtcRequest.getUserName().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            //支持中英文、数字、空格任意组合，字母要区分大小写
            if (!((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb) || (charArray[i] >= 0x30 && charArray[i] <= 0x39) ||
                    (charArray[i] >= 0x41 && charArray[i] <= 0x5a) || (charArray[i] >= 0x61 && charArray[i] <= 0x7a) ||
                    charArray[i] == 0x20)) {
                BizException.fail(MessageCode.NICK_NAME_ERR, "昵称格式不正确");
            }
        }

        UserOtcInfoVO userInfoVO = userService.doRegister(applicationId, applicationClientType, deviceUUID, userFrom, userOtcRequest);

        return Result.createSuccess(userInfoVO);
    }


    /**
     * 登录
     *
     * @param loginRequest
     * @return
     * @throws BizException
     */
    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<UserOtcInfoVO> login(@ApiParam("应用编号") @RequestHeader("applicationId") Long applicationId,
                               @ApiParam("客户端类型 1 web 2 ios 3 android") @RequestHeader("applicationClientType") Integer applicationClientType,
                               @ApiParam("设备UUID") @RequestHeader("deviceUUID") String deviceUUID,
                               @RequestBody LoginRequest loginRequest) throws BizException {

        BizException.isNull(loginRequest.getAccount(), "手机号");
        BizException.isNull(loginRequest.getPassword(), "密码");
        BizException.isNull(loginRequest.getCountryCode(),"国家编号");

        UserOtcInfoVO userInfoVO = userService.doLogin(applicationId,applicationClientType,deviceUUID,loginRequest);

        return Result.createSuccess(userInfoVO);
    }

    @ApiOperation("获取客服群组")
    @GetMapping("/service-group")
    public Result<String> getServiceGroup() {
        Long uerId = LoginDataHelper.getUserId();
        String groupId = userService.getCustomerServiceGroup(uerId);

        return Result.createSuccess(groupId);
    }

    @ApiOperation(value = "修改密码")
    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public Result<Boolean> updatePassword(@ApiParam("应用编号") @RequestHeader("applicationId") Long applicationId,
                                          @ApiParam("国家编号") @RequestParam(value = "countryCode", required = false) String countryCode,
                                          @ApiParam("账号") @RequestParam("account") String account,
                                          @ApiParam("新密码") @RequestParam("newPassword") String newPassword,
                                          @ApiParam("旧密码") @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                          @ApiParam("验证码") @RequestParam(value = "captcha", required = false) String captcha) throws BizException {
        BizException.isNull(account, "初始密码");
        BizException.isNull(newPassword, "新密码");
//        BizException.isNull(oldPassword, "旧密码");
        BizException.isNull(captcha, "验证码");
//        BizException.isNull(countryCode, "国家编号");

        Boolean result = userService.updatePassword(applicationId, countryCode, account, newPassword, oldPassword, captcha);

        return Result.createSuccess(result);
    }

    @ApiOperation(value = "忘记密码")
    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public Result<Boolean> forgotPassword(@ApiParam("国家编号") @RequestParam("countryCode") String countryCode,
                                          @ApiParam("账号") @RequestParam("account") String account,
                                          @ApiParam("新密码") @RequestParam("password") String password,
                                          @ApiParam("验证码") @RequestParam(value = "captcha") String captcha) throws BizException {
        BizException.isNull(account, "账号");
        BizException.isNull(password, "密码");
        BizException.isNull(captcha, "验证码");
        BizException.isNull(countryCode, "国家编号");

        Boolean result = userService.forgotPassword(countryCode, account, password, captcha);

        return Result.createSuccess(result);
    }

    @ApiOperation(value = "资金密码设置")
    @RequestMapping(value = "/set-user-extend-info", method = RequestMethod.POST)
    public Result<UserOtcInfoVO> setUserExtendInfo(@ApiParam("应用编号") @RequestHeader("applicationId") Long applicationId,
                                                   @ApiParam("客户端类型 1 web 2 ios 3 android") @RequestHeader("applicationClientType") Integer applicationClientType,
                                                   @ApiParam("设备UUID") @RequestHeader("deviceUUID") String deviceUUID,
                                                   @RequestBody RegisterOtcUserRequest userOtcRequest) throws BizException {

        BizException.isNull(userOtcRequest.getMobile(), "手机号");
        BizException.isNull(userOtcRequest.getPassword(), "密码");
        BizException.isNull(userOtcRequest.getCaptcha(), "验证码");
        BizException.isNull(userOtcRequest.getUserName(), "昵称");
        BizException.isNull(userOtcRequest.getCapitalCipher(), "资金密码");

        //校验昵称是否合规格
        if (userOtcRequest.getUserName().length() > 20) {
            BizException.fail(MessageCode.NICK_NAME_ERR, "昵称格式不正确");
        }
        if (userOtcRequest.getUserName().startsWith(" ") || userOtcRequest.getUserName().endsWith(" ")) {
            BizException.fail(MessageCode.NICK_NAME_ERR, "昵称格式不正确");
        }
        char[] charArray = userOtcRequest.getUserName().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            //支持中英文、数字、空格任意组合，字母要区分大小写
            if (!((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb) || (charArray[i] >= 0x30 && charArray[i] <= 0x39) ||
                    (charArray[i] >= 0x41 && charArray[i] <= 0x5a) || (charArray[i] >= 0x61 && charArray[i] <= 0x7a) ||
                    charArray[i] == 0x20)) {
                BizException.fail(MessageCode.NICK_NAME_ERR, "昵称格式不正确");
            }
        }

        UserOtcInfoVO userInfoVO = userService.setUserExtendInfo(applicationId, applicationClientType, deviceUUID, userOtcRequest);

        return Result.createSuccess(userInfoVO);
    }

    @ApiOperation(value = "获取昵称")
    @RequestMapping(value = "/get-nickname", method = RequestMethod.GET)
    public Result<String> getNickname(@ApiParam("环信用户ID") @RequestParam("easemobUsername") String easemobUsername) {
        BizException.isNull(easemobUsername, "环信用户ID");
        String nickname = userService.getNicknameByEasemobUsername(easemobUsername);
        return Result.createSuccess(nickname);
    }


    @ApiOperation(value = "是否已设置资金密码")
    @RequestMapping(value = "/owned-capitalCipher", method = RequestMethod.GET)
    public Result<Boolean> ownedCapitalCipher() {
        Long userId = LoginDataHelper.getUserId();

        UserIdentity userIdentityVO = userIdentityService.getUserExtendIdentity(userId);
        if (StringUtils.isEmpty(userIdentityVO.getCapitalCipher())){
            return Result.createSuccess(false);
        }
        return Result.createSuccess(true);
    }
}
