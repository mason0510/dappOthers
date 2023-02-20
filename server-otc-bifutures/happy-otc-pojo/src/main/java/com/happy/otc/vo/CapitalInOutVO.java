package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CapitalInOutVO implements Serializable {

    @ApiModelProperty(value = "充提币交易id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "币种id")
    private Long currencyId;

    @ApiModelProperty(value = "币种数量")
    private BigDecimal numberCoins;

    @ApiModelProperty(value = "币种地址")
    private String currencyAddress;

    @ApiModelProperty(value = "交易id")
    private String transactionId;

    @ApiModelProperty(value = "类别 1:充币 2：提币")
    private Integer type;

    @ApiModelProperty(value = "状态 0:审核中 1：成功 2：失败")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "发送时间")
    private Date sendTime;

    @ApiModelProperty(value = "币种简称")
    private String currencySimpleName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "手续费")
    private BigDecimal serviceFee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public BigDecimal getNumberCoins() {
        return numberCoins;
    }

    public void setNumberCoins(BigDecimal numberCoins) {
        this.numberCoins = numberCoins;
    }

    public String getCurrencyAddress() {
        return currencyAddress;
    }

    public void setCurrencyAddress(String currencyAddress) {
        this.currencyAddress = currencyAddress;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getCurrencySimpleName() {
        return currencySimpleName;
    }

    public void setCurrencySimpleName(String currencySimpleName) {
        this.currencySimpleName = currencySimpleName;
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

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }
}
