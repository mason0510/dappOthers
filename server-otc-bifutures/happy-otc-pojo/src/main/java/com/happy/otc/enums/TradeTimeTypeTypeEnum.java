package com.happy.otc.enums;


public enum TradeTimeTypeTypeEnum {

    FIFTEENMINUTES(0,"15分钟",15*60000L),
    TWENTYMINUTES(1,"20分钟",20*60000L),
    THIRTYMINUTES(2,"30分钟",30*60000L);

    private Integer value;
    private String name;
    private Long leaveTime;
    private TradeTimeTypeTypeEnum(Integer value, String name, Long leaveTime) {
        this.value = value;
        this.name = name;
        this.leaveTime = leaveTime;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public Long getLeaveTime() {
        return this.leaveTime;
    }

    public static TradeTimeTypeTypeEnum getInstance(Integer value) {
        if (value != null) {
            TradeTimeTypeTypeEnum[] instArray = TradeTimeTypeTypeEnum.values();
            for (TradeTimeTypeTypeEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
