package com.happy.otc.bifutures.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class FuturesFundDetailInfo implements Serializable {
    private static final long serialVersionUID = -8862872456767078232L;
    private Long id;//id
    private Integer userId;//用户id
    private BigDecimal money;//流水资金
    private BigDecimal balance;//用户余额
    private Integer type;//是否成功
    private String explain;//注释
    private String detail;//备注
    private Integer sourceId;//策略来源id
    private Date time;//
    private Date version;//版本

    public FuturesFundDetailInfo(Long id, Integer userId, BigDecimal money, BigDecimal balance, Integer type, String explain, String detail, Integer sourceId, Date time, Date version) {
        this.id = id;
        this.userId = userId;
        this.money = money;
        this.balance = balance;
        this.type = type;
        this.explain = explain;
        this.detail = detail;
        this.sourceId = sourceId;
        this.time = time;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "FuturesFundDetailInfo{" +
                "id=" + id +
                ", userId=" + userId +
                ", money=" + money +
                ", balance=" + balance +
                ", type=" + type +
                ", explain='" + explain + '\'' +
                ", detail='" + detail + '\'' +
                ", sourceId=" + sourceId +
                ", time=" + time +
                ", version=" + version +
                '}';
    }
}
