package com.happy.otc.util;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ETHCoinUtils {
    private static ETHCoinUtils instance;

    private final static String LOCK = "LOCK";
    public final String tokenSymbol;
    public final String contractAddress;
    public static final String ADDRESS_TOPIC_PREFIX = "0x000000000000000000000000";
    public static final BigDecimal BASE_UNIT = new BigDecimal("1000000000000000000");
    private static Map< String,ETHCoinUtils > utilsMap = new HashMap<>(  );
    private Web3j web3j;

    private static void init(String network, String infuraAccessToken, String tokenSymbol, String contractAddress) throws Throwable {
        instance = utilsMap.get(tokenSymbol);
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ETHCoinUtils(network,infuraAccessToken, tokenSymbol, contractAddress);
                    utilsMap.put(tokenSymbol,instance);
                }
            }
        }
    }

    public ETHCoinUtils(String network, String infuraAccessToken, String tokenSymbol, String contractAddress) throws Throwable {
        // 身份认证
        web3j = Web3j.build(new HttpService(network + infuraAccessToken));
        this.tokenSymbol = tokenSymbol;
        this.contractAddress = contractAddress;
    }


    public static ETHCoinUtils getInstance(String network, String infuraAccessToken, String tokenSymbol, String contractAddress) throws Throwable {
        init(network,infuraAccessToken, tokenSymbol, contractAddress);
        return instance;
    }

    public static String addressToTopic(String address) {

        return ADDRESS_TOPIC_PREFIX + address;
    }

    /**
     * 当前的最高高度
     * @return
     * @throws IOException
     */
    public BigInteger ethBlockNumber() throws IOException {
        return  web3j.ethBlockNumber().send().getBlockNumber();
    }

    /**
     * 获取当前地址的余额
     * @param address
     * @return
     * @throws IOException
     */
    public BigInteger ethGetBalance(String address) throws IOException {
        return  web3j.ethGetBalance(address,DefaultBlockParameterName.LATEST).send().getBalance();
    }

    /**
     * 获取每笔交易的燃料费
     * @return
     * @throws IOException
     */
    public BigInteger ethGasPrice() throws IOException {
        return  web3j.ethGasPrice().send().getGasPrice();
    }


    /**
     * 地址发送的事务数
     * @param address
     * @return
     * @throws IOException
     */
    public BigInteger ethGetTransactionCount(String address) throws IOException {
        return  web3j.ethGetTransactionCount(address,DefaultBlockParameterName.LATEST).send().getTransactionCount();
    }

    /**
     *  签名交易创建一个新的消息调用交易或合约。
     * @param transaction
     * @return
     * @throws IOException
     */
    public String ethSendRawTransaction(String transaction) throws IOException {
        return  web3j.ethSendRawTransaction(transaction).send().getTransactionHash();
    }

    /**
     * 指定地址的代码 如果不是合约地址就返回0x
     * @param address
     * @return
     * @throws IOException
     */
    public String ethGetCode(String address) throws IOException {
        return  web3j.ethGetCode(address, DefaultBlockParameterName.LATEST ).send().getCode();
    }
}
