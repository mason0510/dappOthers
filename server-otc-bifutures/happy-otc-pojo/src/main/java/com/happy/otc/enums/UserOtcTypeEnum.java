package com.happy.otc.enums;

public enum UserOtcTypeEnum {
    NORMAL(0),      //普通客户
    SELLER(1);      //商家

    private Integer value;

    private UserOtcTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static UserOtcTypeEnum getInstance(Integer value) {
        if (value != null) {
            UserOtcTypeEnum[] instArray = UserOtcTypeEnum.values();
            for (UserOtcTypeEnum instance : instArray) {
                if (instance.getValue().equals(value)) {
                    return instance;
                }
            }
        }
        return null;
    }
}
