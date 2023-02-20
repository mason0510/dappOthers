package com.happy.otc.bifutures;

/**
 * Created by Administrator on 2018\11\21 0021.
 */
public interface CloseReasion {
    String TRIGGER_QUITGAIN_SELL = "触发止盈平仓";
    String TRIGGER_QUITLOSS_SELL = "触发止损平仓";
    String TRIGGER_CLOSEMARKET_SELL = "触发休市平仓";
    String TRIGGER_SELF_SELL = "自主平仓";
    String TRIGGER_COERCIVE_SELL = "触发强平价平仓";
    String TRIGGER_NO_DEFERCHARGE_SELL = "递延费不足平仓";
}
