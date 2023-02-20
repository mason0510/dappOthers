package com.happy.otc.service.bifutures.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.happy.otc.bifutures.dto.FuturesStrategyDto;
import com.happy.otc.bifutures.entity.Params;
import com.happy.otc.bifutures.enums.BiFuturesKindEnum;
import com.happy.otc.bifutures.pojo.SchemeRisk;
import com.happy.otc.bifutures.utill.NumberUtil;
import com.happy.otc.dao.BiFuturesParamMapper;
import com.happy.otc.service.bifutures.MetaRiskContext;
import com.happy.otc.service.bifutures.MetaRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018\11\20 0020.
 */
@Service
public class MetaRiskServiceImpl implements MetaRiskService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    BiFuturesParamMapper biFuturesParamMapper;

    @Autowired
    private MetaRiskContext metaRiskContext;

    @Override
    public String getMetaRisk(String futuresXmlRiskName) {

            Params param = biFuturesParamMapper.getByName(futuresXmlRiskName);

            return param.getValue();

        }


    @Override
    public SchemeRisk getSchemeRisk(String biCode) throws ParserConfigurationException, SAXException, IOException {
       /* MetaRisk metaRisk = metaRiskContext.getMetaRisk(BiFuturesKindEnum.getByValue(biCode));
        SchemeRisk schemeRisk = metaRisk.findSchemeRiskByType(1);*/
        return null;
    }

    @Override
    public void checkCreateValue(FuturesStrategyDto dto) throws ParserConfigurationException, SAXException, IOException {
        BizException.isNull(dto.getBuyPriceOrder(),"买入价不能为空！");
        BizException.isNull(dto.getAmount(),"手数不能为空！");
        BizException.isNull(dto.getType(),"策略类型不能为空！");
        BizException.isNull(dto.getMold(),"策略方式不能为空！");
        BizException.isNull(dto.getPrincipal(),"保证金不能为空！");
        BizException.isNull(dto.getServiceCharge(),"服务综合费不能为空！");
        BizException.isNull(dto.getClosePrice(),"强平价不能为空！");

        /*MetaRisk metaRisk = metaRiskContext.getMetaRisk(BiFuturesKindEnum.getByValue(dto.getBiCode()));
        SchemeRisk schemeRisk = metaRisk.findSchemeRiskByType(1);*/
        String risk = metaRiskContext.getMetaRisk(BiFuturesKindEnum.getByValue(dto.getBiCode()));
        JSONObject jsonObject = JSON.parseObject(risk);
        String futuresName =jsonObject.getJSONObject("futuresName").getString("value");
        String cancelPrice =jsonObject.getJSONObject("cancelPriceRatio").getString("value");
        String serviceCharge = jsonObject.getJSONObject("serviceChargeRatio").getString("value");
        String deferCharge = jsonObject.getJSONObject("deferChargeRatio").getString("value");
        String serviceChargeRabate = jsonObject.getJSONObject("serviceChargeRabate").getString("value");
        String contractValue = jsonObject.getJSONObject("contractValue").getString("value");
        String maxContract = jsonObject.getJSONObject("isTrade").getString("value");

        if (!maxContract.equals("1")){
            BizException.fail(999,"暂停交易");
        }
        if (!dto.getBiCode().equals(futuresName)){
            BizException.fail(501,"币的代码错误");
        }
        dto.setContractValue(new BigDecimal(contractValue));

        BigDecimal close = BigDecimal.ZERO;
        if (dto.getDirection().equals("B")){
            // close =dto.getBuyPriceOrder().subtract(dto.getPrincipal().multiply(new BigDecimal(cancelPrice)));
            if (dto.getBiCode().equals("EOS")||dto.getBiCode().equals("XRP")){
                close =dto.getBuyPriceOrder().subtract(dto.getPrincipal().divide(dto.getContractValue()).divide(new BigDecimal(dto.getAmount()))
                        .multiply(new BigDecimal(cancelPrice))).setScale(4,BigDecimal.ROUND_HALF_UP);
            }else {
                close =dto.getBuyPriceOrder().subtract(dto.getPrincipal().divide(dto.getContractValue()).divide(new BigDecimal(dto.getAmount()))
                        .multiply(new BigDecimal(cancelPrice))).setScale(2,BigDecimal.ROUND_HALF_UP);
            }
            if(close.compareTo(dto.getClosePrice())!=0){
                BizException.fail(501,"买涨强平价错误");
            }
        }else if (dto.getDirection().equals("S")){
            //close = dto.getBuyPriceOrder().add(dto.getPrincipal().multiply(new BigDecimal(cancelPrice)));
            if (dto.getBiCode().equals("EOS")||dto.getBiCode().equals("XRP")){
                close = dto.getBuyPriceOrder().add(dto.getPrincipal().divide(dto.getContractValue()).divide(new BigDecimal(dto.getAmount()))
                        .multiply(new BigDecimal(cancelPrice))).setScale(4,BigDecimal.ROUND_HALF_UP);
            }else {
                close = dto.getBuyPriceOrder().add(dto.getPrincipal().divide(dto.getContractValue()).divide(new BigDecimal(dto.getAmount()))
                        .multiply(new BigDecimal(cancelPrice))).setScale(2,BigDecimal.ROUND_HALF_UP);
            }
            if (close.compareTo(dto.getClosePrice())!=0){
                BizException.fail(501,"买跌强平价错误");
            }
        }

        BigDecimal ser = BigDecimal.ZERO;

        if (dto.getBiCode().equals("EOS")||dto.getBiCode().equals("XRP")){
            ser = new BigDecimal(dto.getAmount()).multiply(new BigDecimal(contractValue)).multiply(dto.getBuyPriceOrder()).
                    multiply(new BigDecimal(serviceCharge)).multiply(new BigDecimal(serviceChargeRabate)).divide(new BigDecimal(10)).setScale(4,BigDecimal.ROUND_HALF_UP);
        }else {
            ser = new BigDecimal(dto.getAmount()).multiply(new BigDecimal(contractValue)).multiply(dto.getBuyPriceOrder()).
                    multiply(new BigDecimal(serviceCharge)).multiply(new BigDecimal(serviceChargeRabate)).divide(new BigDecimal(10)).setScale(4,BigDecimal.ROUND_HALF_UP);
        }
        if (new BigDecimal(numberFormat1(ser)).compareTo(dto.getServiceCharge())!=0){
            BizException.fail(501,"交易综合费错误");
        }
        /*BigDecimal defer = new BigDecimal(dto.getAmount()).multiply(new BigDecimal(contractValue)).multiply(dto.getBuyPriceOrder()).
                multiply(new BigDecimal(deferCharge));
        if (dto.getIsDefer()==1) {
            if (defer.compareTo(dto.getDeferCharge()) != 0) {
                BizException.fail(501, "递延费错误");
            }
        }*/

    }
    public String numberFormat1(BigDecimal number) {
        return NumberUtil.format(number, "#,##0.000");
    }
}
