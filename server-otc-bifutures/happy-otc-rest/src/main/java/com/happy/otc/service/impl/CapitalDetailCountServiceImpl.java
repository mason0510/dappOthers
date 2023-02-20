package com.happy.otc.service.impl;

import com.happy.otc.entity.CapitalDetailCount;
import com.happy.otc.service.ICapitalDetailCountService;
import com.happy.otc.dao.CapitalDetailCountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CapitalDetailCountServiceImpl implements ICapitalDetailCountService {
    @Autowired
    private CapitalDetailCountMapper capitalDetailCountMapper;

    public CapitalDetailCount getCapitalDetailCountInfo(Long userId){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return capitalDetailCountMapper.selectByPrimaryKey(params);
    }

    @Transactional
    @Override
    public Boolean addCapitalDetailCount(CapitalDetailCount capitalDetailCount){
        int result = capitalDetailCountMapper.insertSelective(capitalDetailCount);
        return result > 0;
    }
}
