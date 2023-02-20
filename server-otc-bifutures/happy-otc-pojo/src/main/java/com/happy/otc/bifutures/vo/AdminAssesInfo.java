package com.happy.otc.bifutures.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018\11\29 0029.
 */
public class AdminAssesInfo implements Serializable {

    private static final long serialVersionUID = -8522293791785457955L;

    private BigDecimal balance;
    private BigDecimal fundIn;
    private BigDecimal funOut;
    private BigDecimal frozenPrincepal;
    private BigDecimal currentPal;
    private BigDecimal selldTotalPal;
    private BigDecimal totalBuyMoney;
    private BigDecimal currenBuyMoney;
    private BigDecimal totalServiceCharge;
    private Integer totalBuyAmount;
    private Integer cancelAmount;
    private Integer currentAmount;
    private Integer yingStrategyAmount;
    private Integer kuiStrategyAmount;
    private BigDecimal totalDeferCharge;

    public AdminAssesInfo() {
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFundIn() {
        return fundIn;
    }

    public void setFundIn(BigDecimal fundIn) {
        this.fundIn = fundIn;
    }

    public BigDecimal getFunOut() {
        return funOut;
    }

    public void setFunOut(BigDecimal funOut) {
        this.funOut = funOut;
    }

    public BigDecimal getFrozenPrincepal() {
        return frozenPrincepal;
    }

    public void setFrozenPrincepal(BigDecimal frozenPrincepal) {
        this.frozenPrincepal = frozenPrincepal;
    }

    public BigDecimal getCurrentPal() {
        return currentPal;
    }

    public void setCurrentPal(BigDecimal currentPal) {
        this.currentPal = currentPal;
    }

    public BigDecimal getSelldTotalPal() {
        return selldTotalPal;
    }

    public void setSelldTotalPal(BigDecimal selldTotalPal) {
        this.selldTotalPal = selldTotalPal;
    }

    public BigDecimal getTotalBuyMoney() {
        return totalBuyMoney;
    }

    public void setTotalBuyMoney(BigDecimal totalBuyMoney) {
        this.totalBuyMoney = totalBuyMoney;
    }

    public BigDecimal getCurrenBuyMoney() {
        return currenBuyMoney;
    }

    public void setCurrenBuyMoney(BigDecimal currenBuyMoney) {
        this.currenBuyMoney = currenBuyMoney;
    }

    public BigDecimal getTotalServiceCharge() {
        return totalServiceCharge;
    }

    public void setTotalServiceCharge(BigDecimal totalServiceCharge) {
        this.totalServiceCharge = totalServiceCharge;
    }

    public Integer getTotalBuyAmount() {
        return totalBuyAmount;
    }

    public void setTotalBuyAmount(Integer totalBuyAmount) {
        this.totalBuyAmount = totalBuyAmount;
    }

    public Integer getCancelAmount() {
        return cancelAmount;
    }

    public void setCancelAmount(Integer cancelAmount) {
        this.cancelAmount = cancelAmount;
    }

    public Integer getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Integer currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Integer getYingStrategyAmount() {
        return yingStrategyAmount;
    }

    public void setYingStrategyAmount(Integer yingStrategyAmount) {
        this.yingStrategyAmount = yingStrategyAmount;
    }

    public Integer getKuiStrategyAmount() {
        return kuiStrategyAmount;
    }

    public void setKuiStrategyAmount(Integer kuiStrategyAmount) {
        this.kuiStrategyAmount = kuiStrategyAmount;
    }

    public BigDecimal getTotalDeferCharge() {
        return totalDeferCharge;
    }

    public void setTotalDeferCharge(BigDecimal totalDeferCharge) {
        this.totalDeferCharge = totalDeferCharge;
    }
}
