package com.happy.otc.entity;

import java.util.Date;

public class TransactionPair {
    /**
     * 
     *
     * @mbggenerated
     */
    private Integer transactionPairId;

    /**
     * 种类
     *
     * @mbggenerated
     */
    private String kind;

    /**
     * 现关联种类
     *
     * @mbggenerated
     */
    private String relevantKind;

    /**
     * 更新时间
     *
     * @mbggenerated
     */
    private Date updateTime;

    public Integer getTransactionPairId() {
        return transactionPairId;
    }

    public void setTransactionPairId(Integer transactionPairId) {
        this.transactionPairId = transactionPairId;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getRelevantKind() {
        return relevantKind;
    }

    public void setRelevantKind(String relevantKind) {
        this.relevantKind = relevantKind;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}