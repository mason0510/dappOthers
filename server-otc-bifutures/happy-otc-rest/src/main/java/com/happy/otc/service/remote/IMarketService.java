package com.happy.otc.service.remote;

import com.bitan.common.exception.BizException;
import com.bitan.common.vo.Result;
import com.bitan.market.vo.OriginalExchangeInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient("market-server")
@RequestMapping(value = "/market-rest")
public interface IMarketService {

    @ApiOperation(value = "批量获取实时数据一个点")
    @RequestMapping(value = "/realtime-one-point", method = RequestMethod.GET)
    public Result<List<OriginalExchangeInfoVO>> getRealTimeOnePoint(@ApiParam("市场类型 1市值 2交易对") @RequestParam("marketType") Integer marketType,
                                                                    @ApiParam("币种代码列表") @RequestParam("kindList") List<String> kindList) throws BizException;
}
