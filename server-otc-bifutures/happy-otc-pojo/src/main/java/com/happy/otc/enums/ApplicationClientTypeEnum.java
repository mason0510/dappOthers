package com.happy.otc.enums;


public enum ApplicationClientTypeEnum {

    WEB(1),
    IOS(2),
    ANDROID(3),;

    private Integer value;

    private ApplicationClientTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static ApplicationClientTypeEnum getInstance(Integer value) {
        if (value != null) {
            ApplicationClientTypeEnum[] instArray = ApplicationClientTypeEnum.values();
            for (ApplicationClientTypeEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
