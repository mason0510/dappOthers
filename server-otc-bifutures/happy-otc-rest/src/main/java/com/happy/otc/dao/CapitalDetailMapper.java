package com.happy.otc.dao;

import com.happy.otc.vo.CapitalDetailInfoVO;
import com.happy.otc.entity.CapitalDetail;

import java.util.List;
import java.util.Map;
public interface CapitalDetailMapper {
    int deleteByPrimaryKey(Long capitalDetailId);

    int insert(CapitalDetail record);

    int insertSelective(CapitalDetail record);

    CapitalDetail selectByPrimaryKey(Long capitalDetailId);

    int updateByPrimaryKeySelective(CapitalDetail record);

    int updateByPrimaryKey(CapitalDetail record);

    List<CapitalDetailInfoVO> selectByParam(Map<String, Object> params);
}