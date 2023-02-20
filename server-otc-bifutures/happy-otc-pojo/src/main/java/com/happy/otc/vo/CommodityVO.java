package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class CommodityVO implements Serializable {

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
    @ApiModelProperty(value = "币种f贩卖数量")
    private BigDecimal currencyQuantity;
    @ApiModelProperty(value = "商品类别 0：在买 1：在卖")
    private Integer commodityType;
    @ApiModelProperty(value = "币种id")
    private Long currencyId;

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
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

    public Integer getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(Integer commodityType) {
        this.commodityType = commodityType;
    }
}
