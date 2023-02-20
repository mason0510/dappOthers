package com.happy.otc.bifutures.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class FuturesStrategyQuery implements Serializable {
    private static final long serialVersionUID = -6714590010940382764L;
    private Long id;//策略id
    private Long userId;//用户id
    private String userName;//用户名
    private String biCode;//币种代码
    private BigDecimal buyPriceOrder;//买入委托价
    private BigDecimal buyPriceDeal;//买入成交价
    private BigDecimal sellPriceDeal;//卖出成交价
    private Integer amount;//数量（手数）
    private Integer type;//策略类型
    private List<Integer> status;//策略状态
    private Integer statu;
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
    private Date leInitiateTime;
    private Date geInitiateTime;
    private Date leSellTime;
    private Date geSellTime;

    public FuturesStrategyQuery(){}

    public FuturesStrategyQuery(Long id, Long userId, String userName, String biCode, BigDecimal buyPriceOrder, BigDecimal buyPriceDeal, BigDecimal sellPriceDeal, Integer amount, Integer type, List<Integer> status, Integer mold, String direction, BigDecimal principal, BigDecimal gainPrice, BigDecimal lossPrice, BigDecimal closePrice, BigDecimal serviceCharge, BigDecimal deferCharge, Integer isDefer, Integer isToday, String closeReason, String cancelReason, Date leInitiateTime, Date geInitiateTime, Date leSellTime, Date geSellTime) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.biCode = biCode;
        this.buyPriceOrder = buyPriceOrder;
        this.buyPriceDeal = buyPriceDeal;
        this.sellPriceDeal = sellPriceDeal;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.mold = mold;
        this.direction = direction;
        this.principal = principal;
        this.gainPrice = gainPrice;
        this.lossPrice = lossPrice;
        this.closePrice = closePrice;
        this.serviceCharge = serviceCharge;
        this.deferCharge = deferCharge;
        this.isDefer = isDefer;
        this.isToday = isToday;
        this.closeReason = closeReason;
        this.cancelReason = cancelReason;
        this.leInitiateTime = leInitiateTime;
        this.geInitiateTime = geInitiateTime;
        this.leSellTime = leSellTime;
        this.geSellTime = geSellTime;
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

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
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

    public Date getLeInitiateTime() {
        return leInitiateTime;
    }

    public void setLeInitiateTime(Date leInitiateTime) {
        this.leInitiateTime = leInitiateTime;
    }

    public Date getGeInitiateTime() {
        return geInitiateTime;
    }

    public void setGeInitiateTime(Date geInitiateTime) {
        this.geInitiateTime = geInitiateTime;
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

    public Integer getStatu() {
        return statu;
    }

    public void setStatu(Integer statu) {
        this.statu = statu;
    }
}
