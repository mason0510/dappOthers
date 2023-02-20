package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


public class UserExtendInfo implements Serializable {
    @ApiModelProperty(value = "资金密码")
    private String capitalCipher;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty("手机")
    private String mobile;
    @ApiModelProperty("验证码")
    private String captcha;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCapitalCipher() {
        return capitalCipher;
    }

    public void setCapitalCipher(String capitalCipher) {
        this.capitalCipher = capitalCipher;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}