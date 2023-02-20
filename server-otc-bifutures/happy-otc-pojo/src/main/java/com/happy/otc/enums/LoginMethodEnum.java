package com.happy.otc.enums;


public enum LoginMethodEnum {

    MOBILE(1),
    EMAIL(2);

    private Integer value;

    private LoginMethodEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static LoginMethodEnum getInstance(Integer value) {
        if (value != null) {
            LoginMethodEnum[] instArray = LoginMethodEnum.values();
            for (LoginMethodEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
