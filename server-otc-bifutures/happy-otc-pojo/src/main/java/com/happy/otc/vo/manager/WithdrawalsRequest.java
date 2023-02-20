package com.happy.otc.vo.manager;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class WithdrawalsRequest implements Serializable {
    @ApiModelProperty(value = "提币订单ID")
    private Long capitalInOutId;

    public Long getCapitalInOutId() {
        return capitalInOutId;
    }

    public void setCapitalInOutId(Long capitalInOutId) {
        this.capitalInOutId = capitalInOutId;
    }
}
