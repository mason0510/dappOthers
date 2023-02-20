package com.happy.otc.test.mock;

import com.happy.otc.dao.FeeRuleMapper;
import com.happy.otc.entity.FeeRule;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockFeeRuleMapper extends BaseMockMapper<FeeRule, FeeRule> implements FeeRuleMapper {
    public MockFeeRuleMapper() {
        super(FeeRule.class, FeeRule.class, "id");
    }

    @Override
    public List<FeeRule> selectByParam(Map<String, Object> params) {
        return findByParam(params);
    }

    @Override
    public List<FeeRule> defaultValue() {
        FeeRule sellFee = new FeeRule();
        sellFee.setCurrencyId(1L);
        sellFee.setType(1);
        sellFee.setFeeType((byte) 2);
        sellFee.setFeeNumber(new BigDecimal(0.05));

        return Collections.singletonList(sellFee);
    }
}
