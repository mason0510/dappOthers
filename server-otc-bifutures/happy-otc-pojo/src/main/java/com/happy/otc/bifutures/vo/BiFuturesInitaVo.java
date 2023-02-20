package com.happy.otc.bifutures.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class BiFuturesInitaVo implements Serializable {
    private static final long serialVersionUID = 7009578949860737939L;
    @ApiModelProperty(value = "服务器时间")
    private Long nowTime;
    @ApiModelProperty(value = "风控数据")
    private String strRisk;
    @ApiModelProperty(value = "用户账户余额")
    private BigDecimal balance;
    @ApiModelProperty(value = "委托量最大随机数范围")
    private Integer maxRandomNumber;
    @ApiModelProperty(value = "委托量最小随机数范围")
    private Integer minRandomNumber;

    public BiFuturesInitaVo(Long nowTime, String strRisk, BigDecimal balance, Integer maxRandomNumber, Integer minRandomNumber) {
        this.nowTime = nowTime;
        this.strRisk = strRisk;
        this.balance = balance;
        this.maxRandomNumber = maxRandomNumber;
        this.minRandomNumber = minRandomNumber;
    }

    public Long getNowTime() {
        return nowTime;
    }

    public void setNowTime(Long nowTime) {
        this.nowTime = nowTime;
    }

    public String getStrRisk() {
        return strRisk;
    }

    public void setStrRisk(String strRisk) {
        this.strRisk = strRisk;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getMaxRandomNumber() {
        return maxRandomNumber;
    }

    public void setMaxRandomNumber(Integer maxRandomNumber) {
        this.maxRandomNumber = maxRandomNumber;
    }

    public Integer getMinRandomNumber() {
        return minRandomNumber;
    }

    public void setMinRandomNumber(Integer minRandomNumber) {
        this.minRandomNumber = minRandomNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BiFuturesInitaVo that = (BiFuturesInitaVo) o;

        if (nowTime != null ? !nowTime.equals(that.nowTime) : that.nowTime != null) return false;
        if (strRisk != null ? !strRisk.equals(that.strRisk) : that.strRisk != null) return false;
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        if (maxRandomNumber != null ? !maxRandomNumber.equals(that.maxRandomNumber) : that.maxRandomNumber != null)
            return false;
        return minRandomNumber != null ? minRandomNumber.equals(that.minRandomNumber) : that.minRandomNumber == null;

    }

    @Override
    public int hashCode() {
        int result = nowTime != null ? nowTime.hashCode() : 0;
        result = 31 * result + (strRisk != null ? strRisk.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (maxRandomNumber != null ? maxRandomNumber.hashCode() : 0);
        result = 31 * result + (minRandomNumber != null ? minRandomNumber.hashCode() : 0);
        return result;
    }
}
