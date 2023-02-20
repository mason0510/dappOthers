package com.happy.otc.controller.bifutures;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.DateUtil;
import com.bitan.common.vo.PageResult;
import com.bitan.common.vo.Result;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.entity.FuturesStrategy;
import com.happy.otc.bifutures.pojo.FuturesStrategyQuery;
import com.happy.otc.bifutures.pojo.TimeQuery;
import com.happy.otc.bifutures.pojo.WarningRatio;
import com.happy.otc.bifutures.utill.ExcelUtil;
import com.happy.otc.bifutures.utill.NumberUtil;
import com.happy.otc.bifutures.vo.AdminAssesInfo;
import com.happy.otc.bifutures.vo.BiFuturesUserInfo;
import com.happy.otc.bifutures.vo.TotalAssetsVo;
import com.happy.otc.service.bifutures.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by liuy on 2018\11\14 0014.
 */
@RestController
@RequestMapping(value = "/bi-rest")
@Api(value = "/bi-rest", description = "币期货后台管理流程")
public class BiFuturesAdminController {

    @Autowired
    RealStrategyService realStrategyService;
    @Autowired
    SimStrategyService simStrategyService;
    @Autowired
    UserAssetsService userAssetsService;
    @Autowired
    AdminStrategyService adminStrategyService;
    @Autowired
    private MetaRiskContext metaRiskContext;


    @ApiOperation(value = "查询后台订单")
    @RequestMapping(value = "/getStrategyByQuery",method = {RequestMethod.POST})
    public PageResult<FuturesStrategy> getStrategyByQuery( @ApiParam("交易单号") @RequestParam(value = "strategyId",required = false) Long strategyId,
                                            @ApiParam("委托用户") @RequestParam(value = "userId",required = false) Long userId,
                                            @ApiParam("品种") @RequestParam(value = "biCode",required = false) String biCode,
                                            @ApiParam("状态") @RequestParam(value = "status",required = false) Integer status,
                                            @ApiParam("发起开始时间") @RequestParam(value = "leInitiateTime",required = false) String leInitiateTime,
                                            @ApiParam("发起结束时间") @RequestParam(value = "geInitiateTime",required = false) String geInitiateTime,
                                            @ApiParam("平仓开始时间") @RequestParam(value = "leSellTime",required = false) String leSellTime,
                                            @ApiParam("平仓结束时间") @RequestParam(value = "geSellTime",required = false) String geSellTime,
                                            @RequestParam(value = "pageIndex",required = false) Integer pageIndex,
                                            @RequestParam(value = "pageSize",required = false) Integer pageSize
    ) throws ParseException {
        Date leInitiateTimes = null;
        Date geInitiateTimes= null;
        Date leSellTimes = null;
        Date geSellTimes = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isNotBlank(leInitiateTime)){
            leInitiateTimes = getStartTime(dateFormat.parse(leInitiateTime));
        }
        if (StringUtils.isNotBlank(geInitiateTime)){
            geInitiateTimes = getTailDay(dateFormat.parse(geInitiateTime));
        }
        if (StringUtils.isNotBlank(leSellTime)){
            leSellTimes = getStartTime(dateFormat.parse(leSellTime));
        }
       if (StringUtils.isNotBlank(geSellTime)){
           geSellTimes = getTailDay(dateFormat.parse(geSellTime));
       }

        FuturesStrategyQuery futuresStrategyQuery = new FuturesStrategyQuery();
        futuresStrategyQuery.setId(strategyId);
        futuresStrategyQuery.setUserId(userId);
        futuresStrategyQuery.setStatu(status);
        if (StringUtils.isNotBlank(biCode)){
            futuresStrategyQuery.setBiCode(biCode);
        }
        futuresStrategyQuery.setLeInitiateTime(leInitiateTimes);
        futuresStrategyQuery.setGeInitiateTime(geInitiateTimes);
        futuresStrategyQuery.setLeSellTime(leSellTimes);
        futuresStrategyQuery.setGeSellTime(geSellTimes);
        PageInfo<FuturesStrategy> pageList = realStrategyService.selectByPage(futuresStrategyQuery,
                pageIndex==null?1:pageIndex,pageSize==null?10:pageSize);
        return new PageResult(pageList.getList(), pageList.getPageNum()
                , pageList.getPageSize(), pageList.getTotal());
    }

    @ApiOperation(value = "根据id查询策略详情")
    @RequestMapping(value = "/getStrategyById",method = {RequestMethod.POST})
    public Result<FuturesStrategy> selectStrategyById(@ApiParam("策略id") @RequestParam("id") Long id
                                                      ){

        FuturesStrategy result = realStrategyService.selectById(id);
        if (result == null){
            BizException.fail(501,"id错误，策略不存在");
        }
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "根据用户id查询用户信息")
    @RequestMapping(value = "/getUserInfoById",method = {RequestMethod.POST})
    public Result<BiFuturesUserInfo> getUserInfoById(@ApiParam("userId") @RequestParam("userId") Long userId
    ){

        BiFuturesUserInfo result = userAssetsService.getUserInfoById(userId);
        if (result == null){
            BizException.fail(501,"id错误，用户不存在");
        }
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "根据用户id查询用户资金信息")
    @RequestMapping(value = "/getUserAssesInfoById",method = {RequestMethod.POST})
    public Result<AdminAssesInfo> getUserAssesInfoById(@ApiParam("userId") @RequestParam("userId") Long userId
    ){

        AdminAssesInfo result = adminStrategyService.getUserAsses(userId);
        if (result == null){
            BizException.fail(501,"id错误，用户不存在");
        }
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "报表信息")
    @RequestMapping(value = "/getInfo",method = {RequestMethod.POST})
    public Result<TotalAssetsVo> getInfo( @ApiParam("开始时间") @RequestParam(value = "leSellTime",required = false) String leSellTime,
                                          @ApiParam("结束时间") @RequestParam(value = "geSellTime",required = false) String geSellTime){


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date leSel = null;
        Date geSell =null;
        try {
            if (StringUtils.isNotBlank(leSellTime)){
                leSel =getStartTime(dateFormat.parse(leSellTime));
            }
             if (StringUtils.isNotBlank(geSellTime)){
                 geSell = getTailDay(dateFormat.parse(geSellTime));
             }

        }catch (Exception e){

        }

        TimeQuery timeQuery = new TimeQuery();
        timeQuery.setLeSellTime(leSel);
        timeQuery.setGeSellTime(geSell);
        TotalAssetsVo result = adminStrategyService.getInfo(timeQuery);
        if (result == null){
            BizException.fail(999,"信息为空");
        }
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "后台获取风控参数")
    @RequestMapping(value = "/getWaringRisk",method = {RequestMethod.GET})
    public Result<WarningRatio> getWaringRisk() {

       WarningRatio warningRatio = adminStrategyService.getWaring();
        return Result.createSuccess(warningRatio);
    }

    @ApiOperation(value = "后台修改风控参数")
    @RequestMapping(value = "/updateRisk",method = {RequestMethod.POST})
    public Result<Integer> updateRisk(@ApiParam("参数名") @RequestParam("name") String name,
                                      @ApiParam("参数值") @RequestParam("value") String value,
                                      @ApiParam("biCode") @RequestParam("biCode") String biCode
    ) throws IOException, SAXException, ParserConfigurationException {

        Integer result = adminStrategyService.updateRisk(name,value,biCode);
        if (result == null){
            BizException.fail(501,"未得到风控参数");
        }
        return Result.createSuccess(result);
    }

    @ApiOperation(value = "导出excel")
    @RequestMapping(value = "/exportExcel",method = {RequestMethod.POST})
    public void exportExcel(HttpServletResponse resp, @ApiParam("交易单号") @RequestParam(value = "strategyId",required = false) Long strategyId,
                                                   @ApiParam("委托用户") @RequestParam(value = "userId",required = false) Long userId,
                                                   @ApiParam("品种") @RequestParam(value = "biCode",required = false) String biCode,
                                                   @ApiParam("状态") @RequestParam(value = "status",required = false) Integer status,
                                                   @ApiParam("发起开始时间") @RequestParam(value = "leInitiateTime",required = false) String leInitiateTime,
                                                   @ApiParam("发起结束时间") @RequestParam(value = "geInitiateTime",required = false) String geInitiateTime,
                                                   @ApiParam("平仓开始时间") @RequestParam(value = "leSellTime",required = false) String leSellTime,
                                                   @ApiParam("平仓结束时间") @RequestParam(value = "geSellTime",required = false) String geSellTime
    )  {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date leInitiate = null;
        Date geInitiate =null;
        Date leSel = null;
        Date geSell =null;
        try {
            if (StringUtils.isNotBlank(leInitiateTime)){
                leInitiate =getStartTime(dateFormat.parse(leInitiateTime));
            }
            if (StringUtils.isNotBlank(geInitiateTime)){
                geInitiate = getTailDay(dateFormat.parse(geInitiateTime));
            }
            if (StringUtils.isNotBlank(leSellTime)){
                leSel =getStartTime(dateFormat.parse(leSellTime));
            }
            if (StringUtils.isNotBlank(geSellTime)){
                geSell = getTailDay(dateFormat.parse(geSellTime));
            }

        }catch (Exception e){

        }

        int page = 1, pageSize = 2000;
        FuturesStrategyQuery futuresStrategyQuery = new FuturesStrategyQuery();
        futuresStrategyQuery.setId(strategyId);
        futuresStrategyQuery.setUserId(userId);
        //futuresStrategyQuery.setStatus(CollectionUtil.asList(status));
        futuresStrategyQuery.setStatu(status);
        if (StringUtils.isNotBlank(biCode)){
            futuresStrategyQuery.setBiCode(biCode);
        }
        futuresStrategyQuery.setLeInitiateTime(leInitiate);
        futuresStrategyQuery.setGeInitiateTime(geInitiate);
        futuresStrategyQuery.setLeSellTime(leSel);
        futuresStrategyQuery.setGeSellTime(geSell);
        XSSFWorkbook workbook = ExcelUtil.getXSSFWorkbook();
        Sheet sheet = ExcelUtil.getXSSFSheet(workbook, "合约交易列表");
        try{
            PageInfo<FuturesStrategy> pageList = realStrategyService.selectByPage(futuresStrategyQuery,page,pageSize);
            List<JSONObject> result = makeForJSONList(pageList);
            adminStrategyService.exportToExcel(sheet, pageList, result);
            while (page <pageList.getTotal()) {
                page++;
                pageList = realStrategyService.selectByPage(futuresStrategyQuery,page,pageSize);
                result = makeForJSONList(pageList);
                adminStrategyService.exportToExcel(sheet, pageList, result);
            }
            resp.reset();
            resp.setContentType("application/vnd.ms-excel");
            resp.setHeader("content-disposition",
                    "attachment;filename=FUTURES_STRATEGY_LIST" + DateUtil.format(new Date(), "yyMMddHHmmss") + ".xlsx");
            ExcelUtil.write2OutputStream(workbook, resp.getOutputStream());
            resp.flushBuffer();
        }catch (IllegalArgumentException e){

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                IOUtils.closeQuietly(resp.getOutputStream());
            } catch (IOException e) {

            }
        }

    }
    private List<JSONObject> makeForJSONList(PageInfo<FuturesStrategy> strategy){

        List<JSONObject> result  = new ArrayList<JSONObject>();
        for(FuturesStrategy item : strategy.getList()){
            JSONObject jsonItem = JSON.parseObject(JSON.toJSONString(item));

            //格式化需要导出的数据
            jsonItem.put("buyOrderTime", DateUtil.format(item.getBuyOrderTime(),("yy-MM-dd HH:mm:ss")));
            //jsonItem.put("liquidateTime",DateUtil.format(item.getLiquidateTime(),("yy-MM-dd HH:mm:ss")));
            if(null == item.getBuyPriceDeal())
            {
                jsonItem.put("buyPriceDeal", "--");
            }
            else
            {
                jsonItem.put("buyPriceDeal", NumberUtil.format(item.getBuyPriceDeal(), "#,##0.##"));
            }
            jsonItem.put("principal", NumberUtil.format(item.getPrincipal(), "#,##.##"));
            jsonItem.put("profit", NumberUtil.format(item.getProfit(), "#,##0.###"));
            jsonItem.put("closePrice", NumberUtil.format(item.getClosePrice(), "#,##.##"));
            jsonItem.put("gainPrice", NumberUtil.format(item.getGainPrice(), "#,##.##"));
            jsonItem.put("lossPrice", NumberUtil.format(item.getLossPrice(), "#,##.##"));
            result.add(jsonItem);
        }
        return result;
    }

    private static Date getStartTime(Date date) {
        if (date != null){
            Calendar cl = Calendar.getInstance();
            cl.setTime(date);
            cl.add(Calendar.DAY_OF_MONTH, -1);
            cl.set(Calendar.HOUR, 16);
            cl.set(Calendar.MINUTE, 0);
            cl.set(Calendar.SECOND, 0);
            cl.set(Calendar.MILLISECOND, 0);
            return cl.getTime();
        }else {
            return date;
        }

    }
    public static Date getTailDay(Date date) {
        if(date != null) {
            Calendar cl = Calendar.getInstance();
            cl.setTime(date);
            cl.set(Calendar.HOUR, 15);
            cl.set(Calendar.MINUTE, 59);
            cl.set(Calendar.SECOND, 59);
            cl.set(Calendar.MILLISECOND, 999);
            return cl.getTime();
        } else {
            return date;
        }
    }


}
