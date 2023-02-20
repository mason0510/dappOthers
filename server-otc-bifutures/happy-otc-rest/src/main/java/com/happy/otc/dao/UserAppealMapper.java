package com.happy.otc.dao;

import com.happy.otc.entity.UserAppeal;
import com.happy.otc.vo.UserAppealInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserAppealMapper {
    int deleteByPrimaryKey(Long appealId);

    int insert(UserAppeal record);

    int insertSelective(UserAppeal record);

    UserAppeal selectByPrimaryKey(Long appealId);

    int updateByPrimaryKeySelective(UserAppeal record);

    int updateByPrimaryKey(UserAppeal record);

    List<UserAppeal> selectByParam(Map<String, Object> params);

    List<UserAppealInfoVO> selectInfoByParam(Map<String, Object> params);

    UserAppeal selectLastAppealByUserIdAndCapitalDetailId(@Param("userId") Long userId, @Param("capitalDetailId") Long capitalDetailId);
}