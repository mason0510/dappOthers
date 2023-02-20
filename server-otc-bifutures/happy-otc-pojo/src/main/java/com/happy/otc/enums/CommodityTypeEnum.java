package com.happy.otc.enums;


public enum CommodityTypeEnum {

    BUY(0,"商户可以在线购买"),
    SELL(1,"商户可以在线出售");

    private Integer value;
    private String name;
    private CommodityTypeEnum(Integer value,String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static CommodityTypeEnum getInstance(Integer value) {
        if (value != null) {
            CommodityTypeEnum[] instArray = CommodityTypeEnum.values();
            for (CommodityTypeEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
