package com.happy.otc.entity;

import java.util.Date;

public class UserIdentity {
    /**
     * 
     *
     * @mbggenerated
     */
    private Long id;

    /**
     * 真实姓名
     *
     * @mbggenerated
     */
    private String realName;

    /**
     * 证件类型 0:身份证 1:护照
     *
     * @mbggenerated
     */
    private Byte documentType;

    /**
     * 证件号码
     *
     * @mbggenerated
     */
    private String identityNumber;

    /**
     * 图片地址 证件正面
     *
     * @mbggenerated
     */
    private String imageAddress1;

    /**
     * 图片地址 证件反面
     *
     * @mbggenerated
     */
    private String imageAddress2;

    /**
     * 图片地址 证件手持
     *
     * @mbggenerated
     */
    private String imageAddress3;

    /**
     * 
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 认证状态 0:未认证 1:待认证 2:已认证 3:认证驳回
     *
     * @mbggenerated
     */
    private Byte status;

    /**
     * 驳回理由 0:无 1:边角不完整 2:字体不清晰 3:亮度不均匀
     *
     * @mbggenerated
     */
    private String rejectReason;

    /**
     * 
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 资金密码
     *
     * @mbggenerated
     */
    private String capitalCipher;

    /**
     * 支付方式数组
     *
     * @mbggenerated
     */
    private String payMethodList;

    /**
     * 客户类型 0:普通客户 1:商户
     *
     * @mbggenerated
     */
    private Byte userOtcType;

    /**
     * 语言类型 1:中文 2:英文
     *
     * @mbggenerated
     */
    private Byte languageType;

    /**
     * 其他驳回理由
     *
     * @mbggenerated
     */
    private String otherReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Byte getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Byte documentType) {
        this.documentType = documentType;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getImageAddress1() {
        return imageAddress1;
    }

    public void setImageAddress1(String imageAddress1) {
        this.imageAddress1 = imageAddress1;
    }

    public String getImageAddress2() {
        return imageAddress2;
    }

    public void setImageAddress2(String imageAddress2) {
        this.imageAddress2 = imageAddress2;
    }

    public String getImageAddress3() {
        return imageAddress3;
    }

    public void setImageAddress3(String imageAddress3) {
        this.imageAddress3 = imageAddress3;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCapitalCipher() {
        return capitalCipher;
    }

    public void setCapitalCipher(String capitalCipher) {
        this.capitalCipher = capitalCipher;
    }

    public String getPayMethodList() {
        return payMethodList;
    }

    public void setPayMethodList(String payMethodList) {
        this.payMethodList = payMethodList;
    }

    public Byte getUserOtcType() {
        return userOtcType;
    }

    public void setUserOtcType(Byte userOtcType) {
        this.userOtcType = userOtcType;
    }

    public Byte getLanguageType() {
        return languageType;
    }

    public void setLanguageType(Byte languageType) {
        this.languageType = languageType;
    }

    public String getOtherReason() {
        return otherReason;
    }

    public void setOtherReason(String otherReason) {
        this.otherReason = otherReason;
    }
}