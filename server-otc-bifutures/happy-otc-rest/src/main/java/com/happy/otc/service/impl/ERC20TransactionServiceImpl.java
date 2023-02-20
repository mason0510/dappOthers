package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.DateUtil;
import com.bitan.common.utils.HttpClientUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.io.BaseEncoding;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dto.ERC20TransactionDTO;
import com.happy.otc.dto.UserCurrencyAddressDTO;
import com.happy.otc.entity.Currency;
import com.happy.otc.entity.UserCurrencyAddress;
import com.happy.otc.service.ICurrencyService;
import com.happy.otc.service.IERC20TransactionService;
import com.happy.otc.service.IUserCurrencyAddressService;
import com.happy.otc.util.ETHCoinUtils;
import com.happy.otc.util.FileZipUtil;
import com.happy.otc.util.MoneyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class ERC20TransactionServiceImpl implements IERC20TransactionService {

    Logger logger = LoggerFactory.getLogger( ERC20TransactionServiceImpl.class);

    @Autowired
    private IUserCurrencyAddressService userCurrencyAddressService;
    @Autowired
    private ICurrencyService currencyService;

    @Value("${eth.network}")
    private String networkUrl;
//    private String networkUrl="https://mainnet.infura.io/";
    @Value("${infura.access.token}")
    private String infuraAccessToken;
    @Value("${eth.target.address.index}")
    private int targetAddressIndex;
    @Value("${eth.target.address}")
    private String targetAddress;
    @Value("${contract.address.balance}")
    private String contractAddressBalance;
    @Value("${eth.gas.limit}")
    private String gasLimit;
    @Value("${eth.erc20.gas.limit}")
    private String erc20GasLimit;


    @Override
    public String createTransactionFile() throws Throwable {


        List<Currency> list =  currencyService.getCurrencyList();
        Map<String,List<String>> map = new HashMap<>(  );
        List<String> rechargeTransaction = new ArrayList<>();
        String transaction = null;
        for (Currency v:list) {
            ETHCoinUtils ethCoinUtils = ETHCoinUtils.getInstance( networkUrl,infuraAccessToken,v.getCurrencySimpleName(),v.getContractAddress() );
            if (!v.getCurrencySimpleName().equals( "BTC" ) && !v.getCurrencySimpleName().equals( "USDT" )) {
                BigInteger gasPrice = ethCoinUtils.ethGasPrice();
                BigInteger fuelCost = gasPrice.multiply(new BigInteger( "0"));
                //todo 获取需要操作的地址列表，对只有统计当前时间前推3个月内有过充值记录的地址进行统计
                UserCurrencyAddress userCurrencyAddress = new UserCurrencyAddress();
                userCurrencyAddress.setCurrencyId( v.getCurrencyId() );
                userCurrencyAddress.setCreateTime( DateUtil.addMonth( new Date(), -3 ) );
                PageInfo<UserCurrencyAddressDTO> pageInfo = userCurrencyAddressService.queryCollectAddressList( userCurrencyAddress, 1, 2000 );
                List<UserCurrencyAddressDTO> ulist = pageInfo.getList();

                if (ulist.isEmpty()){
                    continue;
                }
                List<String> transactions = new ArrayList<>();
                for (UserCurrencyAddressDTO userCurrencyAddressDTO : ulist) {
                    String address = "0x" + userCurrencyAddressDTO.getAddress();
                    BigInteger balance = ethCoinUtils.ethGetBalance( address );

                    //用户没有足够的eth作为手续费
                    if (balance.compareTo(fuelCost) < 0) {
                        if (v.getCurrencyId() == 5005){
                            continue;
                        }
                        rechargeTransaction.add( buildETHGasTransaction(targetAddress,address,targetAddressIndex,fuelCost,ethCoinUtils));
                    }else {
                        transaction =  buildTransaction(v,address,targetAddress,userCurrencyAddressDTO.getUserAddressId().intValue(),fuelCost,ethCoinUtils);
                        if (!StringUtils.isEmpty(transaction)){
                            transactions.add(transaction);
                        }
                    }
                }

                if (transactions.size() > 0) {
                    map.put( v.getCurrencySimpleName(), transactions );
                }
            }
        }
        if (rechargeTransaction.size() > 0){
            map.put( "rechargeETH",rechargeTransaction );
        }

        //创建工作目录
        String workPath = System.getProperty( "user.dir" ) + File.separator + "work" + File.separator + System.currentTimeMillis();
        return createCollectZipFile( map, workPath);
    }

    /**
     * 建立交易内容
     * @param currency
     * @param from  发起地址
     * @param to   目标地址
     * @param index 发起地址序列
     * @param fuelCost 燃料费
     * @return
     * @throws IOException
     */
    public String buildTransaction(Currency currency, String from, String to, int index,BigInteger fuelCost,ETHCoinUtils ethCoinUtils) throws Throwable {

        String contractBalanceUrl = "";
        //创建未签名交易文件
        ERC20TransactionDTO erc20TransactionDTO = new ERC20TransactionDTO();
        Long nonce = ethCoinUtils.ethGetTransactionCount( from ).longValue();
        erc20TransactionDTO.setNonce( nonce );
        BigInteger gasPrice = ethCoinUtils.ethGasPrice();
        erc20TransactionDTO.setGasPrice(gasPrice.toString());
        erc20TransactionDTO.setAddress( to );
        erc20TransactionDTO.setName( currency.getCurrencySimpleName() );
        erc20TransactionDTO.setIndex(index);
        if ("ETH".equals( currency.getCurrencySimpleName() )) {
            BigInteger balance = ethCoinUtils.ethGetBalance( from );
            erc20TransactionDTO.setData( "0x" );//eth为0x 别的为"" 空
            erc20TransactionDTO.setContractAddress( "" );//如果未Eth为空.不然传当前币种的TOKEN_CONTRACT_ADDRESS
            erc20TransactionDTO.setGasLimit( new BigDecimal( gasLimit ) );//eth为21000 别的为66000
            erc20TransactionDTO.setValue(balance.subtract(fuelCost).toString());
        } else {
            erc20TransactionDTO.setData( "" );//eth为0x 别的为"" 空
            erc20TransactionDTO.setContractAddress( currency.getContractAddress() );//如果未Eth为空.不然传当前币种的TOKEN_CONTRACT_ADDRESS
            erc20TransactionDTO.setGasLimit(new BigDecimal( erc20GasLimit ));//eth为21000 别的为66000
            contractBalanceUrl = contractAddressBalance.replace( "{contractaddress}", currency.getContractAddress() ).replace( "{address}", from);
            Map<String, String> head = new HashMap<>();
            String resultJson = HttpClientUtil.getOne( contractBalanceUrl, String.class, head );
            JSONObject jsonObject = (JSONObject) JSONObject.parse( resultJson );
            erc20TransactionDTO.setValue(jsonObject.get("result").toString());//eth的值,需要除以10的18次方
        }
        if (erc20TransactionDTO.getValue().compareTo( "100000000000" ) < 1){
            return null;
        }
        return  JSONObject.toJSON( erc20TransactionDTO ).toString();
    }


    /**
     * 建立补充ETH的燃料费交易内容
     * @param from  发起地址
     * @param to   目标地址
     * @param index 发起地址序列
     * @param fuelCost 燃料费
     * @return
     * @throws IOException
     */
    public String buildETHGasTransaction(String from, String to, int index,BigInteger fuelCost,ETHCoinUtils ethCoinUtils) throws IOException {

        //创建未签名交易文件
        ERC20TransactionDTO erc20TransactionDTO = new ERC20TransactionDTO();
        BigInteger balance = ethCoinUtils.ethGetBalance( from );
        BigInteger gasPrice = ethCoinUtils.ethGasPrice();
        Long  ETHGas =  fuelCost.longValue() * 2;
        //补充燃料费，防止不够21000*1.1的倍数
        erc20TransactionDTO.setValue(fuelCost.toString());
        if (balance.compareTo(BigInteger.valueOf(ETHGas)) < 0){
            BizException.fail( ApiResponseCode.PARA_OUT_RANGE,index+"号地址的ETH余额不够 地址为"+ from );
        }
        Long nonce = ethCoinUtils.ethGetTransactionCount( from ).longValue();
        erc20TransactionDTO.setNonce( nonce );
        erc20TransactionDTO.setGasPrice(gasPrice.toString());
        erc20TransactionDTO.setAddress( to );
        erc20TransactionDTO.setName("ETH");
        erc20TransactionDTO.setIndex(index);
        erc20TransactionDTO.setData( "0x" );//eth为0x 别的为"" 空
        erc20TransactionDTO.setContractAddress( "" );//如果未Eth为空.不然传当前币种的TOKEN_CONTRACT_ADDRESS
        erc20TransactionDTO.setGasLimit(new BigDecimal( gasLimit ));//eth为21000 别的为66000

        return  JSONObject.toJSON( erc20TransactionDTO ).toString();
    }

    @Override
    public String ethSendRawTransaction(String data, Currency currency) throws Throwable {

        ETHCoinUtils ethCoinUtils = ETHCoinUtils.getInstance( networkUrl,infuraAccessToken,currency.getCurrencySimpleName(),currency.getContractAddress() );
        return  ethCoinUtils.ethSendRawTransaction(data);
    }


    @Override
    public String createWithdrawTransactionFile(String withdrawAddress, Double ethAmount, Currency currency) throws Throwable {

        ETHCoinUtils ethCoinUtils = ETHCoinUtils.getInstance( networkUrl,infuraAccessToken,currency.getCurrencySimpleName(),currency.getContractAddress() );
        withdrawAddress = "0x" + withdrawAddress;

        if(StringUtils.isEmpty(ethCoinUtils.ethGetCode(withdrawAddress))){
            BizException.fail( ApiResponseCode.PARA_ERR,"提款地址错误");
        }

        if(ethAmount.compareTo( 0.01D ) < 0){
            BizException.fail( ApiResponseCode.PARA_ERR,"提款数量错误");
        }

        //创建未签名交易文件
        ERC20TransactionDTO erc20TransactionDTO = new ERC20TransactionDTO();
        BigInteger gasPrice = ethCoinUtils.ethGasPrice();

        Long nonce = ethCoinUtils.ethGetTransactionCount(targetAddress).longValue();
        erc20TransactionDTO.setGasPrice(gasPrice.toString());
        erc20TransactionDTO.setAddress(withdrawAddress);
        erc20TransactionDTO.setNonce(nonce);

        BigDecimal amount = BigDecimal.valueOf(ethAmount);
        erc20TransactionDTO.setValue(ETHCoinUtils.BASE_UNIT.multiply(amount).toBigInteger().toString());//eth的值,需要除以10的18次方
        if ("ETH".equals(currency.getCurrencySimpleName())){
            erc20TransactionDTO.setData("0x");//eth为0x 别的为"" 空
            erc20TransactionDTO.setContractAddress( "" );//如果未Eth为空.不然传当前币种的TOKEN_CONTRACT_ADDRESS
            erc20TransactionDTO.setGasLimit(new BigDecimal(gasLimit));//eth为21000 别的为66000
        }else {
            erc20TransactionDTO.setData("");//eth为0x 别的为"" 空
            erc20TransactionDTO.setContractAddress( currency.getContractAddress());//如果未Eth为空.不然传当前币种的TOKEN_CONTRACT_ADDRESS
            erc20TransactionDTO.setGasLimit(new BigDecimal(erc20GasLimit));//eth为21000 别的为66000
        }

        erc20TransactionDTO.setName(currency.getCurrencySimpleName());
        erc20TransactionDTO.setIndex( targetAddressIndex );

        //创建工作目录
        String workPath = System.getProperty("user.dir") + File.separator + "work" + File.separator + System.currentTimeMillis();

        List<String> transactions = new ArrayList<>(  );
        transactions.add( JSONObject.toJSON(erc20TransactionDTO).toString());

        return  createZipFile(transactions, workPath, currency);
    }

    /**
     * addressUtils,btcCoinUtils方法的实现
     * @throws Throwable
     */
//    private void  initUtils() throws Throwable {
//        init(network,infuraAccessToken, tokenSymbol, contractAddress);
//        ethCoinUtils = ETHCoinUtils.getInstance( networkUrl,infuraAccessToken,"token1","" );
//    }


    /**
     * 生成聚集ERC的压缩文件
     * @param map 币种交易记录
     * @param workPath 文件保存地址
     * @return
     * @throws Throwable
     */
    private String  createCollectZipFile(Map<String,List<String>> map, String workPath) throws Throwable {

        File workFolder = new File(workPath);
        if (!workFolder.exists() && !workFolder.isDirectory()) {
            workFolder.mkdirs();
        }
        for (Map.Entry<String,List<String>> entry: map.entrySet()){
            //创建文件
            String fileName = entry.getKey()+ "_collect_"+ System.currentTimeMillis() + ".txt";
            File file = new File(workPath + File.separator + fileName);
            FileWriter writer = null;
            try {
                file.createNewFile();

                writer = new FileWriter(file);
                //根据订单，生成需要签名文件
                for (String item : entry.getValue()) {
                    writer.write( item );
                    writer.write( "\r\n");
                }
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
        String zipName = "ERC20_collect_" + System.currentTimeMillis() + ".zip";
        try {
            FileZipUtil.zip(workPath, zipPath, zipName);
        } catch (Exception e) {
            e.printStackTrace();
            BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "生成未处理聚集交易失败");
        }

        //删除未签名文件
        FileZipUtil.deleteDir(workFolder);

        return zipPath + File.separator + zipName;
    }

    @Override
    public String createTxhashFile(List<String> list, String workPath,String fileName) {

        File outfile = new File(workPath + File.separator + fileName);
        FileWriter writer = null;
        try {
            outfile.createNewFile();

            writer = new FileWriter(outfile);
            //根据订单，生成需要签名文件
            for (String item: list) {
                writer.write( item );
                writer.write( "\r\n");
            }
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
            BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "生成交易编码失败");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return  workPath + File.separator + fileName;
    }
    /**
     * 根据交易记录生成未签名交易的zip包
     * @param transactions 交易记录
     * @param workPath 文件保存地址
     * @return
     * @throws Throwable
     */
    private String  createZipFile(List<String> transactions, String workPath, Currency currency) throws Throwable {

        File workFolder = new File(workPath);
        if (!workFolder.exists() && !workFolder.isDirectory()) {
            workFolder.mkdirs();
        }

        //根据订单，生成需要签名文件
        for (String item : transactions) {
            //创建文件
            String fileName = currency.getCurrencySimpleName()+ "_"+ System.currentTimeMillis() + ".txt";
            File file = new File(workPath + File.separator + fileName);
            FileWriter writer = null;
            try {
                file.createNewFile();

                writer = new FileWriter(file);
                writer.write(item);
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
        String zipName = currency.getCurrencySimpleName() + "_" + System.currentTimeMillis() + ".zip";
        try {
            FileZipUtil.zip(workPath, zipPath, zipName);
        } catch (Exception e) {
            e.printStackTrace();
            BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "生成未处理交易失败");
        }

        //删除未签名文件
        FileZipUtil.deleteDir(workFolder);

        return zipPath + File.separator + zipName;
    }
}
