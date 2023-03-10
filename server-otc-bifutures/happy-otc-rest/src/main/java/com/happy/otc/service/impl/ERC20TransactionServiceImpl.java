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
                //todo ?????????????????????????????????????????????????????????????????????3????????????????????????????????????????????????
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

                    //?????????????????????eth???????????????
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

        //??????????????????
        String workPath = System.getProperty( "user.dir" ) + File.separator + "work" + File.separator + System.currentTimeMillis();
        return createCollectZipFile( map, workPath);
    }

    /**
     * ??????????????????
     * @param currency
     * @param from  ????????????
     * @param to   ????????????
     * @param index ??????????????????
     * @param fuelCost ?????????
     * @return
     * @throws IOException
     */
    public String buildTransaction(Currency currency, String from, String to, int index,BigInteger fuelCost,ETHCoinUtils ethCoinUtils) throws Throwable {

        String contractBalanceUrl = "";
        //???????????????????????????
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
            erc20TransactionDTO.setData( "0x" );//eth???0x ?????????"" ???
            erc20TransactionDTO.setContractAddress( "" );//?????????Eth??????.????????????????????????TOKEN_CONTRACT_ADDRESS
            erc20TransactionDTO.setGasLimit( new BigDecimal( gasLimit ) );//eth???21000 ?????????66000
            erc20TransactionDTO.setValue(balance.subtract(fuelCost).toString());
        } else {
            erc20TransactionDTO.setData( "" );//eth???0x ?????????"" ???
            erc20TransactionDTO.setContractAddress( currency.getContractAddress() );//?????????Eth??????.????????????????????????TOKEN_CONTRACT_ADDRESS
            erc20TransactionDTO.setGasLimit(new BigDecimal( erc20GasLimit ));//eth???21000 ?????????66000
            contractBalanceUrl = contractAddressBalance.replace( "{contractaddress}", currency.getContractAddress() ).replace( "{address}", from);
            Map<String, String> head = new HashMap<>();
            String resultJson = HttpClientUtil.getOne( contractBalanceUrl, String.class, head );
            JSONObject jsonObject = (JSONObject) JSONObject.parse( resultJson );
            erc20TransactionDTO.setValue(jsonObject.get("result").toString());//eth??????,????????????10???18??????
        }
        if (erc20TransactionDTO.getValue().compareTo( "100000000000" ) < 1){
            return null;
        }
        return  JSONObject.toJSON( erc20TransactionDTO ).toString();
    }


    /**
     * ????????????ETH????????????????????????
     * @param from  ????????????
     * @param to   ????????????
     * @param index ??????????????????
     * @param fuelCost ?????????
     * @return
     * @throws IOException
     */
    public String buildETHGasTransaction(String from, String to, int index,BigInteger fuelCost,ETHCoinUtils ethCoinUtils) throws IOException {

        //???????????????????????????
        ERC20TransactionDTO erc20TransactionDTO = new ERC20TransactionDTO();
        BigInteger balance = ethCoinUtils.ethGetBalance( from );
        BigInteger gasPrice = ethCoinUtils.ethGasPrice();
        Long  ETHGas =  fuelCost.longValue() * 2;
        //??????????????????????????????21000*1.1?????????
        erc20TransactionDTO.setValue(fuelCost.toString());
        if (balance.compareTo(BigInteger.valueOf(ETHGas)) < 0){
            BizException.fail( ApiResponseCode.PARA_OUT_RANGE,index+"????????????ETH???????????? ?????????"+ from );
        }
        Long nonce = ethCoinUtils.ethGetTransactionCount( from ).longValue();
        erc20TransactionDTO.setNonce( nonce );
        erc20TransactionDTO.setGasPrice(gasPrice.toString());
        erc20TransactionDTO.setAddress( to );
        erc20TransactionDTO.setName("ETH");
        erc20TransactionDTO.setIndex(index);
        erc20TransactionDTO.setData( "0x" );//eth???0x ?????????"" ???
        erc20TransactionDTO.setContractAddress( "" );//?????????Eth??????.????????????????????????TOKEN_CONTRACT_ADDRESS
        erc20TransactionDTO.setGasLimit(new BigDecimal( gasLimit ));//eth???21000 ?????????66000

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
            BizException.fail( ApiResponseCode.PARA_ERR,"??????????????????");
        }

        if(ethAmount.compareTo( 0.01D ) < 0){
            BizException.fail( ApiResponseCode.PARA_ERR,"??????????????????");
        }

        //???????????????????????????
        ERC20TransactionDTO erc20TransactionDTO = new ERC20TransactionDTO();
        BigInteger gasPrice = ethCoinUtils.ethGasPrice();

        Long nonce = ethCoinUtils.ethGetTransactionCount(targetAddress).longValue();
        erc20TransactionDTO.setGasPrice(gasPrice.toString());
        erc20TransactionDTO.setAddress(withdrawAddress);
        erc20TransactionDTO.setNonce(nonce);

        BigDecimal amount = BigDecimal.valueOf(ethAmount);
        erc20TransactionDTO.setValue(ETHCoinUtils.BASE_UNIT.multiply(amount).toBigInteger().toString());//eth??????,????????????10???18??????
        if ("ETH".equals(currency.getCurrencySimpleName())){
            erc20TransactionDTO.setData("0x");//eth???0x ?????????"" ???
            erc20TransactionDTO.setContractAddress( "" );//?????????Eth??????.????????????????????????TOKEN_CONTRACT_ADDRESS
            erc20TransactionDTO.setGasLimit(new BigDecimal(gasLimit));//eth???21000 ?????????66000
        }else {
            erc20TransactionDTO.setData("");//eth???0x ?????????"" ???
            erc20TransactionDTO.setContractAddress( currency.getContractAddress());//?????????Eth??????.????????????????????????TOKEN_CONTRACT_ADDRESS
            erc20TransactionDTO.setGasLimit(new BigDecimal(erc20GasLimit));//eth???21000 ?????????66000
        }

        erc20TransactionDTO.setName(currency.getCurrencySimpleName());
        erc20TransactionDTO.setIndex( targetAddressIndex );

        //??????????????????
        String workPath = System.getProperty("user.dir") + File.separator + "work" + File.separator + System.currentTimeMillis();

        List<String> transactions = new ArrayList<>(  );
        transactions.add( JSONObject.toJSON(erc20TransactionDTO).toString());

        return  createZipFile(transactions, workPath, currency);
    }

    /**
     * addressUtils,btcCoinUtils???????????????
     * @throws Throwable
     */
//    private void  initUtils() throws Throwable {
//        init(network,infuraAccessToken, tokenSymbol, contractAddress);
//        ethCoinUtils = ETHCoinUtils.getInstance( networkUrl,infuraAccessToken,"token1","" );
//    }


    /**
     * ????????????ERC???????????????
     * @param map ??????????????????
     * @param workPath ??????????????????
     * @return
     * @throws Throwable
     */
    private String  createCollectZipFile(Map<String,List<String>> map, String workPath) throws Throwable {

        File workFolder = new File(workPath);
        if (!workFolder.exists() && !workFolder.isDirectory()) {
            workFolder.mkdirs();
        }
        for (Map.Entry<String,List<String>> entry: map.entrySet()){
            //????????????
            String fileName = entry.getKey()+ "_collect_"+ System.currentTimeMillis() + ".txt";
            File file = new File(workPath + File.separator + fileName);
            FileWriter writer = null;
            try {
                file.createNewFile();

                writer = new FileWriter(file);
                //???????????????????????????????????????
                for (String item : entry.getValue()) {
                    writer.write( item );
                    writer.write( "\r\n");
                }
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
                BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "???????????????????????????");
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


        //????????????
        String zipPath = System.getProperty("user.dir") + File.separator + "zip";
        String zipName = "ERC20_collect_" + System.currentTimeMillis() + ".zip";
        try {
            FileZipUtil.zip(workPath, zipPath, zipName);
        } catch (Exception e) {
            e.printStackTrace();
            BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "?????????????????????????????????");
        }

        //?????????????????????
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
            //???????????????????????????????????????
            for (String item: list) {
                writer.write( item );
                writer.write( "\r\n");
            }
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
            BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "????????????????????????");
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
     * ??????????????????????????????????????????zip???
     * @param transactions ????????????
     * @param workPath ??????????????????
     * @return
     * @throws Throwable
     */
    private String  createZipFile(List<String> transactions, String workPath, Currency currency) throws Throwable {

        File workFolder = new File(workPath);
        if (!workFolder.exists() && !workFolder.isDirectory()) {
            workFolder.mkdirs();
        }

        //???????????????????????????????????????
        for (String item : transactions) {
            //????????????
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
                BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "???????????????????????????");
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

        //????????????
        String zipPath = System.getProperty("user.dir") + File.separator + "zip";
        String zipName = currency.getCurrencySimpleName() + "_" + System.currentTimeMillis() + ".zip";
        try {
            FileZipUtil.zip(workPath, zipPath, zipName);
        } catch (Exception e) {
            e.printStackTrace();
            BizException.fail(MessageCode.CREATE_TRANSACTION_ERR, "???????????????????????????");
        }

        //?????????????????????
        FileZipUtil.deleteDir(workFolder);

        return zipPath + File.separator + zipName;
    }
}
