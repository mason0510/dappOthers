package com.happy.otc.service.bifutures;

import com.happy.otc.bifutures.dto.FuturesStrategyDto;
import com.happy.otc.bifutures.vo.FuturesStrategyVo;

/**
 * Created by Administrator on 2018\11\21 0021.
 */
public interface SimStrategyService {
    FuturesStrategyVo createSimStrategy(FuturesStrategyDto futuresStrategyDto);
}
