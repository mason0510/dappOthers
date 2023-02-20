package com.happy.otc.entity;

import java.util.Date;

public class CapitalDetailCount {
    /**
     * 
     *
     * @mbggenerated
     */
    private Long capitalDetailCountId;

    /**
     * 用户id
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 成交次数
     *
     * @mbggenerated
     */
    private Long totalTradeCount;

    /**
     * 成功成交次数
     *
     * @mbggenerated
     */
    private Long successTradeCount;

    /**
     * 成交率
     *
     * @mbggenerated
     */
    private Double closeRate;

    /**
     * 
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * 申诉总数
     *
     * @mbggenerated
     */
    private Long appealCount;

    /**
     * 申诉成功次数
     *
     * @mbggenerated
     */
    private Long successAppealCount;

    /**
     * 平均放行时间
     *
     * @mbggenerated
     */
    private Long exchangeHour;

    public Long getCapitalDetailCountId() {
        return capitalDetailCountId;
    }

    public void setCapitalDetailCountId(Long capitalDetailCountId) {
        this.capitalDetailCountId = capitalDetailCountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTotalTradeCount() {
        return totalTradeCount;
    }

    public void setTotalTradeCount(Long totalTradeCount) {
        this.totalTradeCount = totalTradeCount;
    }

    public Long getSuccessTradeCount() {
        return successTradeCount;
    }

    public void setSuccessTradeCount(Long successTradeCount) {
        this.successTradeCount = successTradeCount;
    }

    public Double getCloseRate() {
        return closeRate;
    }

    public void setCloseRate(Double closeRate) {
        this.closeRate = closeRate;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getAppealCount() {
        return appealCount;
    }

    public void setAppealCount(Long appealCount) {
        this.appealCount = appealCount;
    }

    public Long getSuccessAppealCount() {
        return successAppealCount;
    }

    public void setSuccessAppealCount(Long successAppealCount) {
        this.successAppealCount = successAppealCount;
    }

    public Long getExchangeHour() {
        return exchangeHour;
    }

    public void setExchangeHour(Long exchangeHour) {
        this.exchangeHour = exchangeHour;
    }
}