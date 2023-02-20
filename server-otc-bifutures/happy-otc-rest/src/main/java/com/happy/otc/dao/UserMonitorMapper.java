package com.happy.otc.dao;

import com.happy.otc.entity.UserMonitor;

public interface UserMonitorMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserMonitor record);

    int insertSelective(UserMonitor record);

    UserMonitor selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserMonitor record);

    int updateByPrimaryKey(UserMonitor record);
}