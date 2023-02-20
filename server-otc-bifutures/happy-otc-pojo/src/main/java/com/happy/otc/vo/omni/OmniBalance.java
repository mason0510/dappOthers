package com.happy.otc.vo.omni;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OmniBalance {
    public final String address;
    public final String balance;

    public OmniBalance(String address, String balance) {
        this.address = address;
        this.balance = balance;
    }

    public static String getBalance(List<Map<String, Object>> balances, int targetPropertyId) {
        for (Map<String, Object> balance: balances) {
            int propertyId = (int) balance.get("propertyid");
            if (propertyId == targetPropertyId) {
                return (String) balance.get("balance");
            }
        }
        return null;
    }

    public static OmniBalance getBalance(Map<String, Object> balance,String address) {

        return new OmniBalance(address,(String)balance.get("balance"));
    }

    public static List<OmniBalance> fromList(List<Map<String, Object>> map, int targetPropertyId) {
        List<OmniBalance> result = new ArrayList<>();
        map.forEach(item -> {
            String balance = getBalance((List<Map<String, Object>>) item.get("balances"), targetPropertyId);
            if (balance != null) {
                result.add(new OmniBalance(
                        (String) item.get("address"),
                        balance
                ));
            }
        });

        return result;
    }
}
