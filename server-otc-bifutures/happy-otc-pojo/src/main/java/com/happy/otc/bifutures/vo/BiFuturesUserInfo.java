package com.happy.otc.bifutures.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018\11\15 0015.
 */
public class BiFuturesUserInfo implements Serializable {
    private static final long serialVersionUID = -667125779945079459L;

    private Long userId;
    private String userName;
    private String userRealName;
    private String mobile;

    public BiFuturesUserInfo(){}
    public BiFuturesUserInfo(Long userId, String userName, String userRealName, String mobile) {
        this.userId = userId;
        this.userName = userName;
        this.userRealName = userRealName;
        this.mobile = mobile;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


}
