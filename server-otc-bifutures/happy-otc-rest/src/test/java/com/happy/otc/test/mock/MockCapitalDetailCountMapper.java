package com.happy.otc.test.mock;

import com.google.common.collect.ImmutableMap;
import com.happy.otc.dao.CapitalDetailCountMapper;
import com.happy.otc.entity.CapitalDetailCount;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MockCapitalDetailCountMapper extends BaseMockMapper<CapitalDetailCount, CapitalDetailCount> implements CapitalDetailCountMapper {
    public MockCapitalDetailCountMapper() {
        super(CapitalDetailCount.class, CapitalDetailCount.class, "capitalDetailCountId");
    }

    @Override
    public CapitalDetailCount selectByPrimaryKey(Map<String, Object> params) {
        return findByParam(params).get(0);
    }

    @Override
    public CapitalDetailCount selectByUserId(Long userId) {
        return selectByPrimaryKey(ImmutableMap.of("userId", userId));
    }

    @Override
    public List<CapitalDetailCount> defaultValue() {
        CapitalDetailCount count1 = new CapitalDetailCount();
        count1.setUserId(MockUserIdentityMapper.DEFAULT_ID_1);
        count1.setTotalTradeCount(0L);
        count1.setSuccessTradeCount(0L);
        count1.setCloseRate(0.0);
        count1.setExchangeHour(0L);

        CapitalDetailCount count2 = new CapitalDetailCount();
        count2.setUserId(MockUserIdentityMapper.DEFAULT_ID_2);
        count2.setTotalTradeCount(0L);
        count2.setSuccessTradeCount(0L);
        count2.setCloseRate(0.0);
        count2.setExchangeHour(0L);

        return Arrays.asList(count1, count2);
    }
}
