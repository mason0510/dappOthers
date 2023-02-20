package com.happy.otc.entity;

import java.util.Date;

public class ServiceGroup {
    /**
     * 用户ID
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 环信群组ID
     *
     * @mbggenerated
     */
    private String groupId;

    /**
     * 
     *
     * @mbggenerated
     */
    private Date createTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}