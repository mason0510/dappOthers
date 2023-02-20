package com.happy.otc.bifutures.enums;

/**
 * Created by Administrator on 2018\11\20 0020.
 */
public enum BiFuturesEnum {
    BTC("BTC","比特币"),
    ETH("ETH","以太坊"),
    EOS("EOS","柚子"),
    LTC("LTC","莱特币"),
    XRP("XRP","瑞波币"),
    BCH("BCH","比特现金");

    private String value;
    private String message;

    BiFuturesEnum(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BiFuturesEnum{" +
                "value='" + value + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static BiFuturesEnum getByValue(String value) {
        if(value != null) {
            BiFuturesEnum[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                BiFuturesEnum enu = arr$[i$];
                if(enu.value.equals(value)) {
                    return enu;
                }
            }
        }

        return null;
    }

    public static String getMessageByValue(String value) {
        if(value != null) {
            BiFuturesEnum[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                BiFuturesEnum enu = arr$[i$];
                if(enu.value.equals(value)) {
                    return enu.getMessage();
                }
            }
        }

        return null;
    }
}
