package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by niyang on 2017/11/1.
 */
public class UserInfoVO implements Serializable {

    @ApiModelProperty("用户编号")
    private Long userId;
    @ApiModelProperty("用户邮箱")
    private String email;
    @ApiModelProperty("用户手机")
    private String mobile;
    @ApiModelProperty("用户是否设置密码")
    private Boolean userHavePassword;
    @ApiModelProperty("用户名称")
    private String userName;
    @ApiModelProperty("用户状态 0正常")
    private Integer status;
    @ApiModelProperty("用户扩展信息")
    private Map<String, Object> userAccountExtend;
    @ApiModelProperty("TOKEN")
    private String token;
    @ApiModelProperty("Token有效时间，单位秒")
    private Long validTime;

    public Map<String, Object> getUserAccountExtend() {
        return userAccountExtend;
    }

    public void setUserAccountExtend(Map<String, Object> userAccountExtend) {
        this.userAccountExtend = userAccountExtend;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getValidTime() {
        return validTime;
    }

    public void setValidTime(Long validTime) {
        this.validTime = validTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getUserHavePassword() {
        return userHavePassword;
    }

    public void setUserHavePassword(Boolean userHavePassword) {
        this.userHavePassword = userHavePassword;
    }

}
