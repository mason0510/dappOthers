package com.happy.otc.dao;

import com.happy.otc.entity.CapitalLog;
import com.happy.otc.vo.CapitalLogInfoVO;

import java.util.List;
import java.util.Map;

public interface CapitalLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CapitalLog record);

    int insertSelective(CapitalLog record);

    CapitalLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CapitalLog record);

    int updateByPrimaryKey(CapitalLog record);

    List<CapitalLogInfoVO> selectByParam(Map<String, Object> params);
}