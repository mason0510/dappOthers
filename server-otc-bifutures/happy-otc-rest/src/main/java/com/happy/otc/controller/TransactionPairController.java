package com.happy.otc.controller;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.bitan.common.vo.PageResult;
import com.bitan.common.vo.Result;
import com.github.pagehelper.PageInfo;
import com.happy.otc.contants.Contants;
import com.happy.otc.entity.Currency;
import com.happy.otc.entity.TransactionPair;
import com.happy.otc.enums.KindEnum;
import com.happy.otc.enums.RelevantKindEnum;
import com.happy.otc.service.ICurrencyService;
import com.happy.otc.service.ITransactionPairService;
import com.happy.otc.vo.ExchangeInfoVO;
import com.happy.otc.vo.TransactionPairVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "交易对Api")
public class TransactionPairController {

    @Autowired
    ITransactionPairService transactionPairService;
    @Autowired
    ICurrencyService currencyService;
    @Autowired
    RedisUtil redisUtil;
    @ApiOperation(value = "交易对列表")
    @RequestMapping(value = "/transaction-pair-list", method = RequestMethod.GET)
    public PageResult<List<TransactionPair>> getTransactionPairList(@ApiParam("现关联种类 CNY，USD")
                                                                          @RequestParam(value = "relevantKind",required = false) String relevantKind,
                                                                    @RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex,
                                                                    @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        BizException.isNull(pageIndex, "pageIndex");
        BizException.isNull(pageSize, "pageSize");
        RelevantKindEnum relevantKindEnum = RelevantKindEnum.getInstance(relevantKind);
        if (Objects.isNull(relevantKindEnum)){
            relevantKind = null;
        }
        PageInfo<TransactionPair> pageInfo =  transactionPairService.getTransactionPairList(null,relevantKind,pageIndex,pageSize);

        return  new PageResult(pageInfo.getList(), pageIndex
                , pageSize, pageInfo.getTotal());
    }

    @ApiOperation(value = "交易对列表")
    @RequestMapping(value = "/transaction-pair-list2", method = RequestMethod.GET)
    public PageResult<List<TransactionPairVO>> getTransactionPairList2(){
        List<Currency> list = currencyService.getCurrencyList();
        List<TransactionPairVO> list2 = new ArrayList<>(  );

        for (Currency c :list){
            TransactionPairVO transactionPairVO = new TransactionPairVO();
            transactionPairVO.setCurrencyId(c.getCurrencyId());
            transactionPairVO.setCurrencyName(c.getCurrencySimpleName());
            list2.add(transactionPairVO);
        }
        return  new PageResult(list2, 1
                , list2.size(),  list2.size());
    }

    @ApiOperation(value = "CAD的汇率保存")
    @RequestMapping(value = "/save-cad-exchange-rate", method = RequestMethod.POST)
    public Result<Boolean> saveExchangeRateRedis(@RequestParam(value = "exchangeRate",required = false) String exchangeRate
                                                 ){
        Double rate = 0D;
        if (!StringUtils.isEmpty(exchangeRate)) {
            redisUtil.setStringValue(Contants.REDIS_CAD_EXCHANGE_RATE, exchangeRate);
        }else{
            String oldRate  = redisUtil.getStringValue(Contants.REDIS_CAD_EXCHANGE_RATE);
            if (StringUtils.isEmpty(oldRate)){
                oldRate = "4.339";
            }
        }
        return Result.createSuccess(true);
    }


    @ApiOperation(value = "获取市场价格")
    @RequestMapping(value = "/realtime-one-point", method = RequestMethod.GET)
    public Result<ExchangeInfoVO> getRealTimeOnePoint(@ApiParam("币种简称 BTC ETH") @RequestParam(value = "kind") String kind){

        KindEnum kindEnum = KindEnum.getInstance(kind);
        if (Objects.isNull(kindEnum)){
            BizException.fail(ApiResponseCode.PARA_ERR," kind");
        }

        ExchangeInfoVO exchangeInfoVO = transactionPairService.getExchangeInfoVO(kind);

        return Result.createSuccess(exchangeInfoVO);
    }
}
