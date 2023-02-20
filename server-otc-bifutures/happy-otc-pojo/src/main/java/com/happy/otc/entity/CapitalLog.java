package com.happy.otc.entity;

import java.math.BigDecimal;
import java.util.Date;

public class CapitalLog {
    /**
     * 主键
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * 用户ID
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 币种ID
     *
     * @mbggenerated
     */
    private Long currencyId;

    /**
     * 类型 1:买入 2:卖出 3:充币 4:提币 5:资金冻结 6:资金解冻
     *
     * @mbggenerated
     */
    private Byte type;

    /**
     * 冻结数量
     *
     * @mbggenerated
     */
    private BigDecimal frozenNumber;

    /**
     * 可用数量
     *
     * @mbggenerated
     */
    private BigDecimal availableNumber;

    /**
     * 备注
     *
     * @mbggenerated
     */
    private String comment;

    /**
     * 订单/商品ID
     *
     * @mbggenerated
     */
    private Long orderId;

    /**
     * 时间
     *
     * @mbggenerated
     */
    private Date createTime;

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

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public BigDecimal getFrozenNumber() {
        return frozenNumber;
    }

    public void setFrozenNumber(BigDecimal frozenNumber) {
        this.frozenNumber = frozenNumber;
    }

    public BigDecimal getAvailableNumber() {
        return availableNumber;
    }

    public void setAvailableNumber(BigDecimal availableNumber) {
        this.availableNumber = availableNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}