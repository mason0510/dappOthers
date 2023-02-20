package com.happy.otc.service.bifutures;

import com.happy.otc.bifutures.vo.BiFuturesVo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public interface BiFuturesService {
    List<BiFuturesVo> getBiKinds();

    String getRiskDate(String biCode) throws ParserConfigurationException, SAXException, IOException;
}
