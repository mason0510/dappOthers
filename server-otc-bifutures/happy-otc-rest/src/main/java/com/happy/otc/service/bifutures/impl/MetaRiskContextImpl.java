package com.happy.otc.service.bifutures.impl;

import com.happy.otc.bifutures.SystemConstants;
import com.happy.otc.bifutures.enums.BiFuturesKindEnum;
import com.happy.otc.bifutures.pojo.MetaRisk;
import com.happy.otc.bifutures.pojo.RiskMetaConstants;
import com.happy.otc.bifutures.utill.XmlTools;
import com.happy.otc.dao.BiFuturesParamMapper;
import com.happy.otc.service.bifutures.MetaRiskContext;
import com.happy.otc.service.bifutures.MetaRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
@Service
public class MetaRiskContextImpl implements MetaRiskContext {

    @Autowired
    private MetaRiskService metaRiskService;
    @Autowired
    BiFuturesParamMapper biFuturesParamMapper;

    private Map<String,MetaRisk> metaFuturesRisks = new ConcurrentHashMap<String, MetaRisk>();

    /**
     * xml 参数表保存的风险数据
     */
    private Map<String,String> xmlFuturesRiskCaches = new ConcurrentHashMap<String, String>();

    @Override
    public String getMetaRisk(BiFuturesKindEnum biFuturesKindEnum) throws IOException, SAXException, ParserConfigurationException {
        if (biFuturesKindEnum == null){
            return null;
        }
        String metaRisk = null;
        if (BiFuturesKindEnum.BTC == biFuturesKindEnum){

            return biFuturesParamMapper.getByName(SystemConstants.RISK_XML_DATA_BTC).getValue();
        }
        if (BiFuturesKindEnum.BCH == biFuturesKindEnum){
            return biFuturesParamMapper.getByName(SystemConstants.RISK_XML_DATA_BCH).getValue();
        }
        if (BiFuturesKindEnum.EOS == biFuturesKindEnum){
            return biFuturesParamMapper.getByName(SystemConstants.RISK_XML_DATA_EOS).getValue();

        }
        if (BiFuturesKindEnum.ETH == biFuturesKindEnum){
            return biFuturesParamMapper.getByName(SystemConstants.RISK_XML_DATA_ETH).getValue();
        }
        if (BiFuturesKindEnum.LTC == biFuturesKindEnum){
            return biFuturesParamMapper.getByName(SystemConstants.RISK_XML_DATA_LTC).getValue();
        }
        if (BiFuturesKindEnum.XRP == biFuturesKindEnum){
            return biFuturesParamMapper.getByName(SystemConstants.RISK_XML_DATA_XRP).getValue();

        }
        return metaRisk;
    }
    /**
     * 读取数据
     *
     * @param stream
     */
    private MetaRisk readFromStream(InputStream stream, boolean isCopy) throws IOException, SAXException, ParserConfigurationException {
        Document doc = XmlTools.parseDoc( stream);
        return buildModel(doc, isCopy);
    }
    /**
     * 更新期货数据
     */
    private MetaRisk updateDataIfNeeded(String futuresXmlRiskName) throws ParserConfigurationException, SAXException, IOException {
        String xmlRisk = metaRiskService.getMetaRisk(futuresXmlRiskName);
        String xmlRiskCache = xmlFuturesRiskCaches.get(futuresXmlRiskName);

        //当参数有更新时，重新读取风控参数文件
        if (xmlRisk != null && (xmlRiskCache == null || xmlRisk.hashCode() != xmlRiskCache.hashCode())) {
            synchronized (this) {
                String xmlRiskAgain =  metaRiskService.getMetaRisk(futuresXmlRiskName);
                if (xmlRiskAgain != null && (xmlRiskCache == null || xmlRiskAgain.hashCode() != xmlRiskCache.hashCode())) {

                        MetaRisk metaRisk = readFromStream(new ByteArrayInputStream(xmlRiskAgain.getBytes()), false);
                        metaFuturesRisks.put(futuresXmlRiskName, metaRisk);

                    xmlFuturesRiskCaches.put(futuresXmlRiskName, xmlRiskAgain);
                }
            }
        }

        return metaFuturesRisks.get(futuresXmlRiskName);
    }

    /**
     * 构建模型
     *
     * @param doc
     */
    private MetaRisk buildModel(Document doc, boolean isCopy) {
        Node riskNode = doc.getElementsByTagName(RiskMetaConstants.RISK).item(0);
        return new MetaRisk(riskNode, null, isCopy);
    }
}
