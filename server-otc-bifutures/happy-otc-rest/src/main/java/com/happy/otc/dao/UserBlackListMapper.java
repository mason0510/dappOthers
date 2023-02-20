package com.happy.otc.dao;

import com.happy.otc.entity.UserBlackList;

import java.util.List;
import java.util.Map;

public interface UserBlackListMapper {
    int deleteByPrimaryKey(Long blackListId);

    int insert(UserBlackList record);

    int insertSelective(UserBlackList record);

    UserBlackList selectByPrimaryKey(Long blackListId);

    int updateByPrimaryKeySelective(UserBlackList record);

    int updateByPrimaryKey(UserBlackList record);

    int deleteSelective(UserBlackList record);

    List<UserBlackList> selectByParam(Map<String, Object> params);

    List<Long> selectAllTargetIdByUserId(Long userId);
}