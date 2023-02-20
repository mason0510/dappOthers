package com.happy.otc.enums;


public enum IdentityStautsEnum {
    NO(0),          //未认证
    WAIT(1),        //待认证
    PASS(2),        //已认证
    REJECT(3);      //认证驳回

    private Integer value;

    private IdentityStautsEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static IdentityStautsEnum getInstance(Integer value) {
        if (value != null) {
            IdentityStautsEnum[] instArray = IdentityStautsEnum.values();
            for (IdentityStautsEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
