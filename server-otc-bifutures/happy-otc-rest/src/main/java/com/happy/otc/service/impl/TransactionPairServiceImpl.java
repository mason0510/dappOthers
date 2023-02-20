package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.HttpClientUtil;
import com.bitan.common.utils.RedisUtil;
import com.bitan.market.vo.OriginalExchangeInfoVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;
import com.happy.otc.contants.Contants;
import com.happy.otc.dao.TransactionPairMapper;
import com.happy.otc.entity.TransactionPair;
import com.happy.otc.enums.KindEnum;
import com.happy.otc.enums.RelevantKindEnum;
import com.happy.otc.service.ITransactionPairService;
import com.happy.otc.service.remote.IMarketService;
import com.happy.otc.vo.ExchangeInfoVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class TransactionPairServiceImpl implements ITransactionPairService {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    TransactionPairMapper transactionPairMapper;
    @Autowired
    IMarketService marketService;

    @Value("${markert.price.url}")
    private String markertPriceUrl;

    @Transactional
    @Override
    public PageInfo<TransactionPair> getTransactionPairList(String kind, String relevantKind, Integer pageIndex, Integer pageSize){
        Map<String, Object> params = new HashMap<>();
        params.put("relevantKind",relevantKind);
        params.put("kind",kind);
        PageHelper.startPage(pageIndex, pageSize, Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<TransactionPair> list = transactionPairMapper.selectByParam(params);
        PageInfo pageInfo = new PageInfo(list);
        return  pageInfo;
    }

    @Override
    public Double getRealTimeOnePoint(KindEnum kindEnum, RelevantKindEnum relevantKindEnum){
        Double price = 0.0D;
        OriginalExchangeInfoVO originalExchangeInfoVO =  getMarkertPrice(kindEnum.getValue());

        if (relevantKindEnum.getValue().equalsIgnoreCase(RelevantKindEnum.CNY.getValue())){
            price = originalExchangeInfoVO.getPriceCNY();
        }else {
            price = originalExchangeInfoVO.getPriceUSD();
        }

        if (relevantKindEnum == RelevantKindEnum.CAD){
            String cadRate  = redisUtil.getStringValue(Contants.REDIS_CAD_EXCHANGE_RATE);
            if (StringUtil.isEmpty(cadRate)){
                cadRate ="5.22";
            }
            BigDecimal bigDecimal =  new BigDecimal(originalExchangeInfoVO.getPriceCNY()).divide(new BigDecimal(cadRate), 8, BigDecimal.ROUND_HALF_UP);
            price = bigDecimal.doubleValue();
        }
        return  price;
    }

    @Override
    public OriginalExchangeInfoVO getMarkertPrice(String kind){
        String key =  Contants.REDIS_OTC_PREFIX_CURRENCY_NETWORK_+kind;
        OriginalExchangeInfoVO originalExchangeInfoVO = null;
        String batchInsertRedisDTOList =  redisUtil.getStringValue(key);
        if (StringUtils.isEmpty(batchInsertRedisDTOList)){
            List<String> kindList = new ArrayList<>();
            kindList.add(kind.trim());

           // Result<List<OriginalExchangeInfoVO>> result = marketService.getRealTimeOnePoint(1,kindList);
            String url = markertPriceUrl +kindList.get( 0 );

            Map<String,String> param = new HashMap<>();
            param.put("applicationId","2");
            param.put("applicationClientType","2");
            param.put("token","data");
            String result = null;
            try {
                result = HttpClientUtil.getOne(url,String.class,param);
            } catch (IOException e) {
                BizException.fail(ApiResponseCode.EXTERNAL_SERVICE__EXP,"市场服务器异常");
            }

            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject.get("code").toString().compareTo("0") == 0){
                List<OriginalExchangeInfoVO> list = JSONObject.parseArray(jsonObject.get("data").toString(), OriginalExchangeInfoVO.class);
                originalExchangeInfoVO = (OriginalExchangeInfoVO)list.get(0);

            }else {
                BizException.fail(ApiResponseCode.EXTERNAL_SERVICE__EXP,"市场服务器异常");
            }

            redisUtil.setStringValue(key,JSONObject.toJSONString(originalExchangeInfoVO),5);
            return  originalExchangeInfoVO;
        }else {
            originalExchangeInfoVO = JSONObject.parseObject(batchInsertRedisDTOList, OriginalExchangeInfoVO.class);
            return  originalExchangeInfoVO;
        }
    }

    @Override
    public List<OriginalExchangeInfoVO> getExchangeInfoList(List<String> kind){

        String kindList = "";
        for (String v: kind ) {
            kindList += v + ",";
        }
        kindList = kindList.substring( 0, kindList.lastIndexOf( "," ));
        //需要获取交易对的值
        String url = markertPriceUrl.replace( "=1","=2" ) +kindList;

        Map<String,String> param = new HashMap<>();
        param.put("applicationId","2");
        param.put("applicationClientType","2");
        param.put("token","data");
        String result = null;
        try {
            result = HttpClientUtil.getOne(url,String.class,param);
        } catch (IOException e) {
            BizException.fail(ApiResponseCode.EXTERNAL_SERVICE__EXP,"市场服务器异常");
        }

        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.get("code").toString().compareTo("0") == 0){
            List<OriginalExchangeInfoVO> list = JSONObject.parseArray(jsonObject.get("data").toString(), OriginalExchangeInfoVO.class);
            return  list;
        }else {
            BizException.fail(ApiResponseCode.EXTERNAL_SERVICE__EXP,"市场服务器异常");
        }

        return  null;
    }

    @Override
    public ExchangeInfoVO getExchangeInfoVO(String kind){

        OriginalExchangeInfoVO originalExchangeInfoVO = getMarkertPrice(kind);
        ExchangeInfoVO exchangeInfoVO = new  ExchangeInfoVO();
        exchangeInfoVO.setPriceCNY(originalExchangeInfoVO.getPriceCNY());
        exchangeInfoVO.setPriceUSD(originalExchangeInfoVO.getPriceUSD());
        String cadRate  = redisUtil.getStringValue(Contants.REDIS_CAD_EXCHANGE_RATE);
        if (StringUtil.isEmpty(cadRate)){
            cadRate ="5.22";
        }
        BigDecimal bigDecimal =  new BigDecimal(originalExchangeInfoVO.getPriceCNY()).divide(new BigDecimal(cadRate), 8, BigDecimal.ROUND_HALF_UP);
        exchangeInfoVO.setPriceCAD(bigDecimal.doubleValue());
        exchangeInfoVO.setCurrentTime(new Date());

        return exchangeInfoVO;
    }

    public static void main(String[] args) throws IOException {
        //String url ="http://api.bitane.io/market/market-rest/realtime-one-point?marketType=1&kindList=BTC";
        List<String> kindList = new ArrayList<>();
        kindList.add("BTC");
        String url ="http://183.129.150.2:8781/market-rest/realtime-one-point?marketType=1&kindList="+kindList.get( 0 );
        Map<String,String> param = new HashMap<>();
        param.put("applicationId","2");
        param.put("applicationClientType","2");
        param.put("token","data");
        String result = HttpClientUtil.getOne(url,String.class,param);

        JSONObject jsonObject = JSONObject.parseObject(result);
        System.out.println(jsonObject);
//        if (jsonObject.get("code").toString().compareTo("0") == 0){
//            List<OriginalExchangeInfoVO> list = JSONObject.parseArray(jsonObject.get("data").toString(), OriginalExchangeInfoVO.class);
//            //return; list.get(0).getKindCode();
//        }else {
//            BizException.fail(ApiResponseCode.EXTERNAL_SERVICE__EXP,"市场服务器异常");
//        }
    }
}
