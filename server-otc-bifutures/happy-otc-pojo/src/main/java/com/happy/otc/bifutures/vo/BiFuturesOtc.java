package com.happy.otc.bifutures.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018\11\22 0022.
 */
public class BiFuturesOtc implements Serializable {
    private static final long serialVersionUID = 4339456523424558979L;

    @ApiModelProperty(value = "合约账户余额")
    private BigDecimal balance;
    @ApiModelProperty(value = "可用保证金")
    private BigDecimal principal;
    @ApiModelProperty(value = "已用保证金")
    private BigDecimal usedPrincipal;
    @ApiModelProperty(value = "持仓盈亏")
    private BigDecimal positionPol;
    @ApiModelProperty(value = "当日平仓盈亏")
    private BigDecimal todayCellPol;

    public BiFuturesOtc(){}
    public BiFuturesOtc(BigDecimal balance, BigDecimal principal, BigDecimal usedPrincipal, BigDecimal positionPol, BigDecimal todayCellPol) {
        this.balance = balance;
        this.principal = principal;
        this.usedPrincipal = usedPrincipal;
        this.positionPol = positionPol;
        this.todayCellPol = todayCellPol;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getUsedPrincipal() {
        return usedPrincipal;
    }

    public void setUsedPrincipal(BigDecimal usedPrincipal) {
        this.usedPrincipal = usedPrincipal;
    }

    public BigDecimal getPositionPol() {
        return positionPol;
    }

    public void setPositionPol(BigDecimal positionPol) {
        this.positionPol = positionPol;
    }

    public BigDecimal getTodayCellPol() {
        return todayCellPol;
    }

    public void setTodayCellPol(BigDecimal todayCellPol) {
        this.todayCellPol = todayCellPol;
    }

    @Override
    public String toString() {
        return "BiFuturesOtc{" +
                "balance=" + balance +
                ", principal=" + principal +
                ", usedPrincipal=" + usedPrincipal +
                ", positionPol=" + positionPol +
                ", todayCellPol=" + todayCellPol +
                '}';
    }
}
