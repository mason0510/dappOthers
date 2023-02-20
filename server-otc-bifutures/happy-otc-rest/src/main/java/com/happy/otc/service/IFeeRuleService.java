package com.happy.otc.service;

import java.math.BigDecimal;

public interface IFeeRuleService {
    BigDecimal calculateFee(Integer type, Long currencyId, BigDecimal money);

    BigDecimal calculateMaxSellMoney(Integer type, Long currencyId, BigDecimal money);
}
