package com.happy.otc.service.impl;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.happy.otc.contants.Contants;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.UserBlackList;
import com.happy.otc.service.IUserService;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.dao.UserBlackListMapper;
import com.happy.otc.service.IUserBlackListService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserBlackListServiceImpl implements IUserBlackListService {

    @Autowired
    private UserBlackListMapper userBlackListMapper;
    @Autowired
    private IOauthService oauthService;
    @Autowired
    private IUserService userService;

    /**
     * 添加黑名单
     * @param targetId 被屏蔽人
     * @return
     */
    @Override
    public Boolean addBlackList(Long userId, Long targetId) {

        if (userId == targetId) {
            BizException.fail(MessageCode.SHIELD_SELF_ERR, "不能屏蔽自己");
        }

        //重复校验
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("targetId", targetId);
        List<UserBlackList> userBlackLists = userBlackListMapper.selectByParam(params);
        if (userBlackLists.size() > 0) {
            BizException.fail(ApiResponseCode.DATA_EXIST, "数据已存在");
        }

        UserBlackList blackList = new UserBlackList();
        blackList.setUserId(userId);
        blackList.setTargetId(targetId);
        UserDTO userDTO = userService.getUserInfoByUserId(targetId);
        if (userDTO == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "被屏蔽人不存在");
        }
        blackList.setTargetName(userDTO.getUserName());
        Integer result = userBlackListMapper.insertSelective(blackList);
        return result > 0;
    }

    /**
     * 删除黑名单
     * @param userId    用户ID
     * @param targetId  被屏蔽人ID
     * @return
     */
    @Override
    public Boolean delBlackList(Long userId, Long targetId) {
        UserBlackList params = new UserBlackList();
        params.setUserId(userId);
        params.setTargetId(targetId);
        Integer result = userBlackListMapper.deleteSelective(params);
        return result > 0;
    }

    /**
     * 获取黑名单信息
     * @param userId    用户ID
     * @return
     */
    @Override
    public PageInfo<UserBlackList> selectByUserId(Long userId, Integer pageIndex, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<UserBlackList> userBlackLists = userBlackListMapper.selectByParam(params);

        return new PageInfo<>(userBlackLists);
    }

    /**
     * 根据屏蔽人及被屏蔽人,获取屏蔽信息
     * @param userId
     * @param targetId
     * @return
     */
    @Override
    public UserBlackList selectByUserIdAndTargetId(Long userId, Long targetId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("targetId", targetId);

        List<UserBlackList> userBlackLists = userBlackListMapper.selectByParam(params);

        UserBlackList result = null;
        if (userBlackLists.size() > 0) {
            result = userBlackLists.get(0);
        }
        return result;
    }
}
