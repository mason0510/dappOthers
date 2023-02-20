package com.happy.otc.test.mock;

import com.google.common.collect.ImmutableMap;
import com.happy.otc.dao.CapitalMapper;
import com.happy.otc.entity.Capital;
import com.happy.otc.vo.CapitalInfoVO;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MockCapitalMapper extends BaseMockMapper<Capital, CapitalInfoVO> implements CapitalMapper {
    public MockCapitalMapper() {
        super(Capital.class, CapitalInfoVO.class, "capitalId");
    }

    @Override
    public int updateByPrimaryKeySelective(CapitalInfoVO record) {
        return this.updateByPrimaryKeySelective(fromVO(record));
    }

    @Override
    public List<CapitalInfoVO> selectByParam(Map<String, Object> params) {
        return findVOByParam(params);
    }

    public Capital selectByUserId(Long userId) {
        return findByParam(ImmutableMap.of("userId", userId)).get(0);
    }

    public Capital selectByUserId(Long userId, Long currencyId) {
        return findByParam(ImmutableMap.of("userId", userId, "currencyId", currencyId)).get(0);
    }

    @Override
    public List<Capital> defaultValue() {
        Capital user1Capital = new Capital();
        user1Capital.setCapitalId(10L);
        user1Capital.setUserId(12L);
        user1Capital.setCapitalFrozen(new BigDecimal(1000));
        user1Capital.setCapitalAvailable(new BigDecimal(9000));
        user1Capital.setCurrencyId(1L);

        Capital user2Capital = new Capital();
        user2Capital.setCapitalId(11L);
        user2Capital.setUserId(13L);
        user2Capital.setCapitalFrozen(new BigDecimal(500));
        user2Capital.setCapitalAvailable(new BigDecimal(1500));
        user2Capital.setCurrencyId(1L);

        return Arrays.asList(user1Capital, user2Capital);
    }
}
