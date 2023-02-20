package com.happy.otc.dao;

import com.happy.otc.entity.FeeRule;

import java.util.List;
import java.util.Map;

public interface FeeRuleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FeeRule record);

    int insertSelective(FeeRule record);

    FeeRule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FeeRule record);

    int updateByPrimaryKey(FeeRule record);

    List<FeeRule> selectByParam(Map<String, Object> params);
}