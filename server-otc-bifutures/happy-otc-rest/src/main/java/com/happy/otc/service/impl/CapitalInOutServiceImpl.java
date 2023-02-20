package com.happy.otc.service.impl;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.happy.btc.AddressUtils;
import com.happy.otc.contants.Contants;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dao.CapitalInOutMapper;
import com.happy.otc.dao.CapitalLogMapper;
import com.happy.otc.dao.CapitalMapper;
import com.happy.otc.dto.CapitalInOutDTO;
import com.happy.otc.entity.CapitalInOut;
import com.happy.otc.entity.CapitalLog;
import com.happy.otc.enums.CapitalInOutTypeEnum;
import com.happy.otc.enums.CapitalLogTypeEnum;
import com.happy.otc.enums.FeeRuleTypeEnum;
import com.happy.otc.service.ICapitalInOutService;
import com.happy.otc.service.ICapitalService;
import com.happy.otc.service.IFeeRuleService;
import com.happy.otc.util.ETHAddressUtils;
import com.happy.otc.vo.CapitalInfoVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CapitalInOutServiceImpl implements ICapitalInOutService {

    private static final Logger logger = LogManager.getLogger(CapitalInOutServiceImpl.class);
    @Autowired
    CapitalInOutMapper capitalInOutMapper;
    @Autowired
    ICapitalService capitalService;
    @Autowired
    private CapitalMapper capitalMapper;
    @Autowired
    private CapitalLogMapper capitalLogMapper;
    @Autowired
    private IFeeRuleService feeRuleService;
    @Value("${btc.line.mode}")
    private String lineMode;

    @Override
    public PageInfo<CapitalInOutDTO> getCapitalInOutList(CapitalInOut capitalInOut, Integer pageIndex, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId",capitalInOut.getUserId());
        params.put("type",capitalInOut.getType());
        params.put("status",capitalInOut.getStatus());
        params.put("currencyId",capitalInOut.getCurrencyId());
        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<CapitalInOutDTO> list = capitalInOutMapper.selectByParam(params);

        PageInfo pageInfo = new PageInfo(list);
        return  pageInfo;
    }

    @Transactional
    @Override
    public Boolean delCapitalInOut(Long id) {
        int result = capitalInOutMapper.deleteByPrimaryKey(id);
        return result > 0;
    }

    @Transactional
    @Override
    public Boolean updateCapitalInOut(CapitalInOut capitalInOut) {
        int result = capitalInOutMapper.updateByPrimaryKeySelective(capitalInOut);
        return result > 0;
    }

    @Transactional
    @Override
    public Boolean addCapitalInOut(CapitalInOut capitalInOut) {

        if (capitalInOut.getCurrencyId().compareTo( 5004L ) == 0
                || capitalInOut.getCurrencyId().compareTo( 5037L ) == 0){
            //校验比特币的地址    todo 1.比特币以外币需要另校验
            NetworkParameters params;
            if (lineMode.equals("test")) {
                params = TestNet3Params.get();
            } else {
                params = MainNetParams.get();
            }
            if (!AddressUtils.checkAddress(capitalInOut.getCurrencyAddress(), params)) {
                BizException.fail(MessageCode.ADDRESS_ERR, "地址不正确");
            }

            if (capitalInOut.getNumberCoins().compareTo(BigDecimal.ZERO) <= 0) {
                BizException.fail(MessageCode.POSITIVE_QUANTITY_ERR, "货币数量需为正数");
            }
            if (capitalInOut.getCurrencyId().compareTo( 5004L ) == 0){
                if (capitalInOut.getNumberCoins().compareTo(BigDecimal.valueOf(0.01)) < 0) {
                    BizException.fail(MessageCode.OUT_NOT_ENOUGH_ERR, "提币数量小于最小提币数量");
                }
            }else {
                if (capitalInOut.getNumberCoins().compareTo(BigDecimal.valueOf(100)) < 0) {
                    BizException.fail(MessageCode.OUT_NOT_ENOUGH_ERR, "提币数量小于最小提币数量");
                }
            }

        }else {

            if (!capitalInOut.getCurrencyAddress().startsWith("0x")){
                BizException.fail(MessageCode.ADDRESS_ERR, "地址不正确");
            }

            if (!ETHAddressUtils.isValidAddress(capitalInOut.getCurrencyAddress())){
                BizException.fail(MessageCode.ADDRESS_ERR, "地址不正确");
            }

            if (capitalInOut.getNumberCoins().compareTo(BigDecimal.ZERO) <= 0) {
                BizException.fail(MessageCode.POSITIVE_QUANTITY_ERR, "货币数量需为正数");
            }

            if (capitalInOut.getNumberCoins().compareTo(BigDecimal.valueOf(100)) < 0) {
                BizException.fail(MessageCode.OUT_NOT_ENOUGH_ERR, "提币数量小于最小提币数量");
            }

        }


        CapitalInfoVO capitalInfoVO = capitalService.getCapitalInfoByUserIdAndCurrencyId(capitalInOut.getUserId(), capitalInOut.getCurrencyId());
        //校验当前用户是否有足够的币
        if (CapitalInOutTypeEnum.CAPITAL_OUT.getValue().equals(capitalInOut.getType())){

            //计算手续费
            BigDecimal fee = feeRuleService.calculateFee(FeeRuleTypeEnum.WITHDRAWALS.getValue(), capitalInOut.getCurrencyId(), capitalInOut.getNumberCoins());
            capitalInOut.setServiceFee(fee);

            if (capitalInOut.getNumberCoins().compareTo(capitalInfoVO.getCapitalAvailable()) > 0) {
                BizException.fail(MessageCode.NOT_ENOUGH_ERR, "币种 可提数量不够");
            }
            //用户的资金进行冻结操作
            capitalInfoVO.setCapitalFrozen(capitalInfoVO.getCapitalFrozen().add(capitalInOut.getNumberCoins()));
            capitalInfoVO.setCapitalAvailable(capitalInfoVO.getCapitalAvailable().subtract(capitalInOut.getNumberCoins()));
            capitalService.updateCapitalInfo(capitalInfoVO);
            logger.info("userId: "+capitalInfoVO.getUserId()
                    +" 提取 "+ capitalInOut.getNumberCoins().toString()
                    +"币种id"+ capitalInOut.getCurrencyId());
        }
        int result = capitalInOutMapper.insertSelective(capitalInOut);

        //记录资金变化
        if (CapitalInOutTypeEnum.CAPITAL_OUT.getValue().equals(capitalInOut.getType())) {

            CapitalLog capitalLog = new CapitalLog();
            capitalLog.setUserId(capitalInOut.getUserId());
            capitalLog.setType(CapitalLogTypeEnum.FROZEN.getValue().byteValue());
            capitalLog.setCurrencyId(capitalInOut.getCurrencyId());
            capitalLog.setFrozenNumber(capitalInOut.getNumberCoins());
            capitalLog.setAvailableNumber(capitalInOut.getNumberCoins().negate());
            capitalLog.setComment("申请提款，订单号:" + capitalInOut.getId());
            capitalLog.setOrderId(capitalInOut.getId());
            capitalLog.setCreateTime(new Date());
            capitalLogMapper.insertSelective(capitalLog);
        }

        return result > 0;
    }

    /**
     * 放币处理
     * @param id
     * @return
     */
    @Transactional
    @Override
    public Boolean withdrawals(Long id) {
        CapitalInOut capitalInOut = capitalInOutMapper.selectByPrimaryKey(id);
        if (capitalInOut == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "不存在此订单");
        }
        if (capitalInOut.getStatus() != 0) {
            BizException.fail(MessageCode.ALREADY_DEAL, "该订单已处理");
        }

        //获取用户的资金信息
        Map<String, Object> params = new HashMap<>();
        params.put("currencyId", capitalInOut.getCurrencyId());
        params.put("userId", capitalInOut.getUserId());
        List<CapitalInfoVO> capitalInfoVOList = capitalMapper.selectByParam(params);
        CapitalInfoVO userCapital = capitalInfoVOList.size() > 0 ? capitalInfoVOList.get(0) : null;
        if (userCapital == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, null);
        }

        if (userCapital.getCapitalFrozen().compareTo(capitalInOut.getNumberCoins()) < 0) {
            BizException.fail(MessageCode.NOT_ENOUGH_FROZEN_MONEY, "用户冻结资金不足");
        }

        //对用户的冻结金额进行操作
        userCapital.setCapitalFrozen(userCapital.getCapitalFrozen().subtract(capitalInOut.getNumberCoins()));
        Integer result1 = capitalMapper.updateByPrimaryKeySelective(userCapital);

        //提币单完结
        capitalInOut.setStatus(1);  //状态 0:审核中 1：成功 2：失败
        capitalInOut.setSendTime(new Date());
        Integer result2 = capitalInOutMapper.updateByPrimaryKeySelective(capitalInOut);

        logger.info("放币处理：userId: "+capitalInOut.getUserId()
                +" 提取 "+ capitalInOut.getNumberCoins().toString()
                +" 币种id "+ capitalInOut.getCurrencyId());

        //记录资金变化
        CapitalLog capitalLog = new CapitalLog();
        capitalLog.setUserId(capitalInOut.getUserId());
        capitalLog.setType(CapitalLogTypeEnum.WITHDRAWALS.getValue().byteValue());
        capitalLog.setCurrencyId(capitalInOut.getCurrencyId());
        capitalLog.setFrozenNumber(capitalInOut.getNumberCoins().negate());
        capitalLog.setComment("提款放币，订单号:" + capitalInOut.getId());
        capitalLog.setOrderId(capitalInOut.getId());
        capitalLog.setCreateTime(new Date());
        capitalLogMapper.insertSelective(capitalLog);

        return result1 * result2 > 0;
    }
}
