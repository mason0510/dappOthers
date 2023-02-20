package com.happy.otc.dao;

import com.happy.otc.entity.CapitalDetailCount;

import java.util.Map;

public interface CapitalDetailCountMapper {
    int deleteByPrimaryKey(Long capitalDetailCountId);

    int insert(CapitalDetailCount record);

    int insertSelective(CapitalDetailCount record);

    CapitalDetailCount selectByPrimaryKey(Map<String, Object> params);

    int updateByPrimaryKeySelective(CapitalDetailCount record);

    int updateByPrimaryKey(CapitalDetailCount record);

    CapitalDetailCount selectByUserId(Long userId);
}