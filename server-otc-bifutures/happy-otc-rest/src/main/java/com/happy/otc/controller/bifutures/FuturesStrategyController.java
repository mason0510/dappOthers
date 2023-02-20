package com.happy.otc.controller.bifutures;

import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.vo.Result;
import com.happy.otc.bifutures.CancelReason;
import com.happy.otc.bifutures.dto.FuturesStrategyDto;
import com.happy.otc.bifutures.entity.FuturesStrategy;
import com.happy.otc.bifutures.entity.MaxAndMin;
import com.happy.otc.bifutures.pojo.FuturesStrategyQuery;
import com.happy.otc.bifutures.utill.CollectionUtil;
import com.happy.otc.bifutures.utill.NumberUtil;
import com.happy.otc.bifutures.vo.FuturesStrategyVo;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.service.IUserIdentityService;
import com.happy.otc.service.bifutures.RealStrategyService;
import com.happy.otc.service.bifutures.SimStrategyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by liuy on 2018\11\14 0014.
 */
@RestController
@RequestMapping(value = "/bi-rest")
@Api(value = "/bi-rest", description = "币期货交易流程")
public class FuturesStrategyController {

    @Autowired
    RealStrategyService realStrategyService;
    @Autowired
    SimStrategyService simStrategyService;
    @Autowired
    private IUserIdentityService iUserIdentityService;

    @ApiOperation(value = "币期货发起策略")
    @RequestMapping(value = "/createStrategy",method = {RequestMethod.POST})
    public Result<FuturesStrategyVo> createStrategy(@ApiParam("币种代码") @RequestParam("biCode") String biCode,
                                                    @ApiParam("点买策略方式：1,市价；2,限价") @RequestParam("mold") Integer mold,
                                                    @ApiParam("委托价") @RequestParam("buyPriceOrder") BigDecimal buyPriceOrder,
                                                    @ApiParam("手数") @RequestParam("amount") Integer amount,
                                                    @ApiParam("类型：1,真实；2,模拟") @RequestParam("type") Integer type,
                                                    @ApiParam("方向：B,涨；S,跌") @RequestParam("direction") String direction,
                                                    @ApiParam("保证金") @RequestParam("principal") BigDecimal principal,
                                                    @ApiParam("强评价") @RequestParam("closePrice") BigDecimal closePrice,
                                                    @ApiParam("交易综合费") @RequestParam("serviceCharge") BigDecimal serviceCharge,
                                                    @ApiParam("是否递延1,是；2,否") @RequestParam(value = "isDefer",required = true) Integer isDefer
    ) throws ParserConfigurationException, SAXException, IOException {

       Long userId = LoginDataHelper.getUserId();
       //Long userId = 10075l;
        FuturesStrategyDto futuresStrategyDto = new FuturesStrategyDto();
        futuresStrategyDto.setBiCode(biCode);
        futuresStrategyDto.setUserId(userId);
        futuresStrategyDto.setMold(mold);
        futuresStrategyDto.setBuyPriceOrder(buyPriceOrder);
        if (mold == 1){
            futuresStrategyDto.setBuyPriceDeal(buyPriceOrder);
        }
        futuresStrategyDto.setAmount(amount);
        futuresStrategyDto.setType(type);
        futuresStrategyDto.setDirection(direction);
        futuresStrategyDto.setPrincipal(principal);
        futuresStrategyDto.setClosePrice(closePrice);
        futuresStrategyDto.setServiceCharge(new BigDecimal(numberFormat1(serviceCharge)));
        futuresStrategyDto.setIsDefer(isDefer);
        FuturesStrategyVo result = null;
        if (type ==1){
            result = realStrategyService.createRealStrategy(futuresStrategyDto);
        }else if (type == 2){
            result = simStrategyService.createSimStrategy(futuresStrategyDto);
        }

        return Result.createSuccess(result);
    }

    @ApiOperation(value = "币期货持仓列表")
    @RequestMapping(value = "/getSaleFuturesStrategy",method = {RequestMethod.GET})
    public Result<List> getSaleFuturesStrategy(@ApiParam("策略类型") @RequestParam("type") Integer type,@ApiParam("排序类型") @RequestParam("bbc") String bbc){

        if(!(type==1||type==2)){
            BizException.fail(501,"type类型不存在");
        }
        Long userId = LoginDataHelper.getUserId();
        //Long userId = 10075l;
        FuturesStrategyQuery futuresStrategyQuery = new FuturesStrategyQuery();
        futuresStrategyQuery.setType(type);
        futuresStrategyQuery.setUserId(userId);
        futuresStrategyQuery.setStatus(CollectionUtil.asList(1,2,3,4));
        List<FuturesStrategy> futuresStrategyList = realStrategyService.selectByUserId(futuresStrategyQuery,bbc);
        return Result.createSuccess(futuresStrategyList);
    }

    @ApiOperation(value = "卖出")
    @RequestMapping(value = "/closeStrategy",method = {RequestMethod.POST})
    public Result<String> closeStrategy(@ApiParam("卖出策略id字符串，中间用，隔开") @RequestParam("ids") String ids,
                                      @ApiParam("策略类型") @RequestParam("type") Integer type){

        if(!(type==1||type==2)){
            BizException.fail(501,"type类型不存在");
        }
       Long userId = LoginDataHelper.getUserId();
      // Long userId = 10075l;

        String result = realStrategyService.closeStrategy(ids,userId);
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "设置止盈止损")
    @RequestMapping(value = "/setQuitGainLoss",method = {RequestMethod.POST})
    public Result<Integer> setQuitGainLoss(@ApiParam("策略id") @RequestParam("id") Long id,
                                           @ApiParam("策略类型") @RequestParam("type") Integer type,
                                           @ApiParam("止盈价") @RequestParam(value = "gainPrice",required = false) BigDecimal gainPrice,
                                           @ApiParam("止损价") @RequestParam(value = "lossPrice",required = false) BigDecimal lossPrice){

        if(!(type==1||type==2)){
            BizException.fail(501,"type类型不存在");
        }
        Long userId = LoginDataHelper.getUserId();
       // Long userId = 10075l;

        FuturesStrategyDto futuresStrategyDto = new FuturesStrategyDto();
        futuresStrategyDto.setId(id);
        futuresStrategyDto.setUserId(userId);
        futuresStrategyDto.setGainPrice(gainPrice);
        futuresStrategyDto.setLossPrice(lossPrice);
        Integer result = realStrategyService.setQuitGainLoss(futuresStrategyDto);
        return Result.createSuccess(result);
    }
    @ApiOperation(value = "追加保证金")
    @RequestMapping(value = "/setPrincipal",method = {RequestMethod.POST})
    public Result<Integer> setPrincipal(@ApiParam("策略id") @RequestParam("id") Long id,
                                           @ApiParam("策略类型") @RequestParam("type") Integer type,
                                           @ApiParam("保证金") @RequestParam("principal") BigDecimal principal,
                                           @ApiParam("强平价") @RequestParam("closePrice") BigDecimal closePrice){

        if(!(type==1||type==2)){
            BizException.fail(501,"type类型不存在");
        }
        if(principal.compareTo(new BigDecimal(0))<=0){
            BizException.fail(501,"追加保证金额必须大于零");
        }
       Long userId = LoginDataHelper.getUserId();
        //Long userId = 10075l;

        FuturesStrategyDto futuresStrategyDto = new FuturesStrategyDto();
        futuresStrategyDto.setId(id);
        futuresStrategyDto.setUserId(userId);
        futuresStrategyDto.setPrincipal(principal);
        futuresStrategyDto.setClosePrice(closePrice);
        Integer result = realStrategyService.setPrincipal(futuresStrategyDto);
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "根据id查询策略详情")
    @RequestMapping(value = "/selectStrategyById",method = {RequestMethod.POST})
    public Result<FuturesStrategy> selectStrategyById(@ApiParam("策略id") @RequestParam("id") Long id,
                                        @ApiParam("策略类型") @RequestParam("type") Integer type){

        if(!(type==1||type==2)){
            BizException.fail(501,"type类型不存在");
        }

        FuturesStrategy result = realStrategyService.selectById(id);
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "撤单")
    @RequestMapping(value = "/cancelStrategy",method = {RequestMethod.POST})
    public Result<Integer> cancelStrategy(@ApiParam("策略id") @RequestParam("id") Long id,
                                                      @ApiParam("策略类型") @RequestParam("type") Integer type){

        if(!(type==1||type==2)){
            BizException.fail(501,"type类型不存在");
        }

        Long userId = LoginDataHelper.getUserId();
       // Long userId = 10004l;
        FuturesStrategyDto futuresStrategyDto = new FuturesStrategyDto();
        futuresStrategyDto.setId(id);
        futuresStrategyDto.setUserId(userId);
        futuresStrategyDto.setStatus(4);
        futuresStrategyDto.setCancelTime(new Date());
        futuresStrategyDto.setCancelReason(CancelReason.ACTIVE_CANCEL);
        Integer result = realStrategyService.cancelStrategy(futuresStrategyDto);
        return Result.createSuccess(result);
    }

    @ApiOperation(value = " 是否递延")
    @RequestMapping(value = "/setIsDefer",method = {RequestMethod.POST})
    public Result<Integer> setIsDefer(@ApiParam("策略id") @RequestParam("id") Long id,
                                      @ApiParam("是否递延") @RequestParam("isDefer") Integer isDefer,
                                      @ApiParam("策略类型") @RequestParam("type") Integer type){

        if(!(type==1||type==2)){
            BizException.fail(501,"type类型不存在");
        }
        Long userId = LoginDataHelper.getUserId();
        //Long userId = 10004l;

        FuturesStrategyDto futuresStrategyDto = new FuturesStrategyDto();
        futuresStrategyDto.setId(id);
        futuresStrategyDto.setUserId(userId);
        futuresStrategyDto.setIsDefer(isDefer);
        Integer result = realStrategyService.setIsDefer(futuresStrategyDto);
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "获取单个用户单个合约买涨买跌手数")
    @RequestMapping(value = "/getAmount",method = {RequestMethod.POST})
    public Result<MaxAndMin> getAmount(@ApiParam("币种代码") @RequestParam("biCode") String biCode,
                                     @ApiParam("用户id") @RequestParam("userId") Long userId){

        UserIdentity userRealInfo = iUserIdentityService.getUserExtendIdentity(userId);
        if(userRealInfo == null){
            return Result.createUnknowFail(501,"无该用户信息！");
        }

        FuturesStrategyQuery futuresStrategyQuery = new FuturesStrategyQuery();
        futuresStrategyQuery.setStatus(CollectionUtil.asList(1,2));
        futuresStrategyQuery.setUserId(userId);
        futuresStrategyQuery.setBiCode(biCode);
      MaxAndMin result =  realStrategyService.getAmount(futuresStrategyQuery);
        return Result.createSuccess(result);
    }
    public String numberFormat1(BigDecimal number) {
        return NumberUtil.format(number, "#,##0.000");
    }
}
