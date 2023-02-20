package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class UserBalanceVO implements Serializable {

    @ApiModelProperty(value = "otc可用余额")
    private BigDecimal otcAvailable;
    @ApiModelProperty(value = "期货可用余额")
    private BigDecimal futuresAvailable;

    public BigDecimal getOtcAvailable() {
        return otcAvailable;
    }

    public void setOtcAvailable(BigDecimal otcAvailable) {
        this.otcAvailable = otcAvailable;
    }

    public BigDecimal getFuturesAvailable() {
        return futuresAvailable;
    }

    public void setFuturesAvailable(BigDecimal futuresAvailable) {
        this.futuresAvailable = futuresAvailable;
    }
}
