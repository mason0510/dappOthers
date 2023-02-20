package com.happy.otc.enums;

public enum AppealTypeEnum {
    NULL(0),       //无
    NO_PAY(1),     //对方未付款
    NO_PASS(2),    //对方未放行
    NO_REPLY(3),   //对方无应答
    FAKE(4),       //对方有欺诈行为
    OTHER(5);      //其他

    private Integer value;

    private AppealTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static AppealTypeEnum getInstance(Integer value) {
        if (value != null) {
            AppealTypeEnum[] instArray = AppealTypeEnum.values();
            for (AppealTypeEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
