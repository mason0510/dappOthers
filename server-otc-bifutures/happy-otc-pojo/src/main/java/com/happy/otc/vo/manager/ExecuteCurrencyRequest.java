package com.happy.otc.vo.manager;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class ExecuteCurrencyRequest implements Serializable {
    @ApiModelProperty(value = "交易ID")
    private Long capitalDetailId;

    public Long getCapitalDetailId() {
        return capitalDetailId;
    }

    public void setCapitalDetailId(Long capitalDetailId) {
        this.capitalDetailId = capitalDetailId;
    }
}
