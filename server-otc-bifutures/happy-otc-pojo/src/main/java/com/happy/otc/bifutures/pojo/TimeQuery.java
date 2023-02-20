package com.happy.otc.bifutures.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2018\12\4 0004.
 */
public class TimeQuery implements Serializable {
    private static final long serialVersionUID = 2410124271704348672L;

    private Date leSellTime;
    private Date geSellTime;
    private String biCode;

    public TimeQuery(){}
    public TimeQuery(Date leSellTime, Date geSellTime) {
        this.leSellTime = leSellTime;
        this.geSellTime = geSellTime;
    }

    public Date getLeSellTime() {
        return leSellTime;
    }

    public void setLeSellTime(Date leSellTime) {
        this.leSellTime = leSellTime;
    }

    public Date getGeSellTime() {
        return geSellTime;
    }

    public void setGeSellTime(Date geSellTime) {
        this.geSellTime = geSellTime;
    }

    public String getBiCode() {
        return biCode;
    }

    public void setBiCode(String biCode) {
        this.biCode = biCode;
    }
}
