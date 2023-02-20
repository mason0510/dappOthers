package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class UserCenterInfoVO implements Serializable {

    @ApiModelProperty(value = "交易次数")
    private Long totalTradeCount;
    @ApiModelProperty(value = "申诉总数")
    private Long appealCount;
    @ApiModelProperty(value = "申诉成功次数")
    private Long successAppealCount;
    @ApiModelProperty(value = "总资产")
    private BigDecimal totalCapital;
    @ApiModelProperty(value = "保证金")
    private BigDecimal cautionMoney;
    @ApiModelProperty(value = "是否实名认证")
    private Integer realStatus;
    @ApiModelProperty(value = "是否商家")
    private boolean sellerStatus;
    @ApiModelProperty(value = "有无资金密码")
    private boolean capitalPasswordStatus;

    public Long getTotalTradeCount() {
        return totalTradeCount;
    }

    public void setTotalTradeCount(Long totalTradeCount) {
        this.totalTradeCount = totalTradeCount;
    }

    public Long getAppealCount() {
        return appealCount;
    }

    public void setAppealCount(Long appealCount) {
        this.appealCount = appealCount;
    }

    public Long getSuccessAppealCount() {
        return successAppealCount;
    }

    public void setSuccessAppealCount(Long successAppealCount) {
        this.successAppealCount = successAppealCount;
    }

    public BigDecimal getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(BigDecimal totalCapital) {
        this.totalCapital = totalCapital;
    }

    public BigDecimal getCautionMoney() {
        return cautionMoney;
    }

    public void setCautionMoney(BigDecimal cautionMoney) {
        this.cautionMoney = cautionMoney;
    }

    public Integer getRealStatus() {
        return realStatus;
    }

    public void setRealStatus(Integer realStatus) {
        this.realStatus = realStatus;
    }

    public boolean isSellerStatus() {
        return sellerStatus;
    }

    public void setSellerStatus(boolean sellerStatus) {
        this.sellerStatus = sellerStatus;
    }

    public boolean isCapitalPasswordStatus() {
        return capitalPasswordStatus;
    }

    public void setCapitalPasswordStatus(boolean capitalPasswordStatus) {
        this.capitalPasswordStatus = capitalPasswordStatus;
    }
}
