package com.happy.otc.enums;


public enum LanguageEnum {

    /**
     * 1 中文
     */
    CHINESE(1),
    /**
     * 2 英文
     */
    ENGLISH(2);


    private Integer value;

    private LanguageEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static LanguageEnum getInstance(Integer value) {
        if (value != null) {
            LanguageEnum[] instArray = LanguageEnum.values();
            for (LanguageEnum instance : instArray) {
                if (instance.getValue().equals(value)) {
                    return instance;
                }
            }

        }
        return null;
    }
}
