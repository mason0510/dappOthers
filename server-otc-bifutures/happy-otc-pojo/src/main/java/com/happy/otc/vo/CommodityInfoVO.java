package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CommodityInfoVO implements Serializable {

    @ApiModelProperty(value = "商品id")
    private Long commodityId;
    @ApiModelProperty(value = "现关联种类")
    private String relevantKind;
    @ApiModelProperty(value = "币种的金额")
    private BigDecimal currencyPrice;
    @ApiModelProperty(value = "种类")
    private String kind;
    @ApiModelProperty(value = "最小购买金额")
    private BigDecimal minimumAmount;
    @ApiModelProperty(value = "最大购买金额")
    private BigDecimal maximumAmount;
    @ApiModelProperty(value = "币种贩卖数量")
    private BigDecimal currencyQuantity;
    @ApiModelProperty(value = "冻结的贩卖数量")
    private BigDecimal frozenQuantity;
    @ApiModelProperty(value = "手续费冻结的资金")
    private BigDecimal feeQuantity;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "用户名称")
    private String nickName;
    @ApiModelProperty(value = "用户头像")
    private String headUrl;
    @ApiModelProperty(value = "成功成交量")
    private Long successTradeCount;
    @ApiModelProperty(value = "总成交量")
    private Long totalTradeCount;
    @ApiModelProperty(value = "成交率")
    private Double closeRate;
    @ApiModelProperty(value = "支付方式列表")
    private String payMethodList;
    @ApiModelProperty(value = "用户的认证等级 0:未认证 1;实名认证 2：高级认证")
    private Integer identificationStatus;
    @ApiModelProperty(value = "币种id")
    private Long currencyId;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    @ApiModelProperty(value = "支付限制")
    private String tradeCurb;
    @ApiModelProperty(value = "支付方式类型列表")
    private String payMethodTypeList;
    @ApiModelProperty(value = "版本号")
    private Integer versionNumber;
    @ApiModelProperty(value = "0:固定价格、1:市场价格")
    private Integer tradeMethod;
    @ApiModelProperty(value = "0 :商户可以在线购买,1:商户可以在线出售")
    private Integer commodityType;

    public Integer getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(Integer commodityType) {
        this.commodityType = commodityType;
    }

    public Integer getTradeMethod() {
        return tradeMethod;
    }

    public void setTradeMethod(Integer tradeMethod) {
        this.tradeMethod = tradeMethod;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getPayMethodTypeList() {
        return payMethodTypeList;
    }

    public void setPayMethodTypeList(String payMethodTypeList) {
        this.payMethodTypeList = payMethodTypeList;
    }

    public String getPayMethodList() {
        return payMethodList;
    }

    public void setPayMethodList(String payMethodList) {
        this.payMethodList = payMethodList;
    }

    public String getTradeCurb() {
        return tradeCurb;
    }

    public void setTradeCurb(String tradeCurb) {
        this.tradeCurb = tradeCurb;
    }

    public Long getTotalTradeCount() {
        return totalTradeCount;
    }

    public void setTotalTradeCount(Long totalTradeCount) {
        this.totalTradeCount = totalTradeCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Integer getIdentificationStatus() {
        return identificationStatus;
    }

    public void setIdentificationStatus(Integer identificationStatus) {
        this.identificationStatus = identificationStatus;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public String getRelevantKind() {
        return relevantKind;
    }

    public void setRelevantKind(String relevantKind) {
        this.relevantKind = relevantKind;
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

    public BigDecimal getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(BigDecimal minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public BigDecimal getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public BigDecimal getCurrencyQuantity() {
        return currencyQuantity;
    }

    public void setCurrencyQuantity(BigDecimal currencyQuantity) {
        this.currencyQuantity = currencyQuantity;
    }

    public BigDecimal getFrozenQuantity() {
        return frozenQuantity;
    }

    public void setFrozenQuantity(BigDecimal frozenQuantity) {
        this.frozenQuantity = frozenQuantity;
    }

    public BigDecimal getFeeQuantity() {
        return feeQuantity;
    }

    public void setFeeQuantity(BigDecimal feeQuantity) {
        this.feeQuantity = feeQuantity;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

}
