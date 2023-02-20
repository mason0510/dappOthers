package com.happy.otc.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class FundVO implements Serializable {

    @ApiModelProperty(value = "CNY期货资产")
    private BigDecimal futuresAssetsCNY;
    @ApiModelProperty(value = "USD期货资产")
    private BigDecimal futuresAssetsUSD;
    @ApiModelProperty(value = "USDT期货资产")
    private BigDecimal futuresAssets;
    @ApiModelProperty(value = "CNY的OTC资产")
    private BigDecimal otcAssetsCNY;
    @ApiModelProperty(value = "USD的OTC资产")
    private BigDecimal otcAssetsUSD;
    @ApiModelProperty(value = "OTC资产")
    private BigDecimal otcAssets;
    @ApiModelProperty(value = "CNY总资产")
    private BigDecimal AssetsCNY;
    @ApiModelProperty(value = "USD总资产")
    private BigDecimal AssetsUSD;
    @ApiModelProperty(value = "USDT总资产")
    private BigDecimal Assets;
    @ApiModelProperty(value = "用户OTC的资产详情")
    private List<CapitalInfoVO> list;
    @ApiModelProperty(value = "已用保证金")
    private BigDecimal userdBond;
    @ApiModelProperty(value = "可用保证金")
    private BigDecimal availableBond;
    @ApiModelProperty(value = "持仓盈亏")
    private BigDecimal profit;
    @ApiModelProperty(value = "当日平仓盈亏")
    private BigDecimal profitToday;


    public FundVO() {
        this.futuresAssetsCNY = BigDecimal.ZERO;
        this.futuresAssetsUSD = BigDecimal.ZERO;
        this.futuresAssets = BigDecimal.ZERO;
        this.otcAssetsCNY = BigDecimal.ZERO;
        this.otcAssetsUSD = BigDecimal.ZERO;
        this.otcAssets = BigDecimal.ZERO;
    }

    public BigDecimal getFuturesAssetsCNY() {
        return futuresAssetsCNY;
    }

    public void setFuturesAssetsCNY(BigDecimal futuresAssetsCNY) {
        this.futuresAssetsCNY = futuresAssetsCNY;
    }

    public BigDecimal getFuturesAssetsUSD() {
        return futuresAssetsUSD;
    }

    public void setFuturesAssetsUSD(BigDecimal futuresAssetsUSD) {
        this.futuresAssetsUSD = futuresAssetsUSD;
    }

    public BigDecimal getFuturesAssets() {
        return futuresAssets;
    }

    public void setFuturesAssets(BigDecimal futuresAssets) {
        this.futuresAssets = futuresAssets;
    }

    public BigDecimal getOtcAssetsCNY() {
        return otcAssetsCNY;
    }

    public void setOtcAssetsCNY(BigDecimal otcAssetsCNY) {
        this.otcAssetsCNY = otcAssetsCNY;
    }

    public BigDecimal getOtcAssetsUSD() {
        return otcAssetsUSD;
    }

    public void setOtcAssetsUSD(BigDecimal otcAssetsUSD) {
        this.otcAssetsUSD = otcAssetsUSD;
    }

    public BigDecimal getOtcAssets() {
        return otcAssets;
    }

    public void setOtcAssets(BigDecimal otcAssets) {
        this.otcAssets = otcAssets;
    }

    public BigDecimal getAssetsCNY() {
        return AssetsCNY;
    }

    public void setAssetsCNY(BigDecimal assetsCNY) {
        AssetsCNY = assetsCNY;
    }

    public BigDecimal getAssetsUSD() {
        return AssetsUSD;
    }

    public void setAssetsUSD(BigDecimal assetsUSD) {
        AssetsUSD = assetsUSD;
    }

    public BigDecimal getAssets() {
        return Assets;
    }

    public void setAssets(BigDecimal assets) {
        Assets = assets;
    }

    public BigDecimal getUserdBond() {
        return userdBond;
    }

    public void setUserdBond(BigDecimal userdBond) {
        this.userdBond = userdBond;
    }

    public BigDecimal getAvailableBond() {
        return availableBond;
    }

    public void setAvailableBond(BigDecimal availableBond) {
        this.availableBond = availableBond;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getProfitToday() {
        return profitToday;
    }

    public void setProfitToday(BigDecimal profitToday) {
        this.profitToday = profitToday;
    }

    public List<CapitalInfoVO> getList() {
        return list;
    }

    public void setList(List<CapitalInfoVO> list) {
        this.list = list;
    }
}
