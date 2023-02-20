package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class UserShowInfoVO implements Serializable {

    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "手机")
    private String mobile;
    @ApiModelProperty(value = "名称")
    private String userName;
    @ApiModelProperty(value = "0:未认证 1:待认证 2:已认证 3:认证驳回")
    private Integer identificationStatus;

    @ApiModelProperty(value = "交易次数")
    private Long totalTradeCount;
    @ApiModelProperty(value = "成交率")
    private Double closeRate;
    @ApiModelProperty(value = "平均放行时间")
    private Long exchangeHour;

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

    public Integer getIdentificationStatus() {
        return identificationStatus;
    }

    public void setIdentificationStatus(Integer identificationStatus) {
        this.identificationStatus = identificationStatus;
    }

    public Long getTotalTradeCount() {
        return totalTradeCount;
    }

    public void setTotalTradeCount(Long totalTradeCount) {
        this.totalTradeCount = totalTradeCount;
    }

    public Double getCloseRate() {
        return closeRate;
    }

    public void setCloseRate(Double closeRate) {
        this.closeRate = closeRate;
    }

    public Long getExchangeHour() {
        return exchangeHour;
    }

    public void setExchangeHour(Long exchangeHour) {
        this.exchangeHour = exchangeHour;
    }
}
