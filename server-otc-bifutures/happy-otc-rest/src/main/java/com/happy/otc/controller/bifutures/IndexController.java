package com.happy.otc.controller.bifutures;

import com.bitan.common.exception.BizException;
import com.bitan.common.vo.Result;
import com.happy.otc.bifutures.enums.BiFuturesKindEnum;
import com.happy.otc.bifutures.vo.BiFuturesInitaVo;
import com.happy.otc.bifutures.vo.BiFuturesVo;
import com.happy.otc.service.bifutures.BiFuturesService;
import com.happy.otc.service.bifutures.UserAssetsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by liuy on 2018\11\14 0014.
 */
@RestController
@RequestMapping(value = "/bi-rest")
@Api(value = "/bi-rest", description = "币期货起始加载数据")
public class IndexController {

    @Value("${max.entrust.amount.random}")
    private Integer maxRandom;
    @Value("${min.entrust.amount.random}")
    private Integer minRandom;
    @Autowired
    BiFuturesService biFuturesService;
    @Autowired
    UserAssetsService userAssetsService;

    @ApiOperation(value = "币期货品种查询")
    @RequestMapping(value = "/getFuturesKind",method = {RequestMethod.GET})
    public Result<List> getFuturesKind(){
        List<BiFuturesVo> biFuturesVoList = biFuturesService.getBiKinds();
        return Result.createSuccess(biFuturesVoList);
    }

    @ApiOperation(value = "币期货所有风控数据")
    @RequestMapping(value = "/getIndexRisk",method = {RequestMethod.GET})
    public Result<BiFuturesInitaVo> getIndexRisk(@ApiParam("币种代码") @RequestParam("biCode") String biCode) throws IOException, SAXException, ParserConfigurationException, ParseException {

        BizException.isNull(biCode,"币的代码不能为空！");
        if(BiFuturesKindEnum.getByValue(biCode)==null){
            return Result.createUnknowFail(501,"币种不存在！");
        }
        //Long userId = LoginDataHelper.getUserId();
        long systemTime = new Date().getTime();
        //BiFuturesOtc userAssets = userAssetsService.getAssetsByUserId(10008l);
        //BigDecimal balance = userAssets == null? null:userAssets.getPrincipal();
        String risk = biFuturesService.getRiskDate(biCode);
        BiFuturesInitaVo biFuturesInitaVo = new BiFuturesInitaVo(systemTime,risk,new BigDecimal(0),maxRandom,minRandom);
        return Result.createSuccess(biFuturesInitaVo);
    }

    @ApiOperation(value = "币期货实时行情数据")
    @RequestMapping(value = "/getRealTime",method = {RequestMethod.GET})
    public Result<BigDecimal> getRealTime(@ApiParam("币种代码") @RequestParam("biCode") String biCode){
        BigDecimal newPrice = userAssetsService.getNewPrice(biCode);
        return Result.createSuccess(newPrice);
    }


}
