package com.happy.otc.enums;

public enum CapitalInOutTypeEnum {
    UN_KNOWN(0),       //无
    CAPITAL_IN(1),     //充币
    CAPITAL_OUT(2);    //提币

    private Integer value;

    private CapitalInOutTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static CapitalInOutTypeEnum getInstance(Integer value) {
        if (value != null) {
            CapitalInOutTypeEnum[] instArray = CapitalInOutTypeEnum.values();
            for (CapitalInOutTypeEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
