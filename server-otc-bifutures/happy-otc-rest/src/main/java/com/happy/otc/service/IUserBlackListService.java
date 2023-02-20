package com.happy.otc.service;

import com.happy.otc.entity.UserBlackList;
import com.github.pagehelper.PageInfo;

public interface IUserBlackListService {
    Boolean addBlackList(Long userId, Long targetId);
    Boolean delBlackList(Long userId, Long targetId);
    PageInfo<UserBlackList> selectByUserId(Long userId, Integer pageIndex, Integer pageSize);
    UserBlackList selectByUserIdAndTargetId(Long userId, Long targetId);
}
