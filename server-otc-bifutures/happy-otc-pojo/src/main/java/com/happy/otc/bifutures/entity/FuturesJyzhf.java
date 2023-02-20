package com.happy.otc.bifutures.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class FuturesJyzhf implements Serializable {
    private static final long serialVersionUID = -6681999137646648504L;
    private BigDecimal money;//流水资金

    private String biCode;//备注

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getBiCode() {
        return biCode;
    }

    public void setBiCode(String biCode) {
        this.biCode = biCode;
    }
}
