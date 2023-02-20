package com.happy.otc.dao;

import com.happy.otc.entity.UserAccount;

import java.util.List;
import java.util.Map;

public interface UserAccountMapper {
    int deleteByPrimaryKey(Long userAccountId);

    int insert(UserAccount record);

    int insertSelective(UserAccount record);

    UserAccount selectByPrimaryKey(Long userAccountId);

    int updateByPrimaryKeySelective(UserAccount record);

    int updateByPrimaryKeyWithBLOBs(UserAccount record);

    int updateByPrimaryKey(UserAccount record);

    List<UserAccount> selectByParam(Map<String, Object> params);
}