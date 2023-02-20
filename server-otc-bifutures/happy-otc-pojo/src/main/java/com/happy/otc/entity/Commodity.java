package com.happy.otc.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Commodity {
    /**
     * 商品id
     *
     * @mbggenerated
     */
    private Long commodityId;

    /**
     * 用户id
     *
     * @mbggenerated
     */
    private Long userId;

    /**
     * 现关联种类
     *
     * @mbggenerated
     */
    private String relevantKind;

    /**
     * 币种的价格
     *
     * @mbggenerated
     */
    private BigDecimal currencyPrice;

    /**
     * 种类
     *
     * @mbggenerated
     */
    private String kind;

    /**
     * 最小购买金额
     *
     * @mbggenerated
     */
    private BigDecimal minimumAmount;

    /**
     * 最大购买金额
     *
     * @mbggenerated
     */
    private BigDecimal maximumAmount;

    /**
     * 币种贩卖数量
     *
     * @mbggenerated
     */
    private BigDecimal currencyQuantity;

    /**
     * 冻结的贩卖数量
     *
     * @mbggenerated
     */
    private BigDecimal frozenQuantity;

    /**
     * 手续费冻结的资金
     *
     * @mbggenerated
     */
    private BigDecimal feeQuantity;

    /**
     * 0:在卖；1：已卖完
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * 商品类别 0：在买 1：在卖
     *
     * @mbggenerated
     */
    private Integer commodityType;

    /**
     * 订单创建时间
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 订单修改时间
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * 
     *
     * @mbggenerated
     */
    private Byte isDeleted;

    /**
     * 版本号
     *
     * @mbggenerated
     */
    private Integer versionNumber;

    /**
     * 币种id
     *
     * @mbggenerated
     */
    private Long currencyId;

    /**
     * 支付方式
     *
     * @mbggenerated
     */
    private String payMethodList;

    /**
     * 自动回复内容
     *
     * @mbggenerated
     */
    private String leaveMessage;

    /**
     * 交易限制
     *
     * @mbggenerated
     */
    private String tradeCurb;

    /**
     * 交易时间限制类别
     *
     * @mbggenerated
     */
    private Integer tradeTimeType;

    /**
     * 交易国家
     *
     * @mbggenerated
     */
    private String country;

    /**
     * 付款时间
     *
     * @mbggenerated
     */
    private Date payTime;

    /**
     * 支付方式类别列表
     *
     * @mbggenerated
     */
    private String payMethodTypeList;

    /**
     * 1:固定价格、2:市场价格
     *
     * @mbggenerated
     */
    private Integer tradeMethod;

    /**
     * 防止并发的版本号记录,用来处理乐观锁
     *
     * @mbggenerated
     */
    private Integer version;

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRelevantKind() {
        return relevantKind;
    }

    public void setRelevantKind(String relevantKind) {
        this.relevantKind = relevantKind;
    }

    public BigDecimal getCurrencyPrice() {
        return currencyPrice;
    }

    public void setCurrencyPrice(BigDecimal currencyPrice) {
        this.currencyPrice = currencyPrice;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public BigDecimal getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(BigDecimal minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public BigDecimal getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public BigDecimal getCurrencyQuantity() {
        return currencyQuantity;
    }

    public void setCurrencyQuantity(BigDecimal currencyQuantity) {
        this.currencyQuantity = currencyQuantity;
    }

    public BigDecimal getFrozenQuantity() {
        return frozenQuantity;
    }

    public void setFrozenQuantity(BigDecimal frozenQuantity) {
        this.frozenQuantity = frozenQuantity;
    }

    public BigDecimal getFeeQuantity() {
        return feeQuantity;
    }

    public void setFeeQuantity(BigDecimal feeQuantity) {
        this.feeQuantity = feeQuantity;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(Integer commodityType) {
        this.commodityType = commodityType;
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

    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getPayMethodList() {
        return payMethodList;
    }

    public void setPayMethodList(String payMethodList) {
        this.payMethodList = payMethodList;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public String getTradeCurb() {
        return tradeCurb;
    }

    public void setTradeCurb(String tradeCurb) {
        this.tradeCurb = tradeCurb;
    }

    public Integer getTradeTimeType() {
        return tradeTimeType;
    }

    public void setTradeTimeType(Integer tradeTimeType) {
        this.tradeTimeType = tradeTimeType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getPayMethodTypeList() {
        return payMethodTypeList;
    }

    public void setPayMethodTypeList(String payMethodTypeList) {
        this.payMethodTypeList = payMethodTypeList;
    }

    public Integer getTradeMethod() {
        return tradeMethod;
    }

    public void setTradeMethod(Integer tradeMethod) {
        this.tradeMethod = tradeMethod;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}