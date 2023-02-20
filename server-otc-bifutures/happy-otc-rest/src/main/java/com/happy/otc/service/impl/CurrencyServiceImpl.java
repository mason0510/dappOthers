package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bitan.common.utils.RedisUtil;
import com.happy.otc.dao.CurrencyMapper;
import com.happy.otc.entity.Currency;
import com.happy.otc.service.ICurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CurrencyServiceImpl implements ICurrencyService {

    @Autowired
    CurrencyMapper currencyMapper;
    @Autowired
    RedisUtil redisUtil;
    List<Currency> currencyList;

    public List<Currency> getCurrencyList(){

        String currencyConfigJson = redisUtil.getStringValue(ICurrencyService.REDIS_OTC_CURRENCY_CONFIG_KEY);
        if (StringUtils.isEmpty(currencyConfigJson)) {
            currencyConfigJson = initCurrency();
        }
        List<Currency> currencyList = JSONObject.parseArray(currencyConfigJson, Currency.class);
        return currencyList;
    }

    public Currency getCurrency(String simpleName){
        List<Currency> currencyList = getCurrencyList();
        for (Currency currency: currencyList) {
            if (currency.getCurrencySimpleName().equalsIgnoreCase(simpleName)){
                return  currency;
            }
        }
        return  null;
    }

    public String initCurrency() {
        List<Currency> currencyList = currencyMapper.selectByPrarm();
        redisUtil.setStringValue(ICurrencyService.REDIS_OTC_CURRENCY_CONFIG_KEY, JSONObject.toJSONString(currencyList));

        return JSONObject.toJSONString(currencyList);
    }
}
