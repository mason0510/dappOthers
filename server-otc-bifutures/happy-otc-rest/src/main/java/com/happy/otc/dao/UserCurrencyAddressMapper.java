package com.happy.otc.dao;

import com.happy.otc.dto.UserCurrencyAddressDTO;
import com.happy.otc.entity.UserCurrencyAddress;

import java.util.List;
import java.util.Map;

public interface UserCurrencyAddressMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserCurrencyAddress record);

    int insertSelective(UserCurrencyAddress record);

    UserCurrencyAddress selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserCurrencyAddress record);

    int updateByPrimaryKey(UserCurrencyAddress record);

    List<UserCurrencyAddressDTO> selectByParam(Map<String, Object> params);

    List<UserCurrencyAddressDTO> selectCollectAddressByParam(Map<String, Object> params);
}