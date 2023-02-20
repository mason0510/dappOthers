package com.happy.otc.dao;

import com.happy.otc.entity.UserAddressOrder;

public interface UserAddressOrderMapper {
    int deleteByPrimaryKey(Long userAddressId);

    int insert(UserAddressOrder record);

    long insertSelective(UserAddressOrder record);

    UserAddressOrder selectByPrimaryKey(Long userAddressId);

    int updateByPrimaryKeySelective(UserAddressOrder record);

    int updateByPrimaryKey(UserAddressOrder record);
}