package com.happy.otc.dao;

import com.happy.otc.entity.Capital;
import com.happy.otc.vo.CapitalInfoVO;

import java.util.List;
import java.util.Map;

public interface CapitalMapper {
    int deleteByPrimaryKey(Long capitalId);

    int insert(Capital record);

    int insertSelective(Capital record);

    Capital selectByPrimaryKey(Long capitalId);

    int updateByPrimaryKeySelective(CapitalInfoVO record);

//    int updateByPrimaryKey(CapitalInfoVO record);

    List<CapitalInfoVO> selectByParam(Map<String, Object> params);
}