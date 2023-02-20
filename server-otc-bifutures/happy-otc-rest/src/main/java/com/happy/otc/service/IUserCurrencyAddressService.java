package com.happy.otc.service;

import com.happy.otc.dto.UserCurrencyAddressDTO;
import com.happy.otc.entity.Currency;
import com.happy.otc.entity.UserCurrencyAddress;
import com.github.pagehelper.PageInfo;

public interface IUserCurrencyAddressService {

    public PageInfo<UserCurrencyAddressDTO> getUserCurrencyAddressList(UserCurrencyAddress userCurrencyAddress, Integer pageIndex, Integer pageSize);

    public Boolean delUserCurrencyAddress(Long userCurrencyAddressId);

    public Boolean updateUserCurrencyAddress(UserCurrencyAddress userCurrencyAddress);

    public Boolean addUserCurrencyAddress(UserCurrencyAddress userCurrencyAddress);

    Long getAddressIdByAddress(String address, Currency currency);

    public PageInfo<UserCurrencyAddressDTO> queryCollectAddressList(UserCurrencyAddress userCurrencyAddress, Integer pageIndex, Integer pageSize);
}
