package com.happy.otc.dao;

import com.happy.otc.bifutures.dto.FuturesFundDetailDto;
import com.happy.otc.bifutures.entity.FuturesFundDetail;
import com.happy.otc.bifutures.entity.FuturesJyzhf;
import com.happy.otc.bifutures.pojo.FundDetailQuery;
import com.happy.otc.bifutures.pojo.TimeQuery;

import java.util.List;

/**
 * Created by Administrator on 2018\11\15 0015.
 */
public interface BiFuturesUserAssetsDetailMapper {

    int deleteById(Long userId);

    int insert(FuturesFundDetailDto futuresFundDetailDto);

    FuturesFundDetail selectById(Long userId);

    List<FuturesFundDetail> selectPageByUserId (FundDetailQuery fundDetailQuery);
    int updateAssetsDetail(FuturesFundDetailDto futuresFundDetailDto);

    List<FuturesJyzhf> selectJyshf(TimeQuery timeQuery);
    List<FuturesJyzhf> selectDyf(TimeQuery timeQuery);
}
