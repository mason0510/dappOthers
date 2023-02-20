package com.happy.otc.service.bifutures;

import com.happy.otc.bifutures.dto.FuturesStrategyDto;
import com.happy.otc.bifutures.pojo.SchemeRisk;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by Administrator on 2018\11\20 0020.
 */
public interface MetaRiskService {
    String getMetaRisk(String futuresXmlRiskName);

    SchemeRisk getSchemeRisk(String biCode) throws ParserConfigurationException, SAXException, IOException;

    void checkCreateValue(FuturesStrategyDto dto) throws ParserConfigurationException, SAXException, IOException;
}
