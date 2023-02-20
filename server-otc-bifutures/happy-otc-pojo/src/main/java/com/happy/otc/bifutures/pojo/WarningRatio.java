package com.happy.otc.bifutures.pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018\12\11 0011.
 */
public class WarningRatio implements Serializable {
    private static final long serialVersionUID = 5096600083952230449L;
    String BtcRatio ;
    String BchRatio ;
    String LtcRatio ;
    String XrpRatio ;
    String EosRatio ;
    String EthRatio ;
    public WarningRatio(){};

    public WarningRatio(String btcRatio, String bchRatio, String ltcRatio, String xrpRatio, String eosRatio, String ethRatio) {
        BtcRatio = btcRatio;
        BchRatio = bchRatio;
        LtcRatio = ltcRatio;
        XrpRatio = xrpRatio;
        EosRatio = eosRatio;
        EthRatio = ethRatio;
    }

    public String getBtcRatio() {
        return BtcRatio;
    }

    public void setBtcRatio(String btcRatio) {
        BtcRatio = btcRatio;
    }

    public String getBchRatio() {
        return BchRatio;
    }

    public void setBchRatio(String bchRatio) {
        BchRatio = bchRatio;
    }

    public String getLtcRatio() {
        return LtcRatio;
    }

    public void setLtcRatio(String ltcRatio) {
        LtcRatio = ltcRatio;
    }

    public String getXrpRatio() {
        return XrpRatio;
    }

    public void setXrpRatio(String xrpRatio) {
        XrpRatio = xrpRatio;
    }

    public String getEosRatio() {
        return EosRatio;
    }

    public void setEosRatio(String eosRatio) {
        EosRatio = eosRatio;
    }

    public String getEthRatio() {
        return EthRatio;
    }

    public void setEthRatio(String ethRatio) {
        EthRatio = ethRatio;
    }
}
