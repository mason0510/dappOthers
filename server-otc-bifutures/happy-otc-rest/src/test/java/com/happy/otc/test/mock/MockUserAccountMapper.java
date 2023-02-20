package com.happy.otc.test.mock;

import com.happy.otc.dao.UserAccountMapper;
import com.happy.otc.entity.UserAccount;
import com.happy.otc.proto.Payaccount;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockUserAccountMapper extends BaseMockMapper<UserAccount, UserAccount> implements UserAccountMapper {
    public MockUserAccountMapper() {
        super(UserAccount.class, UserAccount.class, "userAccountId");
    }

    @Override
    public int updateByPrimaryKeyWithBLOBs(UserAccount record) {
        return updateByPrimaryKey(record);
    }

    @Override
    public List<UserAccount> selectByParam(Map<String, Object> params) {
        return findByParam(params);
    }

    @Override
    public List<UserAccount> defaultValue() {
        UserAccount account1 = new UserAccount();
        account1.setUserId(MockUserIdentityMapper.DEFAULT_ID_1);
        account1.setPayStatus(Payaccount.PaymentStatus.ON.getNumber());

        return Collections.singletonList(account1);
    }
}
