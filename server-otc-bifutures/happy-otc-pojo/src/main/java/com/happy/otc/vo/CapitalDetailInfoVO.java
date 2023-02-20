package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CapitalDetailInfoVO implements Serializable {

    @ApiModelProperty(value = "商品id")
    private Long commodityId;
    @ApiModelProperty(value = "币种的金额")
    private BigDecimal currencyPrice;
    @ApiModelProperty(value = "种类")
    private String kind;
    @ApiModelProperty(value = "商品类别 0：在买 1：在卖")
    private Integer commodityType;
    @ApiModelProperty(value = "交易详情id")
    private Long capitalDetailId;
    @ApiModelProperty(value = "买家用户id")
    private Long buyUserId;
    @ApiModelProperty(value = "卖家用户id")
    private Long sellUserId;
    @ApiModelProperty(value = "状态 1:未付款，2：已付款，3：申诉中，4：已取消，5：已完成")
    private Integer status;
    @ApiModelProperty(value = "交易法币金额")
    private BigDecimal transactionAmount;
    @ApiModelProperty(value = "交易量")
    private BigDecimal transactionVolume;
    @ApiModelProperty(value = "交易价格")
    private BigDecimal transactionPrice;
    @ApiModelProperty(value = "创建时间")
    private Date crateTime;
    @ApiModelProperty(value = "用户名")
    private String nickName;
    @ApiModelProperty(value = "订单号")
    private String orderNumber;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "参考号")
    private String referenceNumber;
    @ApiModelProperty(value = "现关联种类")
    private String relevantKind;
    @ApiModelProperty(value = "支付时间")
    private Date payTime;
    @ApiModelProperty(value = "环信群组编号")
    private String easemobGroupId;
    @ApiModelProperty(value = "币种id")
    private Long currencyId;
    @ApiModelProperty(value = "手续费")
    private BigDecimal serviceFee;
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    @ApiModelProperty(value = "自动回复内容")
    private String leaveMessage;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getTradeTimeType() {
        return tradeTimeType;
    }

    public void setTradeTimeType(Integer tradeTimeType) {
        this.tradeTimeType = tradeTimeType;
    }

    @ApiModelProperty(value = "交易时间限制类别 ")
    private Integer tradeTimeType;

    public String getRelevantKind() {
        return relevantKind;
    }

    public void setRelevantKind(String relevantKind) {
        this.relevantKind = relevantKind;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public BigDecimal getCurrencyPrice() {
        return currencyPrice;
    }

    public void setCurrencyPrice(BigDecimal currencyPrice) {
        this.currencyPrice = currencyPrice;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(Integer commodityType) {
        this.commodityType = commodityType;
    }

    public Long getCapitalDetailId() {
        return capitalDetailId;
    }

    public void setCapitalDetailId(Long capitalDetailId) {
        this.capitalDetailId = capitalDetailId;
    }

    public Long getBuyUserId() {
        return buyUserId;
    }

    public void setBuyUserId(Long buyUserId) {
        this.buyUserId = buyUserId;
    }

    public Long getSellUserId() {
        return sellUserId;
    }

    public void setSellUserId(Long sellUserId) {
        this.sellUserId = sellUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getTransactionVolume() {
        return transactionVolume;
    }

    public void setTransactionVolume(BigDecimal transactionVolume) {
        this.transactionVolume = transactionVolume;
    }

    public BigDecimal getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(BigDecimal transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public Date getCrateTime() {
        return crateTime;
    }

    public void setCrateTime(Date crateTime) {
        this.crateTime = crateTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getEasemobGroupId() {
        return easemobGroupId;
    }

    public void setEasemobGroupId(String easemobGroupId) {
        this.easemobGroupId = easemobGroupId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }
}
