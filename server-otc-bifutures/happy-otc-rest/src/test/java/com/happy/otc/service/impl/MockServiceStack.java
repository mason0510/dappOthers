package com.happy.otc.service.impl;

import com.bitan.common.utils.RedisUtil;
import com.happy.otc.dao.*;
import com.happy.otc.service.*;
import com.happy.otc.service.remote.IMessageService;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.test.mock.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MockServiceStack {
    public MockCapitalMapper capitalMapper = new MockCapitalMapper();
    public MockCommodityMapper commodityMapper = new MockCommodityMapper();
    public MockCapitalDetailMapper capitalDetailMapper = new MockCapitalDetailMapper();
    public MockCapitalLogMapper capitalLogMapper = new MockCapitalLogMapper();
    public MockCapitalDetailCountMapper capitalDetailCountMapper = new MockCapitalDetailCountMapper();
    public MockUserIdentityMapper userIdentityMapper = new MockUserIdentityMapper();
    public MockUserBlackListMapper userBlackListMapper = new MockUserBlackListMapper();
    public MockTransactionPairMapper transactionPairMapper = new MockTransactionPairMapper();
    public MockFeeRuleMapper feeRuleMapper = new MockFeeRuleMapper();
    public MockCurrencyMapper currencyMapper = new MockCurrencyMapper();
    public MockUserAccountMapper userAccountMapper = new MockUserAccountMapper();

    public MockRedisUtil redisUtil = new MockRedisUtil();
    public MockMessageService messageService = new MockMessageService();
    public UserServiceImpl userService = new UserServiceImpl();
    public CapitalServiceImpl capitalService = new CapitalServiceImpl();
    public UserIdentityServiceImpl userIdentityService = new UserIdentityServiceImpl();
    public CommodityServiceImpl commodityService = new CommodityServiceImpl();
    public CurrencyServiceImpl currencyService = new CurrencyServiceImpl();
    public FeeRuleServiceImpl feeRuleService = new FeeRuleServiceImpl();
    public TransactionPairServiceImpl transactionPairService = new TransactionPairServiceImpl();
    public EasemobServiceImpl easemobService = new EasemobServiceImpl();
    public MockOauthService oauthService = new MockOauthService();
    public UserAccountServiceImpl userAccountService = new UserAccountServiceImpl();
    public static MockServiceStack DEFAULT = new MockServiceStack();

    public MockServiceStack() {
        Map<Class<?>, Object> instanceMap = new HashMap<>();
        instanceMap.put(CapitalMapper.class, capitalMapper);
        instanceMap.put(CommodityMapper.class, commodityMapper);
        instanceMap.put(CapitalDetailMapper.class, capitalDetailMapper);
        instanceMap.put(CapitalDetailCountMapper.class, capitalDetailCountMapper);
        instanceMap.put(UserIdentityMapper.class, userIdentityMapper);
        instanceMap.put(CapitalLogMapper.class, capitalLogMapper);
        instanceMap.put(UserBlackListMapper.class, userBlackListMapper);
        instanceMap.put(TransactionPairMapper.class, transactionPairMapper);
        instanceMap.put(FeeRuleMapper.class, feeRuleMapper);
        instanceMap.put(CurrencyMapper.class, currencyMapper);
        instanceMap.put(UserAccountMapper.class, userAccountMapper);

        instanceMap.put(RedisUtil.class, redisUtil);
        instanceMap.put(IMessageService.class, messageService);
        instanceMap.put(IUserService.class, userService);
        instanceMap.put(ICapitalService.class, capitalService);
        instanceMap.put(IUserIdentityService.class, userIdentityService);
        instanceMap.put(ICommodityService.class, commodityService);
        instanceMap.put(ThreadPoolTaskExecutor.class, new MockThreadPoolTaskExecutor());
        instanceMap.put(ITransactionPairService.class, transactionPairService);
        instanceMap.put(IEasemobService.class, easemobService);
        instanceMap.put( IOauthService.class, oauthService);
        instanceMap.put( ICurrencyService.class, currencyService);
        instanceMap.put(IUserAccountService.class, userAccountService);
        instanceMap.put(IFeeRuleService.class, feeRuleService);


        for (Map.Entry<Class<?>, Object> entry : instanceMap.entrySet()) {
            for(Field field : entry.getValue().getClass().getDeclaredFields()) {
                Class<?> fieldType = field.getType();
                Object injected = instanceMap.get(fieldType);
                if (injected != null) {
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(), injected);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void makeDefault() {
        capitalMapper.makeDefault();
        commodityMapper.makeDefault();
        capitalDetailMapper.makeDefault();
        capitalLogMapper.makeDefault();
        capitalDetailCountMapper.makeDefault();
        userIdentityMapper.makeDefault();
        userBlackListMapper.makeDefault();
        feeRuleMapper.makeDefault();
        currencyMapper.makeDefault();
        userAccountMapper.makeDefault();
        redisUtil.makeDefault();
        messageService.clear();
        oauthService.clear();
    }
}
