package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserAppealInfoVO implements Serializable {

    @ApiModelProperty(value = "申诉单id")
    private Long appealId;
    @ApiModelProperty(value = "申诉用户id")
    private Long appealUserId;
    @ApiModelProperty(value = "申诉用户名称")
    private String appealUserName;
    @ApiModelProperty(value = "申诉类型 1:对方未付款 2:对方未放行 3:对方无应答 4:对方有欺诈行为 5:其他")
    private Byte appealType;
    @ApiModelProperty(value = "申诉理由")
    private String appealReason;
    @ApiModelProperty(value = "申诉状态 0：处理中，1：胜诉 ，2:败诉  3:取消")
    private Integer appealStatus;
    @ApiModelProperty(value = "申诉时间")
    private Date appealTime;

    @ApiModelProperty(value = "买家用户id")
    private Long buyUserId;
    @ApiModelProperty(value = "买家用户名称")
    private String buyUserName;
    @ApiModelProperty(value = "买家用户邮箱")
    private String buyUserEmail;
    @ApiModelProperty(value = "买家用户电话")
    private String buyUserMobile;
    @ApiModelProperty(value = "卖家用户id")
    private Long sellUserId;
    @ApiModelProperty(value = "卖家用户名称")
    private String sellUserName;
    @ApiModelProperty(value = "卖家用户邮箱")
    private String sellUserEmail;
    @ApiModelProperty(value = "卖家用户电话")
    private String sellUserMobile;

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
    @ApiModelProperty(value = "交易价格")
    private BigDecimal transactionAmount;
    @ApiModelProperty(value = "交易量")
    private BigDecimal transactionVolume;
    @ApiModelProperty(value = "付款时间")
    private Date payTime;
    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    public Long getAppealId() {
        return appealId;
    }

    public void setAppealId(Long appealId) {
        this.appealId = appealId;
    }

    public Long getAppealUserId() {
        return appealUserId;
    }

    public void setAppealUserId(Long appealUserId) {
        this.appealUserId = appealUserId;
    }

    public String getAppealUserName() {
        return appealUserName;
    }

    public void setAppealUserName(String appealUserName) {
        this.appealUserName = appealUserName;
    }

    public Byte getAppealType() {
        return appealType;
    }

    public void setAppealType(Byte appealType) {
        this.appealType = appealType;
    }

    public String getAppealReason() {
        return appealReason;
    }

    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    public Integer getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    public Date getAppealTime() {
        return appealTime;
    }

    public void setAppealTime(Date appealTime) {
        this.appealTime = appealTime;
    }

    public Long getBuyUserId() {
        return buyUserId;
    }

    public void setBuyUserId(Long buyUserId) {
        this.buyUserId = buyUserId;
    }

    public String getBuyUserName() {
        return buyUserName;
    }

    public void setBuyUserName(String buyUserName) {
        this.buyUserName = buyUserName;
    }

    public String getBuyUserEmail() {
        return buyUserEmail;
    }

    public void setBuyUserEmail(String buyUserEmail) {
        this.buyUserEmail = buyUserEmail;
    }

    public String getBuyUserMobile() {
        return buyUserMobile;
    }

    public void setBuyUserMobile(String buyUserMobile) {
        this.buyUserMobile = buyUserMobile;
    }

    public Long getSellUserId() {
        return sellUserId;
    }

    public void setSellUserId(Long sellUserId) {
        this.sellUserId = sellUserId;
    }

    public String getSellUserName() {
        return sellUserName;
    }

    public void setSellUserName(String sellUserName) {
        this.sellUserName = sellUserName;
    }

    public String getSellUserEmail() {
        return sellUserEmail;
    }

    public void setSellUserEmail(String sellUserEmail) {
        this.sellUserEmail = sellUserEmail;
    }

    public String getSellUserMobile() {
        return sellUserMobile;
    }

    public void setSellUserMobile(String sellUserMobile) {
        this.sellUserMobile = sellUserMobile;
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

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
