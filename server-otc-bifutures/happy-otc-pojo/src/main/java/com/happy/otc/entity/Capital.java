package com.happy.otc.entity;

import java.math.BigDecimal;

public class Capital {
    /**
     * 
     *
     * @mbggenerated
     */
    private Long capitalId;

    /**
     * 用户id
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 货币id
     *
     * @mbggenerated
     */
    private Long currencyId;

    /**
     * 拥有货币数量
     *
     * @mbggenerated
     */
    private BigDecimal capitalBalance;

    /**
     * 可用货币数
     *
     * @mbggenerated
     */
    private BigDecimal capitalAvailable;

    /**
     * 冻结的货币数
     *
     * @mbggenerated
     */
    private BigDecimal capitalFrozen;

    /**
     * 存入货币数
     *
     * @mbggenerated
     */
    private BigDecimal capitalIn;

    /**
     * 取出货币数
     *
     * @mbggenerated
     */
    private BigDecimal capitalOut;

    /**
     * 防止并发的版本号记录,用来处理乐观锁
     *
     * @mbggenerated
     */
    private Integer version;

    public Long getCapitalId() {
        return capitalId;
    }

    public void setCapitalId(Long capitalId) {
        this.capitalId = capitalId;
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

    public BigDecimal getCapitalBalance() {
        return capitalBalance;
    }

    public void setCapitalBalance(BigDecimal capitalBalance) {
        this.capitalBalance = capitalBalance;
    }

    public BigDecimal getCapitalAvailable() {
        return capitalAvailable;
    }

    public void setCapitalAvailable(BigDecimal capitalAvailable) {
        this.capitalAvailable = capitalAvailable;
    }

    public BigDecimal getCapitalFrozen() {
        return capitalFrozen;
    }

    public void setCapitalFrozen(BigDecimal capitalFrozen) {
        this.capitalFrozen = capitalFrozen;
    }

    public BigDecimal getCapitalIn() {
        return capitalIn;
    }

    public void setCapitalIn(BigDecimal capitalIn) {
        this.capitalIn = capitalIn;
    }

    public BigDecimal getCapitalOut() {
        return capitalOut;
    }

    public void setCapitalOut(BigDecimal capitalOut) {
        this.capitalOut = capitalOut;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}