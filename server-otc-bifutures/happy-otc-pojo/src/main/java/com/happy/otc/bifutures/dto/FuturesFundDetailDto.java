package com.happy.otc.bifutures.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class FuturesFundDetailDto implements Serializable {
    private static final long serialVersionUID = -7806812555353514964L;
    private Long id;//id
    private Long userId;//用户id
    private BigDecimal money;//流水资金
    private BigDecimal balance;//用户余额
    private Integer type;//是否成功
    private String explain;//注释
    private String detail;//备注
    private Long sourceId;//策略来源id
    private Date time;//


    public FuturesFundDetailDto(){}

    public FuturesFundDetailDto(Long id, Long userId, BigDecimal money, BigDecimal balance, Integer type, String explain, String detail, Long sourceId, Date time) {
        this.id = id;
        this.userId = userId;
        this.money = money;
        this.balance = balance;
        this.type = type;
        this.explain = explain;
        this.detail = detail;
        this.sourceId = sourceId;
        this.time = time;
    }

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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
