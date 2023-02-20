package com.happy.otc.service.bifutures;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.entity.FuturesStrategy;
import com.happy.otc.bifutures.pojo.TimeQuery;
import com.happy.otc.bifutures.pojo.WarningRatio;
import com.happy.otc.bifutures.vo.AdminAssesInfo;
import com.happy.otc.bifutures.vo.TotalAssetsVo;
import org.apache.poi.ss.usermodel.Sheet;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018\11\21 0021.
 */
public interface AdminStrategyService {
    AdminAssesInfo getUserAsses(Long userId);
    TotalAssetsVo getInfo(TimeQuery timeQuery);
    Integer updateRisk(String name, String value,String bi) throws ParserConfigurationException, SAXException, IOException;
    void exportToExcel(Sheet sheet, PageInfo<FuturesStrategy> dataList, List<JSONObject> wrapedJson);
    WarningRatio getWaring();
}
