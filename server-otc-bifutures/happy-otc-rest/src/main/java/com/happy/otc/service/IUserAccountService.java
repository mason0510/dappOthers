package com.happy.otc.service;

import com.happy.otc.entity.UserAccount;

import java.util.List;

public interface IUserAccountService {

    public Long addUserAccount(UserAccount userAccount);

    public Boolean updateUserAccount(UserAccount userAccount);

    public Boolean delUserAccount( Long userAccountId,Long userId);

    public UserAccount getUserAccount(Long userAccountId);

    public List<UserAccount> getUserAccountList(Long userId);

    public Boolean changeAccountStatus(UserAccount userAccount);
}
