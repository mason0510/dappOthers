package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    @ApiModelProperty("登录方式 1手机 2邮箱")
    private Integer loginMethod;
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("密码")
    private String password;

    public Integer getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(Integer loginMethod) {
        this.loginMethod = loginMethod;
    }

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
}
