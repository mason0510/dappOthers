package com.happy.otc.test.mock;

import com.happy.otc.dao.CurrencyMapper;
import com.happy.otc.entity.Currency;

import java.util.Collections;
import java.util.List;

public class MockCurrencyMapper extends BaseMockMapper<Currency, Currency> implements CurrencyMapper {
    public MockCurrencyMapper() {
        super(Currency.class, Currency.class, "currencyId");
    }

    @Override
    public List<Currency> selectByPrarm() {
        return findByParam(Collections.emptyMap());
    }

    @Override
    public List<Currency> defaultValue() {
        Currency btc = new Currency();
        btc.setCurrencyChineseName("比特币");
        btc.setCurrencyEnglishName("Bitcoin");
        btc.setCurrencySimpleName("BTC");

        return Collections.singletonList(btc);
    }
}
