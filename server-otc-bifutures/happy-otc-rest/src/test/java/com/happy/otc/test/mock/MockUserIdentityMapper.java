package com.happy.otc.test.mock;

import com.happy.otc.dao.UserIdentityMapper;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.enums.UserOtcTypeEnum;
import com.happy.otc.vo.UserIdentityVO;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MockUserIdentityMapper extends BaseMockMapper<UserIdentity, UserIdentityVO> implements UserIdentityMapper {
    public static Long DEFAULT_ID_1 = 12L;
    public static Long DEFAULT_ID_2 = 13L;

    public MockUserIdentityMapper() {
        super(UserIdentity.class, UserIdentityVO.class, "id");
    }

    @Override
    public List<UserIdentityVO> selectByParam(Map<String, Object> params) {
        return findVOByParam(params);
    }

    @Override
    public List<UserIdentity> selectByParam2(Map<String, Object> params) {
        return findByParam(params);
    }

    @Override
    public int addSeller(List<Long> list) {
        //TODO
        return 0;
    }

    @Override
    public List<UserIdentity> defaultValue() {
        UserIdentity user1 = new UserIdentity();
        user1.setUserOtcType(UserOtcTypeEnum.SELLER.getValue().byteValue());
        user1.setCapitalCipher("fundPassword1");
        user1.setUserId(DEFAULT_ID_1);

        UserIdentity user2 = new UserIdentity();
        user2.setUserId(DEFAULT_ID_2);
        user2.setUserOtcType(UserOtcTypeEnum.NORMAL.getValue().byteValue());
        user2.setCapitalCipher("fundPassword2");
        return Arrays.asList(user1, user2);
    }
}
