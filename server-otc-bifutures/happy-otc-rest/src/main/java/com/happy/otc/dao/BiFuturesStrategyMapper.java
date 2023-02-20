package com.happy.otc.dao;

import com.happy.otc.bifutures.dto.FuturesStrategyDto;
import com.happy.otc.bifutures.entity.FuturesStrategy;
import com.happy.otc.bifutures.pojo.FuturesStrategyQuery;
import com.happy.otc.bifutures.pojo.TimeQuery;

import java.util.List;

/**
 * Created by Administrator on 2018\11\15 0015.
 */
public interface BiFuturesStrategyMapper {

    int deleteById(Long userId);

    int insert(FuturesStrategyDto futuresStrategyDto);

    FuturesStrategy selectById(Long id);

    List<FuturesStrategy> selectPageByUserId(FuturesStrategyQuery futuresStrategyQuery);

    List<FuturesStrategy> selectByUserId(FuturesStrategyQuery futuresStrategyQuery);
    List<FuturesStrategy> selectOrder(FuturesStrategyQuery futuresStrategyQuery);
    List<FuturesStrategy> selectBuyDeal(FuturesStrategyQuery futuresStrategyQuery);
    List<FuturesStrategy> selectSell(FuturesStrategyQuery futuresStrategyQuery);
    int update(FuturesStrategyDto FuturesStrategyDto);

    Integer selectYHS(TimeQuery timeQuery);
    Integer selectJYSL(TimeQuery timeQuery);


    Integer getNewPepal(TimeQuery timeQuery);

    Integer selectAmount(FuturesStrategyQuery futuresStrategyQuery);
}
