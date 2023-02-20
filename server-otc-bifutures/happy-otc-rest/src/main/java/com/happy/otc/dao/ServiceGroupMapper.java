package com.happy.otc.dao;

import com.happy.otc.entity.ServiceGroup;

public interface ServiceGroupMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(ServiceGroup record);

    int insertSelective(ServiceGroup record);

    ServiceGroup selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(ServiceGroup record);

    int updateByPrimaryKey(ServiceGroup record);
}