package com.happy.otc.bifutures.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class FuturesFundInOutInfo implements Serializable {

    private static final long serialVersionUID = -4182892128509990983L;
    private Long id;//id
    private Integer userId;//用户id
    private BigDecimal money;//进出资金
    private Integer type;//是否成功
    private String detail;//注释
    private String remark;//备注
    private BigDecimal charge;//费用
    private Integer status;//状态
    private Date requestTime;//请求时间
    private Date disposeTime;//完成时间
    private Date version;//版本

    public FuturesFundInOutInfo(Long id, Integer userId, BigDecimal money, Integer type, String detail, String remark, BigDecimal charge, Integer status, Date requestTime, Date disposeTime, Date version) {
        this.id = id;
        this.userId = userId;
        this.money = money;
        this.type = type;
        this.detail = detail;
        this.remark = remark;
        this.charge = charge;
        this.status = status;
        this.requestTime = requestTime;
        this.disposeTime = disposeTime;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public void setCharge(BigDecimal charge) {
        this.charge = charge;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getDisposeTime() {
        return disposeTime;
    }

    public void setDisposeTime(Date disposeTime) {
        this.disposeTime = disposeTime;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "FuturesFundInOutInfo{" +
                "id=" + id +
                ", userId=" + userId +
                ", money=" + money +
                ", type=" + type +
                ", detail='" + detail + '\'' +
                ", remark='" + remark + '\'' +
                ", charge=" + charge +
                ", status=" + status +
                ", requestTime=" + requestTime +
                ", disposeTime=" + disposeTime +
                ", version=" + version +
                '}';
    }
}
