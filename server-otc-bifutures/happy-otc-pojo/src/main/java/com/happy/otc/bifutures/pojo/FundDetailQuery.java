package com.happy.otc.bifutures.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018\11\16 0016.
 */
public class FundDetailQuery implements Serializable {
    private static final long serialVersionUID = 7941366711355678682L;
    private Long id;
    private Long userId;
    private Integer type;
    private List<String> explain;
    private String detail;
    private Date requestTimeStart;
    private Date requestTimeEnd;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getExplain() {
        return explain;
    }

    public void setExplain(List<String> explain) {
        this.explain = explain;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getRequestTimeStart() {
        return requestTimeStart;
    }

    public void setRequestTimeStart(Date requestTimeStart) {
        this.requestTimeStart = requestTimeStart;
    }

    public Date getRequestTimeEnd() {
        return requestTimeEnd;
    }

    public void setRequestTimeEnd(Date requestTimeEnd) {
        this.requestTimeEnd = requestTimeEnd;
    }

    @Override
    public String toString() {
        return "FundDetailQuery{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", explain=" + explain +
                ", detail='" + detail + '\'' +
                ", requestTimeStart=" + requestTimeStart +
                ", requestTimeEnd=" + requestTimeEnd +
                '}';
    }
}
