package com.happy.otc.dto;

import java.io.Serializable;
import java.util.Date;

public class UserCurrencyAddressDTO implements Serializable {
    /**
     * 
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * 充币地址
     *
     * @mbggenerated
     */
    private String address;

    /**
     * 备注
     *
     * @mbggenerated
     */
    private String detail;

    /**
     * 
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 币种id
     *
     * @mbggenerated
     */
    private Long currencyId;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 简称
     *
     * @mbggenerated
     */
    private String currencySimpleName;

    private Long userAddressId;

    public Long getUserAddressId() {
        return userAddressId;
    }

    public void setUserAddressId(Long userAddressId) {
        this.userAddressId = userAddressId;
    }

    public String getCurrencySimpleName() {
        return currencySimpleName;
    }

    public void setCurrencySimpleName(String currencySimpleName) {
        this.currencySimpleName = currencySimpleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}