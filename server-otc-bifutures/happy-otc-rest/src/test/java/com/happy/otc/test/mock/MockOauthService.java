package com.happy.otc.test.mock;

import com.bitan.common.exception.BizException;
import com.bitan.common.vo.Result;
import com.bitan.message.vo.SendOtcMessageRequest;
import com.bitan.oauth.entity.User;
import com.bitan.oauth.vo.LoginRequest;
import com.bitan.oauth.vo.RegisterUserRequest;
import com.bitan.oauth.vo.UserExtendInfoVO;
import com.bitan.oauth.vo.UserInfoVO;
import com.bitan.oauth.vo.manager.SearchUserRequest;
import com.happy.otc.service.remote.IOauthService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockOauthService implements IOauthService {
    public List<SendOtcMessageRequest> otcRequest = new ArrayList<>();

    public void clear() {
        otcRequest.clear();
    }

    @Override
    public Result<UserInfoVO> getUserInfo(Long userId) throws BizException {
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserId( Long.valueOf( userId ));
        userInfoVO.setMobile(userId + "1234567" );
        userInfoVO.setCountryCode( "+86" );
        userInfoVO.setEmail("232");
        userInfoVO.setUserName( "otc"+userId );
        return Result.createSuccess(userInfoVO);
    }

    @Override
    public Result<Map<Long, UserInfoVO>> getUserInfoByUserIdList(List<Long> userIdList) throws BizException {
        Map<Long, UserInfoVO> map = new HashMap<>();
        UserInfoVO userInfoVO = new UserInfoVO();
        for (int i=0; i<100; i++) {
            userInfoVO.setUserId( Long.valueOf( i ));
            userInfoVO.setMobile( i + "1234567" );
            userInfoVO.setCountryCode( "+86" );
            userInfoVO.setEmail("232");
            userInfoVO.setUserName( "otc"+i );
            map.put( Long.valueOf( i ), userInfoVO);
        }

        return Result.createSuccess(map);
    }

    @Override
    public Result<Boolean> updateUserExtendInfo(UserExtendInfoVO userExtendInfoVO) {
        return null;
    }

    @Override
    public Result<List<User>> searchUser(SearchUserRequest searchUserRequest) throws BizException {
        return null;
    }

    @Override
    public Result<UserInfoVO> registerOtc(Long applicationId, Integer applicationClientType, String deviceUUID, String userFrom, RegisterUserRequest userRequest) throws BizException {
        return null;
    }

    @Override
    public Result<UserInfoVO> login(Long applicationId, Integer applicationClientType, String deviceUUID, LoginRequest loginRequest) throws BizException {
        return null;
    }

    @Override
    public Result<UserInfoVO> searchUserByMobile(String mobile) {
        return null;
    }

    @Override
    public Result<Boolean> checkPasswordRepeat(Long userId, String password) {
        return null;
    }

    @Override
    public Result<Boolean> updatePassword(Long applicationId, String account, String newPassword, String oldPassword, String captcha) throws BizException {
        return null;
    }

    @Override
    public Result<UserInfoVO> setOtcUserInfo(Long applicationId, Integer applicationClientType, String deviceUUID, RegisterUserRequest userRequest) throws BizException {
        return null;
    }

    @Override
    public Result<Boolean> forgotPassword(String account, String password, String captcha) throws BizException {
        return null;
    }
}
