package com.happy.otc.entity;

public class Currency {
    /**
     * 
     *
     * @mbggenerated
     */
    private Long currencyId;

    /**
     * 中文名称
     *
     * @mbggenerated
     */
    private String currencyChineseName;

    /**
     * 英文名称
     *
     * @mbggenerated
     */
    private String currencyEnglishName;

    /**
     * 简称
     *
     * @mbggenerated
     */
    private String currencySimpleName;

    /**
     * 合约地址
     *
     * @mbggenerated
     */
    private String contractAddress;

    /**
     * 是否在首页展示：0，不展示；1，展示
     *
     * @mbggenerated
     */
    private Integer isShowIndex;

    /**
     * 最后偏移,交易列表扫描到的最新一页
     *
     * @mbggenerated
     */
    private Long lastOffset;

    /**
     * 地址表中最新的地址id
     *
     * @mbggenerated
     */
    private Long lastAddressNumber;

    /**
     * 小数点
     *
     * @mbggenerated
     */
    private Integer decimals;


    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyChineseName() {
        return currencyChineseName;
    }

    public void setCurrencyChineseName(String currencyChineseName) {
        this.currencyChineseName = currencyChineseName;
    }

    public String getCurrencyEnglishName() {
        return currencyEnglishName;
    }

    public void setCurrencyEnglishName(String currencyEnglishName) {
        this.currencyEnglishName = currencyEnglishName;
    }

    public String getCurrencySimpleName() {
        return currencySimpleName;
    }

    public void setCurrencySimpleName(String currencySimpleName) {
        this.currencySimpleName = currencySimpleName;
    }

    public Integer getIsShowIndex() {
        return isShowIndex;
    }

    public void setIsShowIndex(Integer isShowIndex) {
        this.isShowIndex = isShowIndex;
    }

    public Long getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(Long lastOffset) {
        this.lastOffset = lastOffset;
    }

    public Long getLastAddressNumber() {
        return lastAddressNumber;
    }

    public void setLastAddressNumber(Long lastAddressNumber) {
        this.lastAddressNumber = lastAddressNumber;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
}