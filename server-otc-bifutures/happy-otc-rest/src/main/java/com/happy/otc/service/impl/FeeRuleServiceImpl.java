package com.happy.otc.service.impl;

import com.bitan.common.exception.BizException;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dao.FeeRuleMapper;
import com.happy.otc.entity.FeeRule;
import com.happy.otc.service.IFeeRuleService;
import com.happy.otc.util.MoneyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeRuleServiceImpl implements IFeeRuleService {

    @Autowired
    FeeRuleMapper feeRuleMapper;

    /**
     * 计算手续费
     * @param type          类型
     * @param currencyId    币种
     * @param money         金额
     * @return
     */
    @Override
    public BigDecimal calculateFee(Integer type, Long currencyId, BigDecimal money) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("currencyId", currencyId);
        List<FeeRule> feeRules = feeRuleMapper.selectByParam(params);
        if (feeRules.size() == 0) {
            BizException.fail(MessageCode.FEE_CALCULATE_ERR, "手续费计算失败");
        }
        FeeRule feeRule = feeRules.get(0);
        BigDecimal result;

        if (feeRule.getFeeType() == 1) {
            //取固定额度
            result = feeRule.getFeeNumber();
        } else {
            //按比例收取
            result = MoneyUtil.moneyMul(money, feeRule.getFeeNumber());
            result = result.setScale(6, BigDecimal.ROUND_DOWN);
        }
        return result;
    }


    /**
     * 计算最大可售出金额
     * @param type          类型
     * @param currencyId    币种
     * @param money         金额
     * @return
     */
    @Override
    public BigDecimal calculateMaxSellMoney(Integer type, Long currencyId, BigDecimal money) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("currencyId", currencyId);
        List<FeeRule> feeRules = feeRuleMapper.selectByParam(params);
        if (feeRules.size() == 0) {
            BizException.fail(MessageCode.FEE_CALCULATE_ERR, "手续费计算失败");
        }
        FeeRule feeRule = feeRules.get(0);
        BigDecimal result;

        if (feeRule.getFeeType() == 1) {
            //取固定额度
            result = feeRule.getFeeNumber();
        } else {
            //按比例收取
            BigDecimal ratio = MoneyUtil.moneydiv(new BigDecimal(1),MoneyUtil.moneyAdd(new BigDecimal(1),feeRule.getFeeNumber()));
            result = MoneyUtil.moneyMul(money, ratio);
            result = result.setScale(6, BigDecimal.ROUND_DOWN);
        }
        return result;
    }
}
