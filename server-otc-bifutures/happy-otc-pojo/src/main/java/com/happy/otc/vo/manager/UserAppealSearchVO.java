package com.happy.otc.vo.manager;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class UserAppealSearchVO implements Serializable {

    @ApiModelProperty(value = "当前页")
    private Integer currentPage;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;

    @ApiModelProperty("状态 0：处理中，1：胜诉 ，2:败诉  3:取消")
    private Byte status;

    @ApiModelProperty(value = "订单号")
    private String orderNumber;

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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
