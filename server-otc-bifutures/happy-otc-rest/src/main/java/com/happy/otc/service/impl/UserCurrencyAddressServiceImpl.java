package com.happy.otc.service.impl;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happy.btc.AddressUtils;
import com.happy.otc.contants.Contants;
import com.happy.otc.dao.CurrencyMapper;
import com.happy.otc.dao.UserCurrencyAddressMapper;
import com.happy.otc.dto.UserCurrencyAddressDTO;
import com.happy.otc.entity.Currency;
import com.happy.otc.entity.UserCurrencyAddress;
import com.happy.otc.service.IUserCurrencyAddressService;
import com.happy.otc.util.ETHAddressUtils;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserCurrencyAddressServiceImpl implements IUserCurrencyAddressService {

    @Autowired
    UserCurrencyAddressMapper userCurrencyAddressMapper;

    @Autowired
    CurrencyMapper currencyMapper;

    @Value("${btc.line.public.key}")
    private String publicKey;
    @Value(("${btc.line.mode}"))
    private String btcMode;
    @Value("${eth.line.public.key}")
    private String ethPublicKey;

    @Override
    public PageInfo<UserCurrencyAddressDTO> getUserCurrencyAddressList(UserCurrencyAddress userCurrencyAddress, Integer pageIndex, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId",userCurrencyAddress.getUserId());
        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<UserCurrencyAddressDTO> list = userCurrencyAddressMapper.selectByParam(params);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }


    @Override
    public PageInfo<UserCurrencyAddressDTO> queryCollectAddressList(UserCurrencyAddress userCurrencyAddress, Integer pageIndex, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("currencyId",userCurrencyAddress.getCurrencyId());
        params.put("createTime",userCurrencyAddress.getCreateTime());
        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<UserCurrencyAddressDTO> list = userCurrencyAddressMapper.selectCollectAddressByParam(params);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Transactional
    @Override
    public Boolean delUserCurrencyAddress(Long userCurrencyAddressId) {
        int result = userCurrencyAddressMapper.deleteByPrimaryKey(userCurrencyAddressId);
        return result > 0;
    }
    @Transactional
    @Override
    public Boolean updateUserCurrencyAddress(UserCurrencyAddress userCurrencyAddress) {
        int result = userCurrencyAddressMapper.updateByPrimaryKeySelective(userCurrencyAddress);
        return result > 0;
    }
    @Transactional
    @Override
    public Boolean addUserCurrencyAddress(UserCurrencyAddress userCurrencyAddress) {

        // 正式环境去除 TestNet3Params.get() 参数，使用比特币主网
        AddressUtils utils;
        if (btcMode.equals("test")) {
            utils = new AddressUtils(publicKey, TestNet3Params.get());
        } else {
            utils = new AddressUtils(publicKey);
        }
        ETHAddressUtils ethUtils = new ETHAddressUtils(ethPublicKey);

        List<Currency> list = currencyMapper.selectByPrarm();

        int result = 0;
        for (Currency currency :list){
            if (currency.getCurrencyId() == 5005){
                continue;
            }
            if (currency.getCurrencyId().compareTo( 5004L ) == 0
                    || currency.getCurrencyId().compareTo( 5037L ) == 0){
                userCurrencyAddress.setAddress(utils.deriveAddress(userCurrencyAddress.getUserAddressId().intValue()));
            }else{
                userCurrencyAddress.setAddress(ethUtils.deriveAddress(userCurrencyAddress.getUserAddressId().intValue()));
            }
            userCurrencyAddress.setCurrencyId(currency.getCurrencyId());
            userCurrencyAddress.setCreateTime(new Date());
            result = userCurrencyAddressMapper.insertSelective(userCurrencyAddress);
        }
        return result > 0;
    }

    /**
     * 根据地址获取地址序列号
     * @param address
     * @param currency
     * @return
     */
    @Override
    public Long getAddressIdByAddress(String address, Currency currency) {
        Map<String, Object> params = new HashMap<>();
        params.put("address", address);
        params.put("currencyId", currency.getCurrencyId());
        List<UserCurrencyAddressDTO> userCurrencyAddressDTOS = userCurrencyAddressMapper.selectByParam(params);
        if (userCurrencyAddressDTOS.size() == 0) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "数据不存在");
        }
        return userCurrencyAddressDTOS.get(0).getUserAddressId();
    }
}

