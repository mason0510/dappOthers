package com.happy.otc.enums;

public enum PayMethodEnums {

    /**
     * 支付宝
     */
    ALI_PAY(1,"ALI_PAY"),
    /**
     * 微信支付
     */
    WECHAT_PAY(2,"WECHAT_PAY"),

    /**
     * 银行卡支付
     */
    BANK_CARD(3,"BANK_CARD"),
    /**
     * PAY_PAI
     */
    PAY_PAI(4,"PAY_PAI"),
    /**
     * 西联汇款
     */
    XILIAN_REMITTANCE(5,"XILIAN_REMITTANCE"),
    /**
     * SWIFT
     */
    SWIFT_REMITTANCE(6,"SWIFT_REMITTANCE"),
    /**
     * PAY_NOW
     */
    PAY_NOW(7,"PAY_NOW"),
    /**
     * PAY_TM
     */
    PAY_TM(8,"PAY_TM"),
    /**
     * INTERAC_E_TRANSFER
     */
    INTERAC_E_TRANSFER(9,"INTERAC_E_TRANSFER");


    private Integer value;

    private String name;

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    private PayMethodEnums(Integer value,String name) {

        this.value = value;
        this.name = name;
    }

    public static PayMethodEnums getInstance(Integer value) {
        if (value != null) {
            PayMethodEnums[] instArray = PayMethodEnums.values();
            for (PayMethodEnums instance : instArray) {
                if (instance.getValue().equals(value)) {
                    return instance;
                }
            }

        }
        return null;
    }
}
