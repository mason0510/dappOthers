package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class TransactionPairVO implements Serializable {
    @ApiModelProperty("币种id")
    private Long currencyId;
    @ApiModelProperty("币种名称")
    private String currencyName;

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}
