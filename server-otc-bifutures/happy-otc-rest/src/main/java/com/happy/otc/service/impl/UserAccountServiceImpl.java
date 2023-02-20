package com.happy.otc.service.impl;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dao.UserAccountMapper;
import com.happy.otc.entity.UserAccount;
import com.happy.otc.enums.PayMethodEnums;
import com.happy.otc.proto.Payaccount;
import com.happy.otc.service.ICommodityService;
import com.happy.otc.service.IUserAccountService;
import com.happy.otc.service.IUserIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserAccountServiceImpl implements IUserAccountService {

    @Autowired
    UserAccountMapper userAccountMapper;
    @Autowired
    ICommodityService commodityService;
    @Transactional
    @Override
    public Long addUserAccount(UserAccount userAccount) {

        //银行卡以外，同一支付类型只能有一个
        Map<String, Object> params = new HashMap<>();
        List<UserAccount> validate;
        if (!userAccount.getPayType().equals(PayMethodEnums.BANK_CARD.getValue())) {
            params.put("userId", userAccount.getUserId());
            params.put("payType", userAccount.getPayType());
            validate = userAccountMapper.selectByParam(params);
            if (validate.size() > 0) {
                BizException.fail(ApiResponseCode.DATA_EXIST, "该支付类型已添加");
            }
        }
        //用户添加支付方式不超过3个，默认打开
        params.put("userId", userAccount.getUserId());
        params.put("payType", null);
        validate = userAccountMapper.selectByParam(params);
        if (validate.size() < 3) {
            userAccount.setPayStatus(Payaccount.PaymentStatus.ON.getNumber());
        }
        int result = userAccountMapper.insertSelective(userAccount);
        return userAccount.getUserAccountId();
    }

    @Transactional
    @Override
    public Boolean delUserAccount(Long userAccountId,Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId",userId);
        params.put("status",0);
        Long count = commodityService.countCommodity(params);
        if (count > 0){
            BizException.fail(MessageCode.EDIT_COMMODITY_ERR,"你有广告在发布，无法修改账户");
        }
        int result = userAccountMapper.deleteByPrimaryKey(userAccountId);
        return result > 0;
    }

    @Override
    public List<UserAccount> getUserAccountList(Long userId) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId",userId);
        return userAccountMapper.selectByParam(params);
    }

    @Override
    public UserAccount getUserAccount(Long userAccountId) {
        return userAccountMapper.selectByPrimaryKey(userAccountId);
    }

    @Transactional
    @Override
    public Boolean updateUserAccount(UserAccount userAccount) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId",userAccount.getUserId());
        params.put("status",0);
        Long count = commodityService.countCommodity(params);
        if (count > 0){
            BizException.fail(MessageCode.EDIT_COMMODITY_ERR,"你有广告在发布，无法修改账户");
        }
        int result = userAccountMapper.updateByPrimaryKeySelective(userAccount);
        return result > 0;
    }


    @Transactional
    @Override
    public Boolean changeAccountStatus(UserAccount userAccount) {

        int result = userAccountMapper.updateByPrimaryKeySelective(userAccount);
        return result > 0;
    }
}
