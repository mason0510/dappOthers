package com.happy.otc.bifutures.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public class FuturesStrategyVo implements Serializable {
    private static final long serialVersionUID = -6714590010940382764L;
    Integer isSuccess;
    Long strategyId;

    public FuturesStrategyVo(Integer isSuccess, Long strategyId) {
        this.isSuccess = isSuccess;
        this.strategyId = strategyId;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }
}
