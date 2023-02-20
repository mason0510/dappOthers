package com.happy.otc.service.remote;

import com.bitan.common.exception.BizException;
import com.bitan.common.vo.Result;
import com.bitan.oauth.entity.User;
import com.bitan.oauth.vo.LoginRequest;
import com.bitan.oauth.vo.RegisterUserRequest;
import com.bitan.oauth.vo.UserExtendInfoVO;
import com.bitan.oauth.vo.UserInfoVO;
import com.bitan.oauth.vo.manager.SearchUserRequest;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by niyang on 2017/8/11.
 */
@FeignClient("oauth-server")
@RequestMapping(value = "/oauth-rest")
public interface IOauthService {

    /**
     * 用户信息
     *
     * @param userId
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/user-info", method = RequestMethod.GET)
    public Result<UserInfoVO> getUserInfo(@RequestParam("userId") Long userId) throws BizException;
    /**
     * 【feign】批量获取用户信息
     *
     * @param userIdList
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/user-list", method = RequestMethod.POST)
    public Result<Map<Long, UserInfoVO>> getUserInfoByUserIdList(@RequestParam("userIdList") List<Long> userIdList) throws BizException;
    /**
     * 完善个人信息
     *
     * @param userExtendInfoVO
     * @return
     */
    @RequestMapping(value = "/upload-user-extend-info", method = RequestMethod.POST)
    public Result<Boolean> updateUserExtendInfo(@RequestBody UserExtendInfoVO userExtendInfoVO) ;

    /**
     * 【fegin】搜索用户
     *
     * @param searchUserRequest
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/search-user", method = RequestMethod.POST)
    public Result<List<User>> searchUser(@RequestBody SearchUserRequest searchUserRequest) throws BizException;

    @RequestMapping(value = "/registerOtc", method = RequestMethod.POST)
    public Result<UserInfoVO> registerOtc(@ApiParam("应用编号") @RequestHeader("applicationId") Long applicationId,
                                          @ApiParam("客户端类型 1 web 2 ios 3 android") @RequestHeader("applicationClientType") Integer applicationClientType,
                                          @ApiParam("设备UUID") @RequestHeader("deviceUUID") String deviceUUID,
                                          @ApiParam("用户来源") @RequestHeader("userFrom") String userFrom,
                                          @RequestBody RegisterUserRequest userRequest) throws BizException;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<UserInfoVO> login(@ApiParam("应用编号") @RequestHeader("applicationId") Long applicationId,
                                    @ApiParam("客户端类型 1 web 2 ios 3 android") @RequestHeader("applicationClientType") Integer applicationClientType,
                                    @ApiParam("设备UUID") @RequestHeader("deviceUUID") String deviceUUID,
                                    @RequestBody LoginRequest loginRequest) throws BizException;

    /**
     * 根据手机号获取用户信息
     * @param mobile 手机号
     * @return
     */
    @RequestMapping(value = "/search-mobile", method = RequestMethod.POST)
    public Result<UserInfoVO> searchUserByMobile(@ApiParam("手机号") @RequestParam("mobile") String mobile);

    /**
     * 判断密码是否重复
     * @param userId
     * @param password
     * @return
     */
    @GetMapping("/check-password-repeat")
    Result<Boolean> checkPasswordRepeat(@ApiParam("用户ID") @RequestParam("userId") Long userId,
                                        @ApiParam("password") @RequestParam("password") String password);


    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
    public Result<Boolean> forgotPassword(@ApiParam("账号") @RequestParam("account") String account,
                                          @ApiParam("新密码") @RequestParam("password") String password,
                                          @ApiParam("验证码") @RequestParam("captcha") String captcha) throws BizException;

    /**
     * 修改密码
     */
    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    Result<Boolean> updatePassword(@ApiParam("应用编号") @RequestHeader("applicationId") Long applicationId,
                                   @ApiParam("账号") @RequestParam("account") String account,
                                   @ApiParam("新密码") @RequestParam("newPassword") String newPassword,
                                   @ApiParam("旧密码") @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                   @ApiParam("验证码") @RequestParam(value = "captcha", required = false) String captcha) throws BizException;

    /**
     * 完善OTC用户信息
     * @param applicationId
     * @param applicationClientType
     * @param deviceUUID
     * @param userRequest
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/set-otc-user-info", method = RequestMethod.POST)
    Result<UserInfoVO> setOtcUserInfo(@ApiParam("应用编号") @RequestHeader("applicationId") Long applicationId,
                                      @ApiParam("客户端类型 1 web 2 ios 3 android") @RequestHeader("applicationClientType") Integer applicationClientType,
                                      @ApiParam("设备UUID") @RequestHeader("deviceUUID") String deviceUUID,
                                      @RequestBody RegisterUserRequest userRequest) throws BizException;
}
