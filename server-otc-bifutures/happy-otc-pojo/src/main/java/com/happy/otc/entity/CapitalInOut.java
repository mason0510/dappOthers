package com.happy.otc.entity;

import java.math.BigDecimal;
import java.util.Date;

public class CapitalInOut {
    /**
     * 
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * 
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 币种id
     *
     * @mbggenerated
     */
    private Long currencyId;

    /**
     * 币种数量
     *
     * @mbggenerated
     */
    private BigDecimal numberCoins;

    /**
     * 币种地址
     *
     * @mbggenerated
     */
    private String currencyAddress;

    /**
     * 交易id
     *
     * @mbggenerated
     */
    private String transactionId;

    /**
     * l类别 1:充币 2：提币
     *
     * @mbggenerated
     */
    private Integer type;

    /**
     * 状态 0:审核中 1：成功 2：失败
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 发送时间
     *
     * @mbggenerated
     */
    private Date sendTime;

    /**
     * 手续费
     *
     * @mbggenerated
     */
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

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }
}