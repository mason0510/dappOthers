package com.happy.otc.entity;

import java.math.BigDecimal;

public class FeeRule {
    /**
     * 主键
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * 类别 1:出售 2:收购 3:提币
     *
     * @mbggenerated
     */
    private Integer type;

    /**
     * 币种
     *
     * @mbggenerated
     */
    private Long currencyId;

    /**
     * 收费方式 1:固定收费 2:按比例收费
     *
     * @mbggenerated
     */
    private Byte feeType;

    /**
     * 收费额度
     *
     * @mbggenerated
     */
    private BigDecimal feeNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Byte getFeeType() {
        return feeType;
    }

    public void setFeeType(Byte feeType) {
        this.feeType = feeType;
    }

    public BigDecimal getFeeNumber() {
        return feeNumber;
    }

    public void setFeeNumber(BigDecimal feeNumber) {
        this.feeNumber = feeNumber;
    }
}