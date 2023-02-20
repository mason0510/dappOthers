package com.happy.otc.util;

import com.googlecode.jsonrpc4j.Base64;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.happy.otc.vo.omni.Utxo;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BTCCoinUtils {
    private static BTCCoinUtils instance;

    private final static String LOCK = "LOCK";

    private static void init(String rpcUser, String rpcPwd, String rpcAllowIp, String rpcPort) throws Throwable {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new BTCCoinUtils(rpcUser, rpcPwd, rpcAllowIp, rpcPort);
                }
            }
        }
    }

    private JsonRpcHttpClient client;

    public BTCCoinUtils(String rpcUser, String rpcPwd, String rpcAllowIp, String rpcPort) throws Throwable {
        // 身份认证
        String cred = Base64.encodeBytes((rpcUser + ":" + rpcPwd).getBytes());
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", "Basic " + cred);
        client = new JsonRpcHttpClient(new URL("http://" + rpcAllowIp + ":" + rpcPort), headers);
    }


    public static BTCCoinUtils getInstance(String rpcUser, String rpcPwd, String rpcAllowIp, String rpcPort) throws Throwable {
        init(rpcUser, rpcPwd, rpcAllowIp, rpcPort);
        return instance;
    }


    /**
     * 查询账户下的交易记录
     *
     * @param account
     * @param count
     * @param offset
     * @return
     * @throws Throwable
     */
    public List listtransactions(String account, int count, int offset, boolean includeWatchonly) throws Throwable {
        return client.invoke("listtransactions", new Object[]{account, count, offset, includeWatchonly}, List.class);
    }

    /**
     * 查询账户下的omni交易记录
     *
     * @param txid
     * @param count
     * @param offset
     * @param startblock
     * @param endblock
     * @return
     * @throws Throwable
     */
    public List omni_listtransactions(String txid, int count, int offset, int startblock, int endblock) throws Throwable {
        return client.invoke("omni_listtransactions", new Object[]{txid, count, offset, startblock, endblock}, List.class);
    }

    /**
     * 把地址归于钱包
     *
     * @param account
     * @param label
     * @param rescan
     * @param p2sh
     * @return
     * @throws Throwable
     */
    public String importaddress(String account, String label, boolean rescan,boolean p2sh) throws Throwable {
        return client.invoke("importaddress", new Object[]{account, label, rescan, p2sh}, String.class);
    }

    /**
     * 钱包地址列表
     *
     * @param account
     * @return
     * @throws Throwable
     */
    public List getaddressesbyaccount (String account) throws Throwable {
        return client.invoke("getaddressesbyaccount", new Object[]{account}, List.class);
    }

    public Map<String, Object> getTransaction(String txid, int vout) throws Throwable {
        return client.invoke("gettxout", new Object[]{txid, vout}, Map.class);
    }

    public List<Map<String, Object>> omniGetWalletAddressBalances() throws Throwable {
        return (List<Map<String, Object>> ) client.invoke("omni_getwalletaddressbalances", new Object[]{true}, List.class);
    }

    public Map<String, Object> omniGetbalance(String address,int propertyid) throws Throwable {
        return (Map<String, Object>) client.invoke("omni_getbalance", new Object[]{address,propertyid}, Map.class);
    }

    public List<Map<String, Object>> listunspent(List<String> addresses, int minConf) throws Throwable {
        return (List<Map<String, Object>> ) client.invoke("listunspent",
                new Object[]{minConf, 99999999, addresses}, List.class
        );
    }

    public double estimateFee() throws Throwable {
        return (Double) ((Map<String, Object>) client.invoke("estimatesmartfee",
                new Object[]{10}, Map.class)).get("feerate");
    }

    private List<? extends Map<String, Object>> convertInputs(List<Utxo> inputs) {
        return inputs.stream().map(Utxo::toMap).collect(Collectors.toList());
    }

    public String createRawTransaction(List<Utxo> inputs) throws Throwable {
        return client.invoke("createrawtransaction",
                new Object[]{convertInputs(inputs), Collections.emptyMap()}, String.class
        );
    }

    public String createSimpleSendPayload(int propertyId, String amount) throws Throwable {
        return client.invoke("omni_createpayload_simplesend",
                new Object[]{propertyId, amount}, String.class
        );
    }

    public String createOpReturnOutput(String rawTx, String payload) throws Throwable {
        return client.invoke("omni_createrawtx_opreturn",
                new Object[]{rawTx, payload}, String.class
        );
    }

    public String createReferenceOutput(String rawTx, String toAddress) throws Throwable {
        return client.invoke("omni_createrawtx_reference",
                new Object[]{rawTx, toAddress}, String.class
        );
    }

    public String createChangeOutput(String rawTx, List<Utxo> inputs, String address, String fee) throws Throwable {
        return client.invoke("omni_createrawtx_change",
                new Object[]{rawTx, convertInputs(inputs), address, fee}, String.class
        );
    }
}
