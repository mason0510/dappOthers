package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

public class CapitalLogInfoVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "币种ID")
    private Long currencyId;

    @ApiModelProperty(value = "类型 1:买入 2:卖出 3:充币 4:提币 5:资金冻结 6:资金解冻")
    private Byte type;

    @ApiModelProperty(value = "冻结数量")
    private BigDecimal frozenNumber;

    @ApiModelProperty(value = "可用数量")
    private BigDecimal availableNumber;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "订单/商品ID")
    private Long orderId;

    @ApiModelProperty(value = "时间")
    private Date createTime;

    @ApiModelProperty(value = "币种")
    private String kind;

    @ApiModelProperty(value = "提币地址")
    private String address;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "对家用户名")
    private String targetUsername;

    @ApiModelProperty(value = "法币币种")
    private String relevantKind;

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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public String getRelevantKind() {
        return relevantKind;
    }

    public void setRelevantKind(String relevantKind) {
        this.relevantKind = relevantKind;
    }
}
