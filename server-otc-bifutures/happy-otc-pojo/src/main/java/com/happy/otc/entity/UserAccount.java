package com.happy.otc.entity;

public class UserAccount {
    /**
     * 
     *
     * @mbggenerated
     */
    private Long userAccountId;

    /**
     * 用户id
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 
     *
     * @mbggenerated
     */
    private String realName;

    /**
     * 支付账户
     *
     * @mbggenerated
     */
    private String account;

    /**
     * 支付地址
     *
     * @mbggenerated
     */
    private String address;

    /**
     * 支付类别 1：Alipay 2：WECHAT_PAY 3：bank_pay
     *
     * @mbggenerated
     */
    private Integer payType;

    /**
     * 支付状态 0： 关 1：开
     *
     * @mbggenerated
     */
    private Integer payStatus;

    /**
     * 支付方式额外信息详情
     *
     * @mbggenerated
     */
    private String paymentDetail;

    public Long getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public String getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(String paymentDetail) {
        this.paymentDetail = paymentDetail;
    }
}