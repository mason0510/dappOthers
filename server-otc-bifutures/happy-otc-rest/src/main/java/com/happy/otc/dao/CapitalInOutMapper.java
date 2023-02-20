package com.happy.otc.dao;

import com.happy.otc.dto.CapitalInOutDTO;
import com.happy.otc.entity.CapitalInOut;

import java.util.List;
import java.util.Map;

public interface CapitalInOutMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CapitalInOut record);

    int insertSelective(CapitalInOut record);

    CapitalInOut selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CapitalInOut record);

    int updateByPrimaryKey(CapitalInOut record);

    List<CapitalInOutDTO> selectByParam(Map<String, Object> params);
}