package com.happy.otc.dao;

import com.happy.otc.entity.Easemob;

public interface EasemobMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Easemob record);

    int insertSelective(Easemob record);

    Easemob selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Easemob record);

    int updateByPrimaryKey(Easemob record);

    Easemob selectByUserId(Long userId);
}