package com.happy.otc.service;

import com.happy.otc.entity.UserAppeal;
import com.happy.otc.vo.manager.UserAppealSearchVO;
import com.happy.otc.vo.UserAppealInfoVO;
import com.github.pagehelper.PageInfo;

public interface IUserAppealService {

    Boolean addAppeal(UserAppeal userAppeal);

    UserAppealInfoVO selectAppealInfoById(Long userAppealId);

    void changeAppealStatus(Long userAppealId, Integer status);

    Boolean cancelAppeal(Long userAppealId, Long userId);

    PageInfo<UserAppealInfoVO> getPageList(UserAppealSearchVO searchVO);
}
