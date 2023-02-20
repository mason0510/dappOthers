package com.happy.otc.bifutures.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class UserAssetsDto implements Serializable {
    private static final long serialVersionUID = -6001350051830762571L;
    private Long userId;//用户id
    private BigDecimal balance;//用户账户余额
    private BigDecimal fundIn;//存入资金
    private BigDecimal fundOut;//取出资金
    private Date time;//创建时间

    public UserAssetsDto(){}
    public UserAssetsDto(Long userId, BigDecimal balance, BigDecimal fundIn, BigDecimal fundOut, Date time) {
        this.userId = userId;
        this.balance = balance;
        this.fundIn = fundIn;
        this.fundOut = fundOut;
        this.time = time;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public BigDecimal getFundOut() {
        return fundOut;
    }

    public void setFundOut(BigDecimal fundOut) {
        this.fundOut = fundOut;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
