package com.happy.otc.entity;

import java.util.Date;

public class UserMonitor {
    /**
     * 
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * 
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 1:取消订单次数过多，2：取消发布商品次数过多，3
     *
     * @mbggenerated
     */
    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}