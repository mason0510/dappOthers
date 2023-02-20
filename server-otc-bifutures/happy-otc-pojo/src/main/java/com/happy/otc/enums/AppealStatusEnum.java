package com.happy.otc.enums;


public enum AppealStatusEnum {

    WAIT(0,"处理中"),
    SUCC(1,"胜诉"),
    FAIL(2,"败诉"),
    CANCEL(3,"取消");

    private Integer value;
    private String name;
    private AppealStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static AppealStatusEnum getInstance(Integer value) {
        if (value != null) {
            AppealStatusEnum[] instArray = AppealStatusEnum.values();
            for (AppealStatusEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }
        }
        return null;
    }
}
