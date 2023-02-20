package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

public class UserAppealVO implements Serializable {

    @ApiModelProperty(value = "申诉id")
    private Long appealId;

    @ApiModelProperty(value = "申诉类型: 1:对方未付款 2:对方未放行 3:对方无应答 4:对方有欺诈行为 5:其他")
    private Byte type;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "交易id")
    private Long capitalDetailId;

    @ApiModelProperty(value = "申诉状态 0：处理中，1：胜诉 ，2:败诉  3:取消")
    private Integer status;

    @ApiModelProperty(value = "申诉理由")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String detail;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    public Long getAppealId() {
        return appealId;
    }

    public void setAppealId(Long appealId) {
        this.appealId = appealId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCapitalDetailId() {
        return capitalDetailId;
    }

    public void setCapitalDetailId(Long capitalDetailId) {
        this.capitalDetailId = capitalDetailId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}