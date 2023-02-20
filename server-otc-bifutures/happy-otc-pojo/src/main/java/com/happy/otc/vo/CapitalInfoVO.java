package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CapitalInfoVO implements Serializable {

    @ApiModelProperty(value = "用户资金id")
    private Long capitalId;
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "货币id")
    private Long currencyId;
    @ApiModelProperty(value = "拥有货币数量")
    private BigDecimal capitalBalance;
    @ApiModelProperty(value = "可用货币数")
    private BigDecimal capitalAvailable;
    @ApiModelProperty(value = "冻结的货币数")
    private BigDecimal capitalFrozen;
    @ApiModelProperty(value = "简称")
    private String currencySimpleName;
    @ApiModelProperty(value = "存入货币数")
    private BigDecimal capitalIn;
    @ApiModelProperty(value = "取出货币数")
    private BigDecimal capitalOut;
    @ApiModelProperty(value = "版本号")
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigDecimal getCapitalBalance() {
        return capitalBalance;
    }

    public void setCapitalBalance(BigDecimal capitalBalance) {
        this.capitalBalance = capitalBalance;
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

    public String getCurrencySimpleName() {
        return currencySimpleName;
    }

    public void setCurrencySimpleName(String currencySimpleName) {
        this.currencySimpleName = currencySimpleName;
    }
}
