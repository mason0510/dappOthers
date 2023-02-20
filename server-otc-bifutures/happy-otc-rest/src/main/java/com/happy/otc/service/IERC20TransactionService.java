package com.happy.otc.service;

import com.happy.otc.entity.Currency;

import java.util.List;

public interface IERC20TransactionService {


    String createTransactionFile() throws Throwable;

    String ethSendRawTransaction(String data,Currency currency) throws Throwable;

    String createTxhashFile(List<String> list, String workPath, String fileName);

    String createWithdrawTransactionFile(String withdrawAddress, Double amount, Currency currency) throws Throwable;

}
