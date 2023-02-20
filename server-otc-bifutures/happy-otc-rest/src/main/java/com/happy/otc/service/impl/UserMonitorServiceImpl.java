package com.happy.otc.service.impl;

import com.bitan.common.utils.RedisUtil;
import com.happy.otc.dao.UserMonitorMapper;
import com.happy.otc.entity.UserMonitor;
import com.happy.otc.service.IUserMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserMonitorServiceImpl implements IUserMonitorService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    UserMonitorMapper userMonitorMapper;
    @Override
    public Boolean delUserMonitor(Long userMonitorId) {
        Integer result = userMonitorMapper.deleteByPrimaryKey(userMonitorId);
        return result > 0 ;
    }

    @Override
    @Transactional
    public Boolean addUserMonitor(UserMonitor userMonitor) {
        return userMonitorMapper.insertSelective(userMonitor) > 0;
    }
}
