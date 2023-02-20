package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSON;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.google.common.io.BaseEncoding;
import com.happy.otc.contants.Contants;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.entity.Currency;
import com.happy.otc.service.ICurrencyService;
import com.happy.otc.service.IOmniTransactionService;
import com.happy.otc.service.IUserCurrencyAddressService;
import com.happy.otc.util.AddressUtils;
import com.happy.otc.util.BTCCoinUtils;
import com.happy.otc.util.FileZipUtil;
import com.happy.otc.vo.omni.OmniBalance;
import com.happy.otc.vo.omni.Utxo;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class OmniTransactionServiceImpl implements IOmniTransactionService {

    Logger logger = LoggerFactory.getLogger(OmniTransactionServiceImpl.class);

    private AddressUtils addressUtils;
    private BTCCoinUtils btcCoinUtils;
    private final DecimalFormat format;

    @Autowired
    private IUserCurrencyAddressService userCurrencyAddressService;
    @Autowired
    private ICurrencyService currencyService;

    @Value("${btc.line.mode}")
    private String btcMode;
    @Value("${usdt.target.address}")
    private String targetAddress;
    @Value("${usdt.fund.address}")
    private String fundAddress;
    @Value("${btc.line.public.key}")
    private String publicKey;
    @Value("${rpcUser}")
    private String rpcUser;
    @Value("${rpcPwd}")
    private String rpcPwd;
    @Value("${rpcConnect}")
    private String rpcConnect;
    @Value("${rpcPort}")
    private String rpcPort;
    @Value("${usdt.id}")
    private Integer USDT_ID;


    private static final int OMNI_TRANSACTION_SIZE = 300;
    private static final double OMNI_REFERENCE_AMOUNT = 0.00000546;
    private static final String PARTIAL_TRANSACTON_PREFIX = "45505446ff00";
    private static final BaseEncoding BASE16 = BaseEncoding.base16().lowerCase();
    private static final byte[] NO_SIG_PREFIX = BASE16.decode("01ff");

    public OmniTransactionServiceImpl() {

        format = new DecimalFormat("#.########");
        format.setRoundingMode(RoundingMode.FLOOR);
    }

    public List<OmniBalance> getBalance() throws Throwable{
        return OmniBalance.fromList(this.btcCoinUtils.omniGetWalletAddressBalances(), USDT_ID);
    }

    public OmniBalance getBalance(String address,int propertyid) throws Throwable{
        return OmniBalance.getBalance(this.btcCoinUtils.omniGetbalance(address,propertyid),address);
    }

    public Map<String, List<Utxo>> listUnspent(List<String> addresses, int minConf) throws Throwable{
        return Utxo.fromList(btcCoinUtils.listunspent(addresses, minConf));
    }

    public double estimateFee(double size) throws Throwable {
        return btcCoinUtils.estimateFee() * size / 1000;
    }

    /**
     * 产生一个二进制的交易
     * @param inputs 该交易的输入
     * @param omniAmount 需要转出的omni数量
     * @param toAddress 目标地址
     * @param changeAddress 找零地址
     * @param fee 缴纳的矿工费
     * @return 如果成功则返回交易字符串，否则为空
     */
    private String makeRawTransaction(List<Utxo> inputs, String omniAmount, String toAddress, String changeAddress, double fee) {
        try {
            String rawTx = btcCoinUtils.createRawTransaction(inputs);
            String payload = btcCoinUtils.createSimpleSendPayload(USDT_ID, omniAmount);
            rawTx = btcCoinUtils.createOpReturnOutput(rawTx, payload);
            rawTx = btcCoinUtils.createReferenceOutput(rawTx, toAddress);
            rawTx = btcCoinUtils.createChangeOutput(rawTx, inputs, changeAddress, format.format(fee));
            Transaction transaction = null;
            if ("test".equals(btcMode)) {
                 transaction = new Transaction(
                        NetworkParameters.fromID(NetworkParameters.ID_TESTNET),
                        BASE16.decode(rawTx)  );
            } else {
                transaction = new Transaction(
                        NetworkParameters.fromID(NetworkParameters.ID_MAINNET),
                        BASE16.decode(rawTx)  );
            }



            return serializeTransaction(transaction, inputs);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 根据目标生成一笔交易清空目标地址里的omni
     * @param balance 该地址的omni数量
     * @param output 该地址可用的交易
     * @param funds 其他地址提供的可用输入，用以解决本地址内金额不足的情况
     * @param fee 缴纳的矿工费用
     * @param toAddress 目标地址
     * @param changeAddress 找零地址
     * @return 如果成功返回交易字符串，否则返回空
     */
    private String generateTransaction(OmniBalance balance,
                                      Utxo output,
                                      List<Utxo> funds,
                                      double fee,
                                      String toAddress,
                                      String changeAddress) {
        if (output == null) {
            return null;
        }

        ArrayList<Utxo> input = new ArrayList<>();
        input.add(output);
        if (output.amount < OMNI_REFERENCE_AMOUNT + fee) {
            if (!funds.isEmpty()) {
                input.add(funds.remove(funds.size() - 1));
            } else {
                return null;
            }
        }

        return makeRawTransaction(input, balance.balance, toAddress, changeAddress, fee);
    }

    /**
     * 通过地址得到交易的Script
     * @param address 目标地址
     * @return 该地址对应的Script
     */
    private byte[] addressToXpubKey(String address) {
        //根据地址得到改地址的需要编号
        Currency currency = currencyService.getCurrency(Contants.SCAN_USDT);
        Long addressId = userCurrencyAddressService.getAddressIdByAddress(address, currency);

        return addressUtils.getChildXPubKey(addressId.intValue());
    }

    @Override
    public List<String> sweepWallets(String targetAddress, String fundAddress) {
        try {
            List<OmniBalance> missingInput = new ArrayList<>();
            List<String> result = new ArrayList<>();

            List<OmniBalance> balances = getBalance();
            //todo 没有地址拥有余额
            if (CollectionUtils.isEmpty(balances)){
                return Collections.emptyList();
            }

            List<String> addresses = new ArrayList<>();
            addresses.add(fundAddress);
            balances.forEach(balance -> addresses.add(balance.address));
            //未可用的交易
            Map<String, List<Utxo>> transactions = listUnspent(addresses, 6);

            //获取矿工费
            double fee = estimateFee(OMNI_TRANSACTION_SIZE);

            //添加提供矿工费的交易
            ArrayList<Utxo> funds = new ArrayList<>();
            List<Utxo> fundInputs = transactions.get(fundAddress);
            if (fundInputs != null) {
                for (Utxo input: fundInputs) {
                    if (input.amount >= fee) {
                        funds.add(input);
                    }
                }
            }

            //将所有USDT余额转入目标地址
            for (OmniBalance balance: balances) {
                //排除目标地址
                if (balance.address.equals(targetAddress)) {
                    continue;
                }
                //获取BTC金额大于最小交易额的交易
                Utxo output = findSmallestSuitableOutput(transactions.get(balance.address), OMNI_REFERENCE_AMOUNT);
                //获取未签名交易
                String rawTx = generateTransaction(balance, output, funds, fee, targetAddress, fundAddress);

                if (rawTx == null) {
                    missingInput.add(balance);
                } else {
                    result.add(rawTx);
                }
            }
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> withdrawWallet(String withdrawAddress, String amount,String targetAddress, String fundAddress) {
        try {
            List<OmniBalance> missingInput = new ArrayList<>();
            List<String> result = new ArrayList<>();

            OmniBalance balance = getBalance(targetAddress,USDT_ID);
            //todo 余额不够
            if (new BigDecimal(amount).compareTo(new BigDecimal(balance.balance)) > 0){
                logger.error( withdrawAddress+"需要提币："+ amount +"  ，目标地址的余额不够" );
                return Collections.emptyList();
            }

            List<String> addresses = new ArrayList<>();
            addresses.add(fundAddress);
            addresses.add(balance.address);
            //未可用的交易
            Map<String, List<Utxo>> transactions = listUnspent(addresses, 6);

            //获取矿工费
            double fee = estimateFee(OMNI_TRANSACTION_SIZE);

            //添加提供矿工费的交易
            ArrayList<Utxo> funds = new ArrayList<>();
            List<Utxo> fundInputs = transactions.get(fundAddress);
            if (fundInputs != null) {
                for (Utxo input: fundInputs) {
                    if (input.amount >= fee) {
                        funds.add(input);
                    }
                }
            }

            //将需要USDT数量转入提款地址

            //排除目标地址
            if (balance.address.equals(withdrawAddress)) {
                BizException.fail( MessageCode.TARGETADDRESS_ERR,"不能提款到目标地址");
            }
            //获取BTC金额大于最小交易额的交易
            Utxo output = findSmallestSuitableOutput(transactions.get(balance.address), OMNI_REFERENCE_AMOUNT);

            //对于要提取的余额处理
            balance = new OmniBalance(balance.address, amount);
            //获取未签名交易
            String rawTx = generateTransaction(balance, output, funds, fee, withdrawAddress, fundAddress);

            if (rawTx == null) {
                missingInput.add(balance);
            } else {
                result.add(rawTx);
            }

            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    /**
     * 将交易转为Electurm可以理解并签发的Hex格式
     * @param transaction 需要转化的交易
     * @param utxos 该交易对应的输入
     * @return
     * @throws IOException
     */
    private String serializeTransaction(Transaction transaction, List<Utxo> utxos) throws IOException {
        List<TransactionInput> txInputs = transaction.getInputs();
        for (int i=0; i<utxos.size(); i++) {
            TransactionInput txInput = txInputs.get(i);
            Utxo utxo = utxos.get(i);

            byte[] xpubKey = makeScript(addressToXpubKey(utxo.address));
            txInput.setScriptSig(new Script(
                    xpubKey
            ));
        }

        return PARTIAL_TRANSACTON_PREFIX + BASE16.encode(transaction.bitcoinSerialize());
    }

    private byte[] makeScript(byte[] xpubKey) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ScriptBuilder builder = new ScriptBuilder();
            builder.data(xpubKey);
            byte[] script = builder.build().getProgram();

            output.write(NO_SIG_PREFIX);
            output.write(script);

            byte[] bytes = output.toByteArray();
            output.close();
            return bytes;
        }
    }

    public Utxo findSmallestSuitableOutput(List<Utxo> utxos, double amount) {
        if (utxos != null) {
            utxos.sort((a, b) -> (int) Math.signum(a.amount - b.amount));
            for (Utxo utxo: utxos) {
                if (utxo.amount >= amount) {
                    return utxo;
                }
            }
        }
        return null;
    }

    @Override
    public String createTransactionFile() throws Throwable {

        initUtils();
        //生成未签名订单
        List<String> transactions = sweepWallets(targetAddress, fundAddress);
        if (transactions.size() == 0) {
            BizException.fail(MessageCode.NO_USDT_TRANSACTION, "暂无未处理交易");
        }

        //创建工作目录
        String workPath = System.getProperty("user.dir") + File.separator + "work" + File.separator + System.currentTimeMillis();

        return createZipFile(transactions,workPath);
    }

    @Override
    public String createWithdrawTransactionFile(String withdrawAddress, String amount) throws Throwable {

        initUtils();
        //生成未签名订单
        List<String> transactions = withdrawWallet(withdrawAddress, amount, targetAddress,fundAddress);
        if (transactions.size() == 0) {
            BizException.fail(MessageCode.NO_USDT_TRANSACTION, "暂无未处理交易");
        }
        if ("test".equals(btcMode)) {
           if(!AddressUtils.checkAddress(withdrawAddress, TestNet3Params.get())){
               BizException.fail( ApiResponseCode.PARA_ERR,"提款地址错误");
           }
        } else {
            if(!AddressUtils.checkAddress(withdrawAddress, MainNetParams.get())){
                BizException.fail( ApiResponseCode.PARA_ERR,"提款地址错误");
            }
        }
        //创建工作目录
        String workPath = System.getProperty("user.dir") + File.separator + "work" + File.separator + System.currentTimeMillis();

        return createZipFile(transactions,workPath);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * addressUtils,btcCoinUtils方法的实现
     * @throws Throwable
     */
    private void  initUtils() throws Throwable {

        if ("test".equals(btcMode)) {
            addressUtils = AddressUtils.getInstance(publicKey, TestNet3Params.get());
            USDT_ID = 1;
        } else {
            addressUtils = AddressUtils.getInstance(publicKey, MainNetParams.get());
            USDT_ID = 31;
        }
        btcCoinUtils = BTCCoinUtils.getInstance(rpcUser, rpcPwd, rpcConnect, rpcPort);
    }

    /**
     * 根据交易记录生成未签名交易的zip包
     * @param transactions 交易记录
     * @param workPath 文件保存地址
     * @return
     * @throws Throwable
     */
    private String  createZipFile(List<String> transactions, String workPath) throws Throwable {

        File workFolder = new File(workPath);
        if (!workFolder.exists() && !workFolder.isDirectory()) {
            workFolder.mkdirs();
        }

        //根据订单，生成Electrum可用文件
        for (String item : transactions) {
            Map<String, Object> transactionMap = new HashMap<>();
            transactionMap.put("hex", item);
            transactionMap.put("complete", false);
            transactionMap.put("final", false);
            String content = JSON.toJSONString(transactionMap);

            //创建文件
            String fileName = "usdt_" + System.currentTimeMillis() + "_" + item.substring(0, 4) + ".txn";
            File file = new File(workPath + File.separator + fileName);
            FileWriter writer = null;
            try {
                file.createNewFile();

                writer = new FileWriter(file);
                writer.write(content);
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
                BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "生成未处理交易失败");
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //打包文件
        String zipPath = System.getProperty("user.dir") + File.separator + "zip";
        String zipName = "usdt_" + System.currentTimeMillis() + ".zip";
        try {
            FileZipUtil.zip(workPath, zipPath, zipName);
        } catch (Exception e) {
            e.printStackTrace();
            BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "生成未处理交易失败");
        }

        //删除未签名文件
        deleteDir(workFolder);

        return zipPath + File.separator + zipName;
    }
}
