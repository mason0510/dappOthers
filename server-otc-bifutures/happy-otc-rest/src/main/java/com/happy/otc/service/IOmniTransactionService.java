package com.happy.otc.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: zhuligang
 * @Date: 2018/9/21 18:07
 * @Description:
 */
public interface IOmniTransactionService {

    List<String> sweepWallets(String targetAddress, String fundAddress);

    String createTransactionFile() throws Throwable;

    String createWithdrawTransactionFile(String withdrawAddress, String amount) throws Throwable;

    List<String> withdrawWallet(String withdrawAddress, String amount,String targetAddress, String fundAddress);
}
