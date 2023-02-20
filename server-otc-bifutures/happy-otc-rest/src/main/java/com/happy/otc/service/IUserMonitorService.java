package com.happy.otc.service;

import com.happy.otc.entity.UserMonitor;

public interface IUserMonitorService {

    public Boolean delUserMonitor(Long userMonitorId);

    public Boolean addUserMonitor(UserMonitor userMonitor);

}
