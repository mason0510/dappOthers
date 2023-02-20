package com.happy.otc.controller;

import com.bitan.common.utils.RedisUtil;
import com.happy.otc.enums.FeeRuleTypeEnum;
import com.happy.otc.service.ICapitalDetailService;
import com.happy.otc.service.ICommodityService;
import com.happy.otc.service.IFeeRuleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;


public class FeeRuleTest extends BaseJunit4Test {
    @Autowired
    ICommodityService commodityService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    IFeeRuleService feeRuleService;

    /**
     * 对于固定及百分比的收费金额的校验
     */
    @Test
    @Transactional
    public void testFeeRule() {

        BigDecimal buy  = feeRuleService.calculateFee(FeeRuleTypeEnum.BUY.getValue(),5004L, new BigDecimal( 1000 ) );
        BigDecimal sell = feeRuleService.calculateFee(FeeRuleTypeEnum.SELL.getValue(),5004L, new BigDecimal( 1000 ) );
        assertEquals(5, buy.doubleValue(),0.000000);
        assertEquals(5, sell.doubleValue(),0.000000);

        BigDecimal with1 = feeRuleService.calculateFee(FeeRuleTypeEnum.WITHDRAWALS.getValue(),5004L, new BigDecimal( 1000 ) );
        assertEquals(0.0005, with1.doubleValue(),0.000000);

    }

}