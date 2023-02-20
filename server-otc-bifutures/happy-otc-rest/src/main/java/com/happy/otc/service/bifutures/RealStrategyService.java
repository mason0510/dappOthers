package com.happy.otc.service.bifutures;

import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.dto.FuturesStrategyDto;
import com.happy.otc.bifutures.entity.FuturesStrategy;
import com.happy.otc.bifutures.entity.MaxAndMin;
import com.happy.otc.bifutures.pojo.FuturesStrategyQuery;
import com.happy.otc.bifutures.vo.FuturesStrategyVo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018\11\21 0021.
 */
public interface RealStrategyService {

    FuturesStrategyVo createRealStrategy(FuturesStrategyDto futuresStrategyDto) throws IOException, SAXException, ParserConfigurationException;

    List<FuturesStrategy> selectByUserId(FuturesStrategyQuery futuresStrategyQuery,String bbc);
    PageInfo<FuturesStrategy> selectByPage(FuturesStrategyQuery futuresStrategyQuery, Integer pageIndex, Integer pageSize);

    String closeStrategy (String ids,Long userId);

    Integer setQuitGainLoss (FuturesStrategyDto futuresStrategyDto);

    Integer setPrincipal (FuturesStrategyDto futuresStrategyDto);

    FuturesStrategy selectById(Long id);

    Integer cancelStrategy (FuturesStrategyDto futuresStrategyDto);

    Integer setIsDefer(FuturesStrategyDto futuresStrategyDto);

    MaxAndMin getAmount(FuturesStrategyQuery futuresStrategyQuery);
}
