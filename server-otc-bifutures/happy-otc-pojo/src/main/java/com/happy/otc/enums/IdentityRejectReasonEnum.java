package com.happy.otc.enums;

public enum IdentityRejectReasonEnum {
    NULL(0),           //无
    INCOMPLETE(1),     //边角不完整
    FONT_ERROR(2),     //字体不清晰
    LIGHT_ERROR(3);    //亮度不均匀

    private Integer value;

    private IdentityRejectReasonEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static IdentityRejectReasonEnum getInstance(Integer value) {
        if (value != null) {
            IdentityRejectReasonEnum[] instArray = IdentityRejectReasonEnum.values();
            for (IdentityRejectReasonEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
