package com.happy.otc.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户手机号
     */
    private String mobile;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 国家编号
     */
    private String countryCode;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
