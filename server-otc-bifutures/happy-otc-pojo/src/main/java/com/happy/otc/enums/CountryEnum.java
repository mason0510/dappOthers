package com.happy.otc.enums;

/**
 * Created by niyang on 2017/12/28.
 */
public enum CountryEnum {

    CHINA("+86"),
    AMERICA("+1"),
    CANADA("+1"),
    AUSTRALIA("+61");

    private String value;

    private CountryEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static CountryEnum getInstance(String value) {
        if (value != null) {
            CountryEnum[] instArray = CountryEnum.values();
            for (CountryEnum instance : instArray) {
                if (instance.getValue().equals(value)) {
                    return instance;
                }
            }

        }
        return null;
    }
}
