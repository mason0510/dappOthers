package com.happy.otc.dao;

import com.happy.otc.vo.CommodityInfoVO;
import com.happy.otc.entity.Commodity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CommodityMapper {
    int deleteByPrimaryKey(Long commodityId);

    int insert(Commodity record);

    int insertSelective(Commodity record);

    Commodity selectByPrimaryKey(Long commodityId);

    List<CommodityInfoVO> selectByParam(Map<String, Object> params);

    Long countCommodity(Map<String, Object> params);

    int updateByPrimaryKeySelective(Commodity record);

//    int updateByPrimaryKey(Commodity record);

    int updateQuantity(@Param("commodityId") Long commodityId,
                       @Param("currencyQuantity") BigDecimal currencyQuantity,
                       @Param("frozenQuantity") BigDecimal frozenQuantity,
                       @Param("feeQuantity") BigDecimal feeQuantity,
                       @Param("version") Integer version);
}