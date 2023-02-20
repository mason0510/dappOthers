package com.happy.otc.service;

import com.bitan.oauth.vo.LoginRequest;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.vo.RegisterOtcUserRequest;
import com.happy.otc.vo.UserOtcInfoVO;

import java.util.List;
import java.util.Map;

public interface IUserService {

    public UserOtcInfoVO doRegister(Long applicationId, Integer applicationClientType, String deviceUUID, String userFrom, RegisterOtcUserRequest userOtcRequest);

    public UserOtcInfoVO doLogin(Long applicationId, Integer applicationClientType, String deviceUUID, LoginRequest loginRequest);

    String getCustomerServiceGroup(Long userId);

    Boolean updatePassword(Long applicationId, String countryCode, String account, String newPassword, String oldPassword, String captcha);

    Boolean forgotPassword(String countryCode, String account, String password, String captcha);

    UserOtcInfoVO setUserExtendInfo(Long applicationId, Integer applicationClientType, String deviceUUID, RegisterOtcUserRequest userOtcRequest);

    UserDTO getUserInfoByUserId(Long userId);

    Map<Long, UserDTO> UserInfoByUserIds(List<Long> userIds);

    String getNicknameByEasemobUsername(String easemobUsername);
}
