package com.happy.otc.entity;

import java.util.Date;

public class Easemob {
    /**
     * 主键
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * 用户ID
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 环信用户名
     *
     * @mbggenerated
     */
    private String username;

    /**
     * 环信用户密码
     *
     * @mbggenerated
     */
    private String password;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private Date createTime;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}