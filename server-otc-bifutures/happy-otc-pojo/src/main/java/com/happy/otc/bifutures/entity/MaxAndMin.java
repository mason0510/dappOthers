package com.happy.otc.bifutures.entity;

import java.io.Serializable;

public class MaxAndMin implements Serializable {

    private static final long serialVersionUID = 8262086624445809241L;

    private Integer Bamount;
    private Integer Samount;

    public MaxAndMin() {
    }
    public MaxAndMin(Integer bamount, Integer samount) {
        Bamount = bamount;
        Samount = samount;
    }

    public Integer getBamount() {
        return Bamount;
    }

    public void setBamount(Integer bamount) {
        Bamount = bamount;
    }

    public Integer getSamount() {
        return Samount;
    }

    public void setSamount(Integer samount) {
        Samount = samount;
    }
}
