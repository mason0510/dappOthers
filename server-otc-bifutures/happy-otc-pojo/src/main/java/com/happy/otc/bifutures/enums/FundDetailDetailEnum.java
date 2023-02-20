package com.happy.otc.bifutures.enums;

/**
 * Created by Administrator on 2018\11\15 0015.
 */
public enum FundDetailDetailEnum {

    /**
     * 充值存入
     */
    FUTURES_CHARGE_IN("OTC账户划转至合约账户"),
    /**
     * 提款取出
     */
    FUTURES_CHARGE_OUT("合约账户划转至OTC账户"),

    FUTURES_STRATEGY_CREATE_OUT("合约交易[#{strategyId}]开仓成功，冻结保证金#{margin}"),

    FUTURES_STRATEGY_SET_PRINCIPAL_OUT("合约交易[#{strategyId}]追加保证金成功，冻结保证金#{margin}"),

    FUTURES_SIM_STRATEGY_CREATE_MONEY__IN("模拟合约交易[#{strategyId}]开仓成功，赠送金额金#{margin}"),

    FUTURES_STRATEGY_SERVIC_CHARGE_OUT("合约交易[#{strategyId}]开仓成功，扣除交易综合费#{serviceCharge}"),

    FUTURES_STRATEGY_REFUND_STRATEGY_IN("合约交易[#{strategyId}]撤销，退还保证金#{margin}，退还交易综合费#{serviceCharge}"),

    FUTURES_STRATEGY_REFUND_STRATEGY_INVESTOR_IN("合约交易[#{strategyId}]撤销，退还保证金#{margin}，退还交易综合费#{serviceCharge}"),

    FUTURES_STRATEGY_FINISH_GAIN("合约交易平仓成功[#{strategyId}]盈利#{profit}，退还保证金#{margin}"),

    FUTURES_STRATEGY_FINISH_LOSE("合约交易平仓成功[#{strategyId}]亏损#{profit}，扣除保证金#{margin}，退还#{money}"),

    FUTURES_STRATEGY_DEFERCHARGE("合约交易[#{strategyId}]扣递延费，扣除#{money}");


    private String value;

    private FundDetailDetailEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FundDetailDetailEnum getByValue(Integer value){
        if(value!=null){
            for(FundDetailDetailEnum enu:values()){
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
