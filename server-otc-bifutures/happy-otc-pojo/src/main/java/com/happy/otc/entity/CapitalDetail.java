package com.happy.otc.entity;

import java.math.BigDecimal;
import java.util.Date;

public class CapitalDetail {
    /**
     * 交易详情id
     *
     * @mbggenerated
     */
    private Long capitalDetailId;

    /**
     * 买家用户id
     *
     * @mbggenerated
     */
    private Long buyUserId;

    /**
     * 卖家用户id
     *
     * @mbggenerated
     */
    private Long sellUserId;

    /**
     * 商品id
     *
     * @mbggenerated
     */
    private Long commodityId;

    /**
     * 商品类别 0：在买 1：在卖
     *
     * @mbggenerated
     */
    private Integer commodityType;

    /**
     * 状态 1:未付款，2：已付款，3：申诉中，4：已取消，5：已完成
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * 交易法币金额
     *
     * @mbggenerated
     */
    private BigDecimal transactionAmount;

    /**
     * 交易量
     *
     * @mbggenerated
     */
    private BigDecimal transactionVolume;

    /**
     * 交易价格
     *
     * @mbggenerated
     */
    private BigDecimal transactionPrice;

    /**
     * 订单号
     *
     * @mbggenerated
     */
    private String orderNumber;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private Date crateTime;

    /**
     * 
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * 点击已付款按钮
     *
     * @mbggenerated
     */
    private Date payTime;

    /**
     * 付款参考号
     *
     * @mbggenerated
     */
    private String referenceNumber;

    /**
     * 环信群组编号
     *
     * @mbggenerated
     */
    private String easemobGroupId;

    /**
     * 币种id
     *
     * @mbggenerated
     */
    private Long currencyId;

    /**
     * 法币币种
     *
     * @mbggenerated
     */
    private String relevantKind;

    /**
     * 手续费
     *
     * @mbggenerated
     */
    private BigDecimal serviceFee;

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

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public Integer getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(Integer commodityType) {
        this.commodityType = commodityType;
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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getCrateTime() {
        return crateTime;
    }

    public void setCrateTime(Date crateTime) {
        this.crateTime = crateTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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

    public String getRelevantKind() {
        return relevantKind;
    }

    public void setRelevantKind(String relevantKind) {
        this.relevantKind = relevantKind;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }
}