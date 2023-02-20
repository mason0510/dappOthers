package com.happy.otc.service;

import com.happy.otc.entity.UserIdentity;
import com.happy.otc.proto.Account;
import com.happy.otc.vo.UserIdentityVO;
import com.happy.otc.vo.manager.SellerSearchVo;
import com.happy.otc.vo.manager.UserIdentitySearchVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IUserIdentityService {
    Boolean saveUserIdentity(UserIdentity userIdentity);

    Boolean updateUserIdentity(UserIdentity userIdentity);

    PageInfo<UserIdentityVO> getPageList(UserIdentitySearchVO searchVO);

    UserIdentityVO getByUserId(Long userId);

    /**
     * 修改用户的资金密码
     * @return
     */
    Boolean addUserExtendIdentity(Long userId, Account.UpdateCapitalCipherParamVO userExtendInfo);

    /**
     * otc获取用户额外信息
     * @return
     */
    UserIdentity getUserExtendIdentity(Long userId);

    /**
     * 获取商户信息
     * @param searchVO
     * @return
     */
    PageInfo<UserIdentityVO> getSellerPageList(SellerSearchVo searchVO);

    /**
     * 添加商户
     * @param userIds
     * @return
     */
    Integer addSeller(List<Long> userIds);

    /**
     * 判断是否为商户
     * @param userId
     * @return
     */
    Boolean isSeller(Long userId);

    /**
     * 判断资产密码
     * @param input
     * @param pw
     * @param userId
     */
    void checkCapitalCipher(String input, String pw, Long userId);
}
