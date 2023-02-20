package com.happy.otc.service.bifutures;

import com.happy.otc.bifutures.enums.BiFuturesKindEnum;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public interface MetaRiskContext {

    /**
     * 获得风险数据
     */
     String getMetaRisk(BiFuturesKindEnum biFuturesKindEnum) throws IOException, SAXException, ParserConfigurationException;
}
