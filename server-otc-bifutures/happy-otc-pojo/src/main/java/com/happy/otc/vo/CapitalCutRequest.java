package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public class CapitalCutRequest implements Serializable {

    @ApiModelProperty("资金划转类别")
    private BigDecimal money;
    @ApiModelProperty("OTC账户为主体，资金数量 7:资金划出,8:资金划入")
    private Integer capitalInOutType;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Integer getCapitalInOutType() {
        return capitalInOutType;
    }

    public void setCapitalInOutType(Integer capitalInOutType) {
        this.capitalInOutType = capitalInOutType;
    }
}
