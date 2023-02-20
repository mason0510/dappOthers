package com.happy.otc.service.bifutures;

import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.dto.UserAssetsDto;
import com.happy.otc.bifutures.entity.FuturesFundDetail;
import com.happy.otc.bifutures.pojo.FundDetailQuery;
import com.happy.otc.bifutures.vo.BiFuturesOtc;
import com.happy.otc.bifutures.vo.BiFuturesUserInfo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Created by Administrator on 2018\11\14 0014.
 */
public interface UserAssetsService {
    BiFuturesOtc getAssetsByUserId(Long userId) throws ParseException, ParserConfigurationException, SAXException, IOException;

    Integer updateAssetsByUserAssetsDto(Long userId, BigDecimal money,Integer isInOut);

    Integer updateByUserId(UserAssetsDto dto);
    BiFuturesUserInfo getUserInfoById(Long userId);

    PageInfo<FuturesFundDetail> selectPageByUserId (FundDetailQuery fundDetailQuery, Integer pageIndex, Integer pageSize);

    BigDecimal getNewPrice(String biCode);
}
