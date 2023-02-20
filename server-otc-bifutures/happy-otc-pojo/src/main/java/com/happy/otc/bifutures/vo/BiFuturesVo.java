package com.happy.otc.bifutures.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class BiFuturesVo implements Serializable {

    private static final long serialVersionUID = 7304762177768069906L;
    @ApiModelProperty(value = "币种代码")
    private String biCode;
    @ApiModelProperty(value = "币简介信息")
    private String explain;

    public String getBiCode() {
        return biCode;
    }

    public void setBiCode(String biCode) {
        this.biCode = biCode;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    @Override
    public String toString() {
        return "BiFuturesVo{" +
                "biCode='" + biCode + '\'' +
                ", explain='" + explain + '\'' +
                '}';
    }
}
