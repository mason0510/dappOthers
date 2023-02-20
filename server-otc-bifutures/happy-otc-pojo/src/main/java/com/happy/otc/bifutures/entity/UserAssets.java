package com.happy.otc.bifutures.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class UserAssets implements Serializable {
    private static final long serialVersionUID = -8913873447089294977L;
    private Long userId;//用户id
    private BigDecimal balance;//用户账户余额
    private BigDecimal fundIn;//存入资金
    private BigDecimal fundOut;//取出资金
    private Date time;//创建时间
    private Date version;//版本


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

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "UserAssets{" +
                "userId=" + userId +
                ", balance=" + balance +
                ", fundIn=" + fundIn +
                ", fundOut=" + fundOut +
                ", time=" + time +
                ", version=" + version +
                '}';
    }
}
