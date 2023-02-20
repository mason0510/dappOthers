package com.happy.otc.enums;


public enum CapitalLogTypeEnum {

    UNKNOWN(0,"未知"),
    BUY(1,"买入"),
    SELL(2,"卖出"),
    CHARGE(3,"充币"),
    WITHDRAWALS(4,"提币"),
    FROZEN(5,"资金冻结"),
    THAW(6,"资金解冻"),
    CUT_OUT(7,"资金划出"),
    CUT_IN(8,"资金划入");

    private Integer value;
    private String name;
    private CapitalLogTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static CapitalLogTypeEnum getInstance(Integer value) {
        if (value != null) {
            CapitalLogTypeEnum[] instArray = CapitalLogTypeEnum.values();
            for (CapitalLogTypeEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }
        }
        return null;
    }
}
