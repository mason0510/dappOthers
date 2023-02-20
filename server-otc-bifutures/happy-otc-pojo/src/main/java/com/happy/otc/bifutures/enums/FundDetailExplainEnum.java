package com.happy.otc.bifutures.enums;

/**
 * Created by Administrator on 2018\11\15 0015.
 */
public enum FundDetailExplainEnum {

    /**
     * 充值存入
     */
    CHARGE_IN("合约账户充值存入"),
    /**
     * 提款取出
     */
    WITHDRAW_OUT("合约账户提币取出"),

    /**
     * 追加保证金
     */
    PRINCIPAL_APPEND_OUT("合约交易追加保证金"),

    /**
     * 其他收入
     */
    OTHER_IN("其他收入"),
    /**
     * 其他支出
     */
    OTHER_OUT("其他支出"),
    /**
     * 策略递延费支出
     */
    STRATEGY_DEFER_CHARGE_OUT("合约交易递延费支出"),


    SELL_STRATEGY_DEFER_CHARGE_IN("合约交易触发休市平仓"),

    SELL_LIMITE_STRATEGY_IN("合约交易限价买入委托到期撤单成功"),

    SELL_LIMITE_CURRENT_IN("合约交易限价委托撤单成功"),

    BUY_LIMITE_STRATEGY_OUT("合约交易限价委托买入成功"),

    /**
     * 策略交易综合费支出
     */
    STRATEGY_SERVICE_CHARGE_OUT("交易综合费支出"),

    /**
     * 策略自动补足信用金支出
     */
    STRATEGY_SERVICE_AUTOMATIC_PRINCIPAL_OUT("合约交易自动补足信用金支出"),

    /**
     * 点买成功
     */
    BUY_STRATEGY_SUCCESS_OUT("合约交易点买成功"),

    /**
     * 点买失败
     */
    BUY_STRATEGY_FAIL_IN("合约交易点买失败"),

    /**
     *点卖成功
     */
    SELL_STRATEGY_SUCCESS_IN("合约交易点卖成功"),

    SELL_GAIN_STRATEGY_SUCCESS_IN("合约交易触发止盈平仓"),

    SELL_LOSS_STRATEGY_SUCCESS_IN("合约交易触发止损平仓"),

    SELL_CLOSE_STRATEGY_SUCCESS_IN("合约交易触发强平价平仓"),

    /**
     *追加保证金成功
     */
    UPDATE_STRATEGY_PRINCIPAL_OUT("追加保证金成功");


    private String value;

    private FundDetailExplainEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FundDetailExplainEnum getByValue(Integer value){
        if(value!=null){
            for(FundDetailExplainEnum enu:values()){
                if(enu.value.equals(value)){
                    return enu;
                }
            }
        }
        return null;
    }

    public String toString(){
        return value;
    }
}
