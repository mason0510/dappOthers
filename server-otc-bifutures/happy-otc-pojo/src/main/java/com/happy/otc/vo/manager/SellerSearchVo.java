package com.happy.otc.vo.manager;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 卖家列表查询条件
 */
public class SellerSearchVo implements Serializable {
    @ApiModelProperty(value = "当前页")
    private Integer currentPage;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

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
}
