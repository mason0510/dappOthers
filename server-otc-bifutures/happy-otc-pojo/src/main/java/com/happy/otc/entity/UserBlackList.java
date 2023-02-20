package com.happy.otc.entity;

import java.util.Date;

public class UserBlackList {
    /**
     * 主键
     *
     * @mbggenerated
     */
    private Long blackListId;

    /**
     * 屏蔽人ID
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 被屏蔽人ID
     *
     * @mbggenerated
     */
    private Long targetId;

    /**
     * 被屏蔽人名称
     *
     * @mbggenerated
     */
    private String targetName;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private Date createTime;

    public Long getBlackListId() {
        return blackListId;
    }

    public void setBlackListId(Long blackListId) {
        this.blackListId = blackListId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}