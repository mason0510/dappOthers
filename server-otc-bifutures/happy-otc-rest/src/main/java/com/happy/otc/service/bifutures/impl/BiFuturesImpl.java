package com.happy.otc.service.bifutures.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.happy.otc.bifutures.enums.BiFuturesEnum;
import com.happy.otc.bifutures.enums.BiFuturesKindEnum;
import com.happy.otc.bifutures.vo.BiFuturesVo;
import com.happy.otc.service.bifutures.BiFuturesService;
import com.happy.otc.service.bifutures.MetaRiskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
@Service
public class BiFuturesImpl implements BiFuturesService {

    @Autowired
    private MetaRiskContext metaRiskContext;
    @Override
    public List<BiFuturesVo> getBiKinds() {
        List<BiFuturesVo> biFuturesVos = new ArrayList<>();
        BiFuturesVo btc =new BiFuturesVo();
        btc.setBiCode(BiFuturesEnum.BTC.getValue());
        btc.setExplain(BiFuturesEnum.BTC.getMessage());
        biFuturesVos.add(btc);
        BiFuturesVo eth = new BiFuturesVo();
        eth.setBiCode(BiFuturesEnum.ETH.getValue());
        eth.setExplain(BiFuturesEnum.ETH.getMessage());
        biFuturesVos.add(eth);
        BiFuturesVo eos = new BiFuturesVo();
        eos.setExplain(BiFuturesEnum.EOS.getMessage());
        eos.setBiCode(BiFuturesEnum.EOS.getValue());
        biFuturesVos.add(eos);
        BiFuturesVo ltc = new BiFuturesVo();
        ltc.setBiCode(BiFuturesEnum.LTC.getValue());
        ltc.setExplain(BiFuturesEnum.LTC.getMessage());
        biFuturesVos.add(ltc);
        BiFuturesVo xrp = new BiFuturesVo();
        xrp.setExplain(BiFuturesEnum.XRP.getMessage());
        xrp.setBiCode(BiFuturesEnum.XRP.getValue());
        biFuturesVos.add(xrp);
        BiFuturesVo bch = new BiFuturesVo();
        bch.setBiCode(BiFuturesEnum.BCH.getValue());
        bch.setExplain(BiFuturesEnum.BCH.getMessage());
        biFuturesVos.add(bch);
        return biFuturesVos;
    }

    @Override
    public String getRiskDate(String biCode) throws ParserConfigurationException, SAXException, IOException {
        String metaRisk = metaRiskContext.getMetaRisk(BiFuturesKindEnum.getByValue(biCode));
        JSONObject jsonObject = JSON.parseObject(metaRisk);
        String s = jsonObject.toJSONString();
        /*SchemeRisk schemeRisk = metaRisk.findSchemeRiskByType(1);
        Map<String, Object> map = schemeRisk.toObjectMap();
       // JSONObject jsonObject = JSON.parseObject(String.valueOf(map));
        JSONObject jsonObject = new JSONObject(map);*/
        return s;
    }
}
