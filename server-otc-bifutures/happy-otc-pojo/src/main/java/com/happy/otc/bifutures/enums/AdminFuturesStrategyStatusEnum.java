package com.happy.otc.bifutures.enums;

import com.happy.otc.bifutures.utill.CollectionUtil;

import java.util.List;

/**
 * Created by js on 2016/6/3.
 */
public enum AdminFuturesStrategyStatusEnum {

    MATCHING(CollectionUtil.asList(1), "待接单"),
    HOLDING(CollectionUtil.asList(2), "持仓中"),
    LIQUIDATD(CollectionUtil.asList(3), "结算成功"),
    MISBIRTH(CollectionUtil.asList(4), "已流单");

    private List<Integer> value;
    private String message;

    private AdminFuturesStrategyStatusEnum(List<Integer> value, String message) {
        this.value = value;
        this.message = message;
    }

    public List<Integer> getValue() {
        return this.value;
    }

    public String getMessage() {
        return this.message;
    }

    public static AdminFuturesStrategyStatusEnum getByValue(Integer value) {
        if(value != null) {
            AdminFuturesStrategyStatusEnum[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                AdminFuturesStrategyStatusEnum enu = arr$[i$];
                if(enu.value.indexOf(value) != -1) {
                    return enu;
                }
            }
        }

        return null;
    }

    public String toString() {
        return this.value + "|" + this.message;
    }
}
