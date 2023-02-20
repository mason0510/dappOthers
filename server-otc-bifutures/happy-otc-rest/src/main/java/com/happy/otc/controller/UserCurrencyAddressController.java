package com.happy.otc.controller;

import com.bitan.common.login.LoginDataHelper;
import com.happy.otc.service.IUserCurrencyAddressService;
import com.happy.otc.util.ProtoUtils;
import com.happy.otc.dto.UserCurrencyAddressDTO;
import com.happy.otc.entity.UserCurrencyAddress;
import com.happy.otc.proto.CurrencyAddressInfo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "用户充币地址Api")
public class UserCurrencyAddressController {

    @Autowired
    private IUserCurrencyAddressService userCurrencyAddressService;

    @ApiOperation(value = "用户充币地址列表")
    @GetMapping(value = "/user-currency-address-list")
    public CurrencyAddressInfo.UserCurrencyAddressListResult getUserCurrencyAddressList(){
        Long userId = LoginDataHelper.getUserId();

        UserCurrencyAddress userCurrencyAddress = new UserCurrencyAddress();
        userCurrencyAddress.setUserId(userId);
        PageInfo<UserCurrencyAddressDTO> list = userCurrencyAddressService.getUserCurrencyAddressList(userCurrencyAddress,1,10);

        //请求数据的封装
        List<CurrencyAddressInfo.UserCurrencyAddressDTO> listDTO = new ArrayList<>();
        CurrencyAddressInfo.UserCurrencyAddressDTO.Builder userCurrencyAddressDTO = CurrencyAddressInfo.UserCurrencyAddressDTO.newBuilder();
        for (UserCurrencyAddressDTO currencyAddress:list.getList()) {
            if (currencyAddress.getCurrencyId().compareTo( 5004L ) == 0
                    || currencyAddress.getCurrencyId().compareTo( 5037L ) == 0){
                userCurrencyAddressDTO.setAddress(currencyAddress.getAddress());
            }else{
                userCurrencyAddressDTO.setAddress("0x"+currencyAddress.getAddress());
            }
            userCurrencyAddressDTO.setUserId(currencyAddress.getUserId());
            userCurrencyAddressDTO.setDetail(currencyAddress.getDetail());
            userCurrencyAddressDTO.setCurrencySimpleName(currencyAddress.getCurrencySimpleName());
            userCurrencyAddressDTO.setCurrencyId(currencyAddress.getCurrencyId());
            userCurrencyAddressDTO.setUserCurrencyAddressId(currencyAddress.getId());
            listDTO.add(userCurrencyAddressDTO.build());
        }

        CurrencyAddressInfo.UserCurrencyAddressListResult result = CurrencyAddressInfo.UserCurrencyAddressListResult.getDefaultInstance();
        return ProtoUtils.createPageResultSuccess(listDTO, list.getPageNum(), list.getPageSize(), list.getTotal(), result);
    }

}
