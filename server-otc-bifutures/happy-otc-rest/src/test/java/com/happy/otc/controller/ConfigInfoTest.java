package com.happy.otc.controller;

import com.bitan.common.utils.RedisUtil;
import com.happy.otc.contants.Contants;
import com.happy.otc.dao.CurrencyMapper;
import com.happy.otc.entity.Currency;
import com.happy.otc.service.ICurrencyService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ConfigInfoTest  extends BaseJunit4Test  {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ICurrencyService currencyService;
    @Autowired
    CurrencyMapper currencyMapper;

    /**
     * 需要清空缓存数据方便测试
     */
    //@Test
    public void testRedisInit() {
        //币种信息
        redisUtil.delete( ICurrencyService.REDIS_OTC_CURRENCY_CONFIG_KEY );
        //币种的全网价格
        redisUtil.delete(Contants.REDIS_OTC_PREFIX_CURRENCY_NETWORK_ + "BTC");
        redisUtil.delete(Contants.REDIS_OTC_PREFIX_CURRENCY_NETWORK_ + "USDT");
        //对于用户权限的解禁
        redisUtil.delete(Contants.REDIS_USER_TRADE_CANCLE + "1");
        redisUtil.delete(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + "1");
    }

    /**
     * 查看缓存内容
     * @throws IOException
     */
    @Test
    public void testReadRedisContent() {
        String key =ICurrencyService.REDIS_OTC_CURRENCY_CONFIG_KEY;
        //币种信息
        System.out.println(redisUtil.getStringValue(key));
    }
    @Test
    public  void  testCurrencyInfo(){

       String[] name = new String[]{"BTC", "USDT"};
        List<Currency> list =  currencyService.getCurrencyList();
        assertFalse(list.isEmpty());
//        assertEquals(name[0], list.get(0).getCurrencySimpleName());
//        assertEquals(name[1], list.get(1).getCurrencySimpleName());

    }


    @Test
    @Transactional
    public void  testSaveCurrency(){
        Currency currency = new Currency();
        currency.setCurrencyChineseName( "testA" );
        currency.setCurrencyEnglishName( "testA" );
        currency.setCurrencySimpleName( "testA" );
        assertEquals(currencyMapper.insertSelective(currency),1) ;
    }
}
