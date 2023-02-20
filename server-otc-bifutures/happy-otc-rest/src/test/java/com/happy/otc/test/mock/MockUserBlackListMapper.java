package com.happy.otc.test.mock;

import com.google.common.collect.ImmutableMap;
import com.happy.otc.dao.UserBlackListMapper;
import com.happy.otc.entity.CapitalLog;
import com.happy.otc.entity.UserBlackList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockUserBlackListMapper extends BaseMockMapper<UserBlackList,CapitalLog> implements UserBlackListMapper {

    public MockUserBlackListMapper() {
        super(UserBlackList.class,CapitalLog.class, "blackListId");
    }

    @Override
    public int deleteSelective(UserBlackList record) {
        return 0;
    }

    @Override
    public List<UserBlackList> selectByParam(Map<String, Object> params) {
        return findByParam(params);
    }

    @Override
    public List<Long> selectAllTargetIdByUserId(Long userId) {
        List<UserBlackList> list = findByParam( ImmutableMap.of("userId", userId));
        List<Long> rList = new ArrayList<>(  );
        list.forEach( v ->{
            rList.add( v.getTargetId() );
        } );
         return rList;
    }


    @Override
    public List<UserBlackList> defaultValue() {

        UserBlackList userBlackList = new UserBlackList();
        userBlackList.setBlackListId( 1L );
        userBlackList.setTargetId(1L);
        userBlackList.setUserId(1L);
        userBlackList.setTargetName("junit1");
        return Collections.singletonList(userBlackList);
    }
}
