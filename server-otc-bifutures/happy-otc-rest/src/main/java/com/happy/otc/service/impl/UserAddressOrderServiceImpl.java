package com.happy.otc.service.impl;

import com.happy.otc.dao.UserAddressOrderMapper;
import com.happy.otc.entity.UserAddressOrder;
import com.happy.otc.service.IUserAddressOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAddressOrderServiceImpl implements IUserAddressOrderService {

    @Autowired
    UserAddressOrderMapper userAddressOrderMapper;

    @Transactional
    @Override
    public long addUserAddressOrder(UserAddressOrder userAddressOrder){
      return   userAddressOrderMapper.insertSelective(userAddressOrder);
    }


}
