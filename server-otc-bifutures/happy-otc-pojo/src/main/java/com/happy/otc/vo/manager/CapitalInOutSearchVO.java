package com.happy.otc.vo.manager;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class CapitalInOutSearchVO implements Serializable {
    @ApiModelProperty(value = "当前页")
    private Integer currentPage;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

    @ApiModelProperty("状态 0:审核中 1：成功 2：失败")
    private Integer status;

    @ApiModelProperty("用户手机号")
    private String mobile;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
