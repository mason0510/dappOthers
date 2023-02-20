package com.happy.otc.service;

import com.happy.otc.entity.CapitalDetailCount;

public interface ICapitalDetailCountService {

    /**
     * 获取用户交易统计信息
     *
     * @param userId
     * @return
     */
    public CapitalDetailCount getCapitalDetailCountInfo(Long userId);

    /**
     * 添加用户交易统计信息
     *
     * @return
     */
    public Boolean addCapitalDetailCount(CapitalDetailCount capitalDetailCount);
}
