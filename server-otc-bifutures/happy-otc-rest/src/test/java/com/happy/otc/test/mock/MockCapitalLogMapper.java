package com.happy.otc.test.mock;

import com.happy.otc.dao.CapitalLogMapper;
import com.happy.otc.entity.CapitalLog;
import com.happy.otc.vo.CapitalLogInfoVO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockCapitalLogMapper extends BaseMockMapper<CapitalLog, CapitalLogInfoVO> implements CapitalLogMapper {
    public MockCapitalLogMapper() {
        super(CapitalLog.class, CapitalLogInfoVO.class, "id");
    }

    @Override
    public List<CapitalLogInfoVO> selectByParam(Map<String, Object> params) {
        return findVOByParam(params);
    }

    @Override
    public List<CapitalLog> defaultValue() {
        return Collections.emptyList();
    }
}
