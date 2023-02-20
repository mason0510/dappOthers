package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

public class UserOtcInfoVO implements Serializable {

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
    @ApiModelProperty("性别 1男 2女")
    private Integer gender;
    @ApiModelProperty("用户状态 0正常")
    private Integer status;
    @ApiModelProperty("TOKEN")
    private String token;
    @ApiModelProperty("Token有效时间，单位秒")
    private Long validTime;
    @ApiModelProperty("头像")
    private String userAvatar;

    @ApiModelProperty(value = "用户角色")
    private Integer userRole;

    @ApiModelProperty("用户扩展信息")
    private Map<String, Object> userAccountExtend;

    @ApiModelProperty("认证状态")
    private Integer identityStatus;
    @ApiModelProperty("资金密码")
    private boolean capitalPasswordStatus;

    @ApiModelProperty("OTC用户类别 0:普通客户 1:商户")
    private Integer otcType;

    public boolean isCapitalPasswordStatus() {
        return capitalPasswordStatus;
    }

    public void setCapitalPasswordStatus(boolean capitalPasswordStatus) {
        this.capitalPasswordStatus = capitalPasswordStatus;
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Map<String, Object> getUserAccountExtend() {
        return userAccountExtend;
    }

    public void setUserAccountExtend(Map<String, Object> userAccountExtend) {
        this.userAccountExtend = userAccountExtend;
    }

    public Boolean getUserHavePassword() {
        return userHavePassword;
    }

    public void setUserHavePassword(Boolean userHavePassword) {
        this.userHavePassword = userHavePassword;
    }

    public Integer getUserRole() {
        return userRole;
    }

    public void setUserRole(Integer userRole) {
        this.userRole = userRole;
    }

    public Integer getIdentityStatus() {
        return identityStatus;
    }

    public void setIdentityStatus(Integer identityStatus) {
        this.identityStatus = identityStatus;
    }

    public Integer getOtcType() {
        return otcType;
    }

    public void setOtcType(Integer otcType) {
        this.otcType = otcType;
    }
}
