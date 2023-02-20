package com.happy.otc.controller;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.vo.Result;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.vo.BiFuturesOtc;
import com.happy.otc.dto.CapitalInOutDTO;
import com.happy.otc.entity.CapitalInOut;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.enums.CapitalInOutTypeEnum;
import com.happy.otc.enums.CapitalLogTypeEnum;
import com.happy.otc.proto.CapitalInOutInfo;
import com.happy.otc.proto.ResultInfo;
import com.happy.otc.service.ICapitalInOutService;
import com.happy.otc.service.ICapitalService;
import com.happy.otc.service.IUserIdentityService;
import com.happy.otc.service.bifutures.UserAssetsService;
import com.happy.otc.util.ProtoUtils;
import com.happy.otc.vo.CapitalCutRequest;
import com.happy.otc.vo.CapitalInfoVO;
import com.happy.otc.vo.UserBalanceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "用户充币提币详情Api")
public class CapitalInOutController {

    @Autowired
    private ICapitalInOutService capitalInOutService;
    @Autowired
    private ICapitalService capitalService;
    @Autowired
    UserAssetsService userAssetsService;
    @Autowired
    IUserIdentityService userIdentityService;

    @ApiOperation(value = "用户提币")
    @PostMapping(value = "/add-capital-in-out")
    public ResultInfo.ResultBooleanVO addCapitalInOut(@RequestBody CapitalInOutInfo.AddCapitalInOutParamDTO addCapitalInOutDTO){

        BizException.isNull(addCapitalInOutDTO.getAddress(), "address");
        BizException.isNull(addCapitalInOutDTO.getCapitalCipher(), "capitalCipher");
        BizException.isNull(addCapitalInOutDTO.getCurrencyId(), "currencyId");

        Long userId = LoginDataHelper.getUserId();

        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);
        //判断资金密码是否正确
        userIdentityService.checkCapitalCipher(addCapitalInOutDTO.getCapitalCipher(), userIdentity.getCapitalCipher(), userId);

        CapitalInOut capitalInOut = new CapitalInOut();
        capitalInOut.setUserId(userId);
        capitalInOut.setType(CapitalInOutTypeEnum.CAPITAL_OUT.getValue());
        capitalInOut.setCurrencyAddress(addCapitalInOutDTO.getAddress());
        capitalInOut.setNumberCoins(new BigDecimal(addCapitalInOutDTO.getNumberCoins()).setScale(6,BigDecimal.ROUND_DOWN));
        capitalInOut.setCurrencyId(addCapitalInOutDTO.getCurrencyId());
        Boolean result = capitalInOutService.addCapitalInOut(capitalInOut);

        //返回结果
        return ProtoUtils.createBoolSuccess(result);
    }

/*    @ApiOperation(value = "修改用户充币充币记录状态")
    @PostMapping(value = "/update-capital-in-out")
    public ResultInfo.ResultBooleanVO updateCapitalInOut(@RequestBody CapitalInOutInfo.CapitalInOutDTO capitalInOutDTO){

        BizException.isNull(capitalInOutDTO.getCapitalInOutId(), "capitalInOutId");
        BizException.isNull(capitalInOutDTO.getType(), "Type");

        Long userId = LoginDataHelper.getUserId();
        CapitalInOut capitalInOut = new CapitalInOut();
        capitalInOut.setUserId(userId);
        capitalInOut.setType(capitalInOutDTO.getTypeValue());
        capitalInOut.setId(capitalInOutDTO.getCapitalInOutId());
        Boolean result = capitalInOutService.updateCapitalInOut(capitalInOut);

        //返回结果
        return ProtoUtils.createBoolSuccess(result);
    }*/

    @ApiOperation(value = "用户充币提币记录列表")
    @GetMapping(value = "/capital-in-out-list")
    public CapitalInOutInfo.CapitalInOutListResult getCapitalInOutList(@RequestParam("type") Integer type,
                                                                       @RequestParam(value = "pageIndex") Integer pageIndex,
                                                                       @RequestParam(value = "pageSize") Integer pageSize){

        BizException.isNull(pageIndex, "pageIndex");
        BizException.isNull(pageSize, "pageSize");

        Long userId = LoginDataHelper.getUserId();
        CapitalInOut capitalInOutVO = new CapitalInOut();
        capitalInOutVO.setUserId(userId);
        capitalInOutVO.setType(type);
        PageInfo<CapitalInOutDTO> list = capitalInOutService.getCapitalInOutList(capitalInOutVO,pageIndex,pageSize);

        //请求数据的封装
        List<CapitalInOutInfo.CapitalInOutDTO> listDTO = new ArrayList<>();
        CapitalInOutInfo.CapitalInOutDTO.Builder capitalInOutDTO = CapitalInOutInfo.CapitalInOutDTO.newBuilder();
        for (CapitalInOutDTO capitalInOut:list.getList()) {
            capitalInOutDTO.setAddress(capitalInOut.getCurrencyAddress());
            capitalInOutDTO.setUserId(capitalInOut.getUserId());
            capitalInOutDTO.setCreateTime(capitalInOut.getCreateTime().getTime());
            capitalInOutDTO.setCurrencySimpleName(capitalInOut.getCurrencySimpleName());
            capitalInOutDTO.setCurrencyId(capitalInOut.getCurrencyId());
            capitalInOutDTO.setCapitalInOutId(capitalInOut.getId());
            capitalInOutDTO.setSendTime(capitalInOut.getSendTime().getTime());
            capitalInOutDTO.setNumberCoins(capitalInOut.getNumberCoins().toString());
            capitalInOutDTO.setTypeValue(capitalInOut.getType());
            capitalInOutDTO.setStatusValue(capitalInOut.getStatus());
            listDTO.add(capitalInOutDTO.build());
        }

        CapitalInOutInfo.CapitalInOutListResult pageResult = CapitalInOutInfo.CapitalInOutListResult.getDefaultInstance();
        return ProtoUtils.createPageResultSuccess(listDTO, list.getPageNum(), list.getPageSize(), list.getTotal(), pageResult);
    }


    @ApiOperation(value = "用户OTC账户资金划出与划进")
    @PostMapping(value = "/otc-capital-cut-out-in")
    public Result<Boolean> otcCapitalCutOut(@RequestBody CapitalCutRequest capitalCutRequest) throws ParserConfigurationException, SAXException, ParseException, IOException {

        BizException.isNull(capitalCutRequest.getCapitalInOutType(), "资金类别");
        BizException.isNull(capitalCutRequest.getMoney(), "转账金额");
        Long currencyId = 5037L;
        CapitalLogTypeEnum capitalLogTypeEnum = CapitalLogTypeEnum.getInstance(capitalCutRequest.getCapitalInOutType());

        if (capitalLogTypeEnum != CapitalLogTypeEnum.CUT_OUT && capitalLogTypeEnum != CapitalLogTypeEnum.CUT_IN){
            BizException.fail( ApiResponseCode.PARA_ERR, "资金类别错误");
        }
        if (capitalCutRequest.getMoney().compareTo( new BigDecimal( "0.000001" ) ) < 0){
            BizException.fail( ApiResponseCode.PARA_ERR, "资金数量过小");
        }

        Long userId = LoginDataHelper.getUserId();

        //返回结果
        return Result.createSuccess(capitalService.capitalCut(userId,capitalCutRequest.getMoney(),currencyId,capitalLogTypeEnum) > 0);
    }


    @ApiOperation(value = "用户账户余额")
    @GetMapping(value = "/otc-capital")
    public Result<UserBalanceVO> otcCapital() throws ParserConfigurationException, SAXException, ParseException, IOException {

        Long userId = LoginDataHelper.getUserId();
        Long currencyId = 5037L;
        UserBalanceVO userBalanceVO = new UserBalanceVO();
        CapitalInfoVO capitalInfoVO =  capitalService.getCapitalInfoByUserIdAndCurrencyId(userId,currencyId);
        userBalanceVO.setOtcAvailable(capitalInfoVO.getCapitalAvailable().setScale(6, BigDecimal.ROUND_HALF_DOWN));
        BiFuturesOtc userAssets = userAssetsService.getAssetsByUserId(userId);
        userBalanceVO.setFuturesAvailable(userAssets.getPrincipal());

        //返回结果
        return Result.createSuccess(userBalanceVO);
    }
}
