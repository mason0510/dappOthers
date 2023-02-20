package com.happy.otc.enums;


public enum KindEnum {

    /**
     * BTC 比特币
     */
    BTC("BTC"),
    /**
     * USDT 泰达币
     */
    USDT("USDT"),
    /**
     * TUSD
     */
    TUSD("TUSD"),
    /**
     * USDC
     */
    USDC("USDC"),
    /**
     * GUSD
     */
    GUSD("GUSD"),
    /**
     * PAX
     */
    PAX("PAX"),
    /**
     * ETH 以太坊
     */
    ETH("ETH");


    private String value;

    private KindEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static KindEnum getInstance(String value) {
        if (value != null) {
            KindEnum[] instArray = KindEnum.values();
            for (KindEnum instance : instArray) {
                if (instance.getValue().equals(value)) {
                    return instance;
                }
            }

        }
        return null;
    }
}
