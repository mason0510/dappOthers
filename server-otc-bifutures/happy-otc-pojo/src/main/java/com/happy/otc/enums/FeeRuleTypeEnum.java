package com.happy.otc.enums;


public enum FeeRuleTypeEnum {

    SELL(1,"售出"),
    BUY(2,"收购"),
    WITHDRAWALS(3,"提币");

    private Integer value;
    private String name;
    private FeeRuleTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static FeeRuleTypeEnum getInstance(Integer value) {
        if (value != null) {
            FeeRuleTypeEnum[] instArray = FeeRuleTypeEnum.values();
            for (FeeRuleTypeEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }
        }
        return null;
    }
}
