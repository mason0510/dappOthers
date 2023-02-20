package com.happy.otc.test.mock;

import com.alibaba.fastjson.JSON;
import com.bitan.common.utils.RedisUtil;
import com.happy.otc.contants.Contants;
import com.happy.otc.dto.UserDTO;

import java.util.HashMap;
import java.util.Map;

public class MockRedisUtil extends RedisUtil {
    private Map<String, String> valueMap = new HashMap<>();

    @Override
    public void setStringValue(final String key, String value) {
        valueMap.put(key, value);
    }

    @Override
    public void setStringValue(final String key, String value, final int seconds) {
        valueMap.put(key, value);
    }

    @Override
    public String getStringValue(final String key) {
        return valueMap.get(key);
    }

    @Override
    public boolean delete(final String key) {
        return valueMap.remove(key) != null;
    }

    public void clear() {
        valueMap.clear();
    }

    public void makeDefault() {
        UserDTO user1 = new UserDTO();
        user1.setUserId(MockUserIdentityMapper.DEFAULT_ID_1);
        user1.setMobile("123456");
        valueMap.put(Contants.REDIS_USER_INFO + user1.getUserId(), JSON.toJSONString(user1));

        UserDTO user2 = new UserDTO();
        user2.setUserId(MockUserIdentityMapper.DEFAULT_ID_2);
        user2.setMobile("654321");
        valueMap.put(Contants.REDIS_USER_INFO + user2.getUserId(), JSON.toJSONString(user2));
    }
}
