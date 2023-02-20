package com.happy.otc.vo.manager;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class AppealCheckRequest implements Serializable {
    @ApiModelProperty(value = "申诉ID")
    private Long userAppealId;
    @ApiModelProperty(value = "审核结果 1:胜诉 2:败诉")
    private Integer status;

    public Long getUserAppealId() {
        return userAppealId;
    }

    public void setUserAppealId(Long userAppealId) {
        this.userAppealId = userAppealId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
