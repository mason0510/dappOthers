package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class RegisterOtcUserRequest implements Serializable {
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("手机")
    private String mobile;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("性别 1男2女")
    private Integer gender;
    @ApiModelProperty("国家代码")
    private String countryCode;
    @ApiModelProperty("用户角色 2普通用户 4经纪人")
    private Integer userRole;
    @ApiModelProperty("验证码")
    private String captcha;
    @ApiModelProperty("下载渠道")
    private String downloadChannel;
    @ApiModelProperty("资金密码")
    private String capitalCipher;
    @ApiModelProperty("语言 1 cn ,2 en")
    private Integer languageType;

    public RegisterOtcUserRequest() {
    }

    public Integer getLanguageType() {
        return languageType;
    }

    public void setLanguageType(Integer languageType) {
        this.languageType = languageType;
    }

    public String getCapitalCipher() {
        return capitalCipher;
    }

    public void setCapitalCipher(String capitalCipher) {
        this.capitalCipher = capitalCipher;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getGender() {
        return this.gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getDownloadChannel() {
        return this.downloadChannel;
    }

    public void setDownloadChannel(String downloadChannel) {
        this.downloadChannel = downloadChannel;
    }

    public Integer getUserRole() {
        return this.userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCaptcha() {
        return this.captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
