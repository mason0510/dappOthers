package com.happy.otc.bifutures.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class FuturesStrategy implements Serializable {
    private static final long serialVersionUID = -6714590010940382764L;
    private Long id;//策略id
    private Long userId;//用户id
    private String userName;//用户名
    private String biCode;//币种代码
    private BigDecimal buyPriceOrder;//买入委托价
    private BigDecimal buyPriceDeal;//买入成交价
    private BigDecimal sellPriceDeal;//卖出成交价
    private BigDecimal contractValue;//每手价值
    private Integer amount;//数量（手数）
    private Integer type;//策略类型
    private Integer status;//策略状态
    private Integer mold;//点买策略方式
    private String direction;//方向
    private BigDecimal principal;//保证金
    private BigDecimal gainPrice;//止盈价
    private BigDecimal lossPrice;//止损价
    private BigDecimal closePrice;//强平价
    private BigDecimal serviceCharge;//服务费
    private BigDecimal deferCharge;//过夜费
    private Integer isDefer;//是否过夜开关
    private Integer isToday;//是否长期委托
    private String closeReason;//平仓原因
    private String cancelReason;//撤单原因
    private Date buyDealTime;//买入时间
    private Date buyOrderTime;//委托时间
    private Date sellDealTime;//卖出时间
    private Date cancelTime;//撤单时间
    private String remark;//备注
    private Date version;//版本
    private BigDecimal profit;//盈亏


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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBiCode() {
        return biCode;
    }

    public void setBiCode(String biCode) {
        this.biCode = biCode;
    }

    public BigDecimal getBuyPriceOrder() {
        return buyPriceOrder;
    }

    public void setBuyPriceOrder(BigDecimal buyPriceOrder) {
        this.buyPriceOrder = buyPriceOrder;
    }

    public BigDecimal getBuyPriceDeal() {
        return buyPriceDeal;
    }

    public void setBuyPriceDeal(BigDecimal buyPriceDeal) {
        this.buyPriceDeal = buyPriceDeal;
    }

    public BigDecimal getSellPriceDeal() {
        return sellPriceDeal;
    }

    public void setSellPriceDeal(BigDecimal sellPriceDeal) {
        this.sellPriceDeal = sellPriceDeal;
    }

    public BigDecimal getContractValue() {
        return contractValue;
    }

    public void setContractValue(BigDecimal contractValue) {
        this.contractValue = contractValue;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMold() {
        return mold;
    }

    public void setMold(Integer mold) {
        this.mold = mold;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getGainPrice() {
        return gainPrice;
    }

    public void setGainPrice(BigDecimal gainPrice) {
        this.gainPrice = gainPrice;
    }

    public BigDecimal getLossPrice() {
        return lossPrice;
    }

    public void setLossPrice(BigDecimal lossPrice) {
        this.lossPrice = lossPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getDeferCharge() {
        return deferCharge;
    }

    public void setDeferCharge(BigDecimal deferCharge) {
        this.deferCharge = deferCharge;
    }

    public Integer getIsDefer() {
        return isDefer;
    }

    public void setIsDefer(Integer isDefer) {
        this.isDefer = isDefer;
    }

    public Integer getIsToday() {
        return isToday;
    }

    public void setIsToday(Integer isToday) {
        this.isToday = isToday;
    }

    public String getCloseReason() {
        return closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Date getBuyDealTime() {
        return buyDealTime;
    }

    public void setBuyDealTime(Date buyDealTime) {
        this.buyDealTime = buyDealTime;
    }

    public Date getBuyOrderTime() {
        return buyOrderTime;
    }

    public void setBuyOrderTime(Date buyOrderTime) {
        this.buyOrderTime = buyOrderTime;
    }

    public Date getSellDealTime() {
        return sellDealTime;
    }

    public void setSellDealTime(Date sellDealTime) {
        this.sellDealTime = sellDealTime;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    @Override
    public String toString() {
        return "FuturesStrategy{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", biCode='" + biCode + '\'' +
                ", buyPriceOrder=" + buyPriceOrder +
                ", buyPriceDeal=" + buyPriceDeal +
                ", sellPriceDeal=" + sellPriceDeal +
                ", contractValue=" + contractValue +
                ", amount=" + amount +
                ", type=" + type +
                ", status=" + status +
                ", mold=" + mold +
                ", direction='" + direction + '\'' +
                ", principal=" + principal +
                ", gainPrice=" + gainPrice +
                ", lossPrice=" + lossPrice +
                ", closePrice=" + closePrice +
                ", serviceCharge=" + serviceCharge +
                ", deferCharge=" + deferCharge +
                ", isDefer=" + isDefer +
                ", isToday=" + isToday +
                ", closeReason='" + closeReason + '\'' +
                ", cancelReason='" + cancelReason + '\'' +
                ", buyDealTime=" + buyDealTime +
                ", buyOrderTime=" + buyOrderTime +
                ", sellDealTime=" + sellDealTime +
                ", cancelTime=" + cancelTime +
                ", remark='" + remark + '\'' +
                ", version=" + version +
                ", profit=" + profit +
                '}';
    }
}
