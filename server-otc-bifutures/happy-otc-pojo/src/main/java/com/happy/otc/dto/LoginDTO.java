package com.happy.otc.dto;

import com.happy.otc.enums.ApplicationClientTypeEnum;
import com.happy.otc.enums.LoginMethodEnum;

import java.io.Serializable;

/**
 * Created by niyang on 2017/10/30.
 */
public class LoginDTO implements Serializable {

    private LoginMethodEnum loginMethodEnum;
    private String account;
    private String password;

    private Long applicationId;
    private ApplicationClientTypeEnum applicationClientTypeEnum;
    private String deviceUUID;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public ApplicationClientTypeEnum getApplicationClientTypeEnum() {
        return applicationClientTypeEnum;
    }

    public void setApplicationClientTypeEnum(ApplicationClientTypeEnum applicationClientTypeEnum) {
        this.applicationClientTypeEnum = applicationClientTypeEnum;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }

    public LoginMethodEnum getLoginMethodEnum() {
        return loginMethodEnum;
    }

    public void setLoginMethodEnum(LoginMethodEnum loginMethodEnum) {
        this.loginMethodEnum = loginMethodEnum;
    }
}
