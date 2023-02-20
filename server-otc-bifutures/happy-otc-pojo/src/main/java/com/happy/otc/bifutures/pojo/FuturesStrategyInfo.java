package com.happy.otc.bifutures.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class FuturesStrategyInfo implements Serializable {
    private static final long serialVersionUID = 5653903982399877213L;
    private Long id;//策略id
    private Integer userId;//用户id
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
    private BigDecimal serviceCharge;//服务费
    private BigDecimal deferCharge;//过夜费
    private Integer isDeger;//是否过夜开关
    private Integer isToday;//是否长期委托
    private String closeReason;//平仓原因
    private String cancelReason;//撤单原因
    private Date buyDealTime;//买入时间
    private Date buyOrderTime;//委托时间
    private Date sellDealTime;//卖出时间
    private Date cancelTime;//撤单时间
    private String remark;//备注
    private Date version;//版本

    public FuturesStrategyInfo(Long id, Integer userId, String userName, String biCode, BigDecimal buyPriceOrder, BigDecimal buyPriceDeal, BigDecimal sellPriceDeal, BigDecimal contractValue, Integer amount, Integer type, Integer status, Integer mold, String direction, BigDecimal principal, BigDecimal gainPrice, BigDecimal lossPrice, BigDecimal serviceCharge, BigDecimal deferCharge, Integer isDeger, Integer isToday, String closeReason, String cancelReason, Date buyDealTime, Date buyOrderTime, Date sellDealTime, Date cancelTime, String remark, Date version) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.biCode = biCode;
        this.buyPriceOrder = buyPriceOrder;
        this.buyPriceDeal = buyPriceDeal;
        this.sellPriceDeal = sellPriceDeal;
        this.contractValue = contractValue;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.mold = mold;
        this.direction = direction;
        this.principal = principal;
        this.gainPrice = gainPrice;
        this.lossPrice = lossPrice;
        this.serviceCharge = serviceCharge;
        this.deferCharge = deferCharge;
        this.isDeger = isDeger;
        this.isToday = isToday;
        this.closeReason = closeReason;
        this.cancelReason = cancelReason;
        this.buyDealTime = buyDealTime;
        this.buyOrderTime = buyOrderTime;
        this.sellDealTime = sellDealTime;
        this.cancelTime = cancelTime;
        this.remark = remark;
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

    public Integer getIsDeger() {
        return isDeger;
    }

    public void setIsDeger(Integer isDeger) {
        this.isDeger = isDeger;
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
}
