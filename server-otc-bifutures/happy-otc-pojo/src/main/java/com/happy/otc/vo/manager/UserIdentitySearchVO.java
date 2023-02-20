package com.happy.otc.vo.manager;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class UserIdentitySearchVO implements Serializable {

    @ApiModelProperty(value = "当前页")
    private Integer currentPage;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

    @ApiModelProperty("认证状态 null:所有 0:未认证 1:待认证 2:已认证 3:认证驳回")
    private Byte status;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}
