package com.happy.otc.vo.omni;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utxo {
    public final String txid;
    public final int vout;
    public final String address;
    public final String scriptPubKey;
    public final double amount;
    private String xpubKey;

    public Utxo(String txid,
                int vout,
                String address,
                String scriptPubKey,
                double amount) {
        this.txid = txid;
        this.vout = vout;
        this.address = address;
        this.scriptPubKey = scriptPubKey;
        this.amount = amount;
    }

    public static Map<String, List<Utxo>> fromList(List<Map<String, Object>> items) {
        Map<String, List<Utxo>> result = new HashMap<>();
        items.forEach(item -> {
            String address = (String) item.get("address");
            List<Utxo> utxos = result.computeIfAbsent(address, key -> new ArrayList<>());
            utxos.add(new Utxo(
                    (String) item.get("txid"),
                    (int) item.get("vout"),
                    address,
                    (String) item.get("scriptPubKey"),
                    (Double) item.get("amount")
            ));
        });
        return result;
    }

    public void setXpubKey(String xpubKey) {
        this.xpubKey = xpubKey;
    }

    public String getXpubKey(){
        return xpubKey;
    }

    public Map<String, Object> toMap() {
        return ImmutableMap.of(
                "txid", txid,
                "vout", vout,
                "scriptPubKey", scriptPubKey,
                "value", String.valueOf(amount)
        );
    }
}
