package com.happy.otc.vo;


import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

public class ExchangeInfoVO implements Serializable {
    @ApiModelProperty("最新价美元 币种使用")
    private Double priceUSD;
    @ApiModelProperty("最新价人民币 币种使用")
    private Double priceCAD;
    @ApiModelProperty("最新价加拿大币 币种使用")
    private Double priceCNY;
    @ApiModelProperty("当前时间")
    private Date currentTime;

    public ExchangeInfoVO() {
    }

    public Double getPriceUSD() {
        return priceUSD;
    }

    public void setPriceUSD(Double priceUSD) {
        this.priceUSD = priceUSD;
    }

    public Double getPriceCAD() {
        return priceCAD;
    }

    public void setPriceCAD(Double priceCAD) {
        this.priceCAD = priceCAD;
    }

    public Double getPriceCNY() {
        return priceCNY;
    }

    public void setPriceCNY(Double priceCNY) {
        this.priceCNY = priceCNY;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }
}
