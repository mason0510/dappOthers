package com.happy.otc.entity;

import java.util.Date;

public class UserAppeal {
    /**
     * 申诉id
     *
     * @mbggenerated
     */
    private Long appealId;

    /**
     * 申诉类型: 1:对方未付款 2:对方未放行 3:对方无应答 4:对方有欺诈行为 5:其他
     *
     * @mbggenerated
     */
    private Byte type;

    /**
     * 用户id
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 交易id
     *
     * @mbggenerated
     */
    private Long capitalDetailId;

    /**
     * 0：处理中，1：胜诉 ，2:败诉  3:取消
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * 申诉理由
     *
     * @mbggenerated
     */
    private String reason;

    /**
     * 备注
     *
     * @mbggenerated
     */
    private String detail;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 修改时间
     *
     * @mbggenerated
     */
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