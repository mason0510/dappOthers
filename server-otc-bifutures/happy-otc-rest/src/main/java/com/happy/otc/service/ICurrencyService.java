package com.happy.otc.service;

import com.happy.otc.entity.Currency;

import java.util.List;

public interface ICurrencyService {

   public final static String REDIS_OTC_CURRENCY_CONFIG_KEY = "otc-currency-config";

   public List<Currency> getCurrencyList();

   public Currency getCurrency(String simpleName);
}
