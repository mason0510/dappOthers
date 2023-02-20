package com.happy.otc.enums;


public enum RelevantKindEnum {

    /**
     * 人民币
     */
    CNY("CNY"),
    /**
     * 加币
     */
    CAD("CAD"),
    /**
     * 美元
     */
    USD("USD");


    private String value;

    private RelevantKindEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static RelevantKindEnum getInstance(String value) {
        if (value != null) {
            RelevantKindEnum[] instArray = RelevantKindEnum.values();
            for (RelevantKindEnum instance : instArray) {
                if (instance.getValue().equals(value)) {
                    return instance;
                }
            }

        }
        return null;
    }
}
