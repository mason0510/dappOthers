package com.happy.otc.service;

import com.happy.otc.enums.CapitalLogTypeEnum;
import com.happy.otc.vo.CapitalInfoVO;
import com.happy.otc.vo.CapitalLogInfoVO;
import com.github.pagehelper.PageInfo;
import com.happy.otc.vo.FundVO;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ICapitalService {

    public PageInfo<CapitalInfoVO> getCapitalInfoVO(Long userId, Integer pageIndex, Integer pageSize);

    /**
     * 获取用户的资金对象
     * @param params
     * @return
     */
    List<CapitalInfoVO> getCapitalInfoVO(Map<String, Object> params);

    CapitalInfoVO getCapitalInfoByUserIdAndCurrencyId(Long userId, Long currencyId);

    public void transferCapital(Long capitalDetailId);

    public int updateCapitalInfo(CapitalInfoVO capitalInfoVO);

    public Boolean addCapitalInfo(Long userId);

    PageInfo<CapitalLogInfoVO> getCapitalLogList(Long userId, Integer type, String time, Integer timeZone, Long kind,
                                                 Integer pageIndex, Integer pageSize);

    public CapitalInfoVO getCapitalInfoVO(CapitalInfoVO capitalInfoVO);

    int capitalCut(Long userId, BigDecimal money, Long currencyId, CapitalLogTypeEnum capitalLogTypeEnum) throws ParserConfigurationException, SAXException, ParseException, IOException;

    public FundVO buildFundVO(Long userId) throws ParserConfigurationException, SAXException, ParseException, IOException;
}
