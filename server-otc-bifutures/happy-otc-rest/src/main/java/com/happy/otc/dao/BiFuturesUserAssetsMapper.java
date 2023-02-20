package com.happy.otc.dao;

import com.happy.otc.bifutures.dto.UserAssetsDto;
import com.happy.otc.bifutures.entity.UserAssets;
import com.happy.otc.bifutures.pojo.TimeQuery;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018\11\15 0015.
 */
public interface BiFuturesUserAssetsMapper {

    int deleteByUserId(Long userId);

    int insert(UserAssetsDto userAssets);

    UserAssets selectByUserId(Long userId);

    int updateAssetsByUserAssetsDto(UserAssetsDto userAssets);

    BigDecimal getTotalFundIn(TimeQuery timeQuery);

    BigDecimal getTotalFundOut(TimeQuery timeQuery);

   // List<UserAssets> selectByParam(Map<String, Object> params);

}
