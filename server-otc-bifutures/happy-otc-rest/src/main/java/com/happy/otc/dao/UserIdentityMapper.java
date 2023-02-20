package com.happy.otc.dao;

import com.happy.otc.entity.UserIdentity;
import com.happy.otc.vo.UserIdentityVO;

import java.util.List;
import java.util.Map;

public interface UserIdentityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserIdentity record);

    int insertSelective(UserIdentity record);

    UserIdentity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserIdentity record);

    int updateByPrimaryKey(UserIdentity record);

    List<UserIdentityVO> selectByParam(Map<String, Object> params);

    List<UserIdentity> selectByParam2(Map<String, Object> params);

    int addSeller(List<Long> list);
}