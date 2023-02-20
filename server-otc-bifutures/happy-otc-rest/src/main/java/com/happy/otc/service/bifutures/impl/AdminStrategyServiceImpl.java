package com.happy.otc.service.bifutures.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.happy.otc.bifutures.entity.*;
import com.happy.otc.bifutures.enums.AdminFuturesStrategyStatusEnum;
import com.happy.otc.bifutures.enums.BiFuturesEnum;
import com.happy.otc.bifutures.enums.BiFuturesKindEnum;
import com.happy.otc.bifutures.pojo.FuturesStrategyQuery;
import com.happy.otc.bifutures.pojo.MetaRisk;
import com.happy.otc.bifutures.pojo.TimeQuery;
import com.happy.otc.bifutures.pojo.WarningRatio;
import com.happy.otc.bifutures.utill.CollectionUtil;
import com.happy.otc.bifutures.utill.ExcelUtil;
import com.happy.otc.bifutures.vo.AdminAssesInfo;
import com.happy.otc.bifutures.vo.TotalAssetsVo;
import com.happy.otc.dao.BiFuturesParamMapper;
import com.happy.otc.dao.BiFuturesStrategyMapper;
import com.happy.otc.dao.BiFuturesUserAssetsDetailMapper;
import com.happy.otc.dao.BiFuturesUserAssetsMapper;
import com.happy.otc.service.bifutures.AdminStrategyService;
import com.happy.otc.service.bifutures.MetaRiskContext;
import com.happy.otc.service.bifutures.UserAssetsService;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\11\29 0029.
 */
@Service
public class AdminStrategyServiceImpl implements AdminStrategyService {
    @Autowired
    private BiFuturesUserAssetsMapper biFuturesUserAssetsMapper;
    @Autowired
    private BiFuturesUserAssetsDetailMapper biFuturesUserAssetsDetailMapper;
    @Autowired
    private BiFuturesStrategyMapper biFuturesStrategyMapper;
    @Autowired
    UserAssetsService userAssetsService;
    @Autowired
    private MetaRiskContext metaRiskContext;
    @Autowired
    BiFuturesParamMapper biFuturesParamMapper;

    String RISK_XML_DATA = "RISK_XML_DATA_";

    @Override
    public AdminAssesInfo getUserAsses(Long userId) {
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal fundIn = BigDecimal.ZERO;
        BigDecimal funOut = BigDecimal.ZERO;
        BigDecimal frozenPrincepal = BigDecimal.ZERO;
        BigDecimal currentPal = BigDecimal.ZERO;
        BigDecimal selldTotalPal = BigDecimal.ZERO;
        BigDecimal totalBuyMoney = BigDecimal.ZERO;
        BigDecimal currenBuyMoney = BigDecimal.ZERO;
        BigDecimal totalServiceCharge = BigDecimal.ZERO;
        Integer totalBuyAmount = 0;
        Integer cancelAmount= 0;
        Integer currentAmount= 0;
        Integer yingStrategyAmount= 0;
        Integer kuiStrategyAmount= 0;
        BigDecimal totalDeferCharge = BigDecimal.ZERO;
        UserAssets userAssets = biFuturesUserAssetsMapper.selectByUserId(userId);
        balance = userAssets.getBalance();
        fundIn = userAssets.getFundIn();
        funOut = userAssets.getFundOut();

        FuturesStrategyQuery futuresStrategyQuery = new FuturesStrategyQuery();
        futuresStrategyQuery.setType(1);
        futuresStrategyQuery.setUserId(userId);
        futuresStrategyQuery.setStatus(CollectionUtil.asList(1,2,3,4));
        List<FuturesStrategy> futuresStrategyList = biFuturesStrategyMapper.selectByUserId(futuresStrategyQuery);
        for (FuturesStrategy futuresStrategy : futuresStrategyList) {
           BigDecimal newPrice = userAssetsService.getNewPrice(futuresStrategy.getBiCode());
            if (futuresStrategy.getDeferCharge()!=null){
                totalDeferCharge = totalDeferCharge.add(futuresStrategy.getDeferCharge());
            }
            if (futuresStrategy.getStatus() != 4){
                frozenPrincepal = frozenPrincepal.add(futuresStrategy.getPrincipal());
            }

            if (futuresStrategy.getStatus() == 2){
                currenBuyMoney = currenBuyMoney.add(futuresStrategy.getBuyPriceDeal().multiply(futuresStrategy.getContractValue()
                        .multiply(new BigDecimal(futuresStrategy.getAmount()))));
                currentAmount = currentAmount + 1;

                if (futuresStrategy.getDirection().equals("B")) {
                    currentPal = currentPal.add((newPrice.subtract(futuresStrategy.getBuyPriceDeal())).multiply(new BigDecimal
                            (futuresStrategy.getAmount())).multiply(futuresStrategy.getContractValue()));
                } else if (futuresStrategy.getDirection().equals("S")) {
                    currentPal = currentPal.add((futuresStrategy.getBuyPriceDeal().subtract(newPrice)).multiply(new BigDecimal
                            (futuresStrategy.getAmount())).multiply(futuresStrategy.getContractValue()));
                }
            }

            if (futuresStrategy.getStatus() == 2 || futuresStrategy.getStatus() == 3){
                totalServiceCharge = totalServiceCharge.add(futuresStrategy.getServiceCharge());
                totalBuyMoney = totalBuyMoney.add(futuresStrategy.getBuyPriceDeal().multiply(futuresStrategy.getContractValue()
                        .multiply(new BigDecimal(futuresStrategy.getAmount()))));
                totalBuyAmount = totalBuyAmount + 1;
            }
            if (futuresStrategy.getStatus() == 3){
                if (futuresStrategy.getDirection().equals("B")){
                    selldTotalPal = selldTotalPal.add((futuresStrategy.getSellPriceDeal().subtract(futuresStrategy.getBuyPriceDeal()))
                            .multiply(new BigDecimal(futuresStrategy.getAmount())).multiply(futuresStrategy.getContractValue()));
                    if ((futuresStrategy.getSellPriceDeal().subtract(futuresStrategy.getBuyPriceDeal())).compareTo(new BigDecimal(0))>0){
                        yingStrategyAmount = yingStrategyAmount + 1;
                    }else if ((futuresStrategy.getSellPriceDeal().subtract(futuresStrategy.getBuyPriceDeal())).compareTo(new BigDecimal(0))<0){
                        kuiStrategyAmount = kuiStrategyAmount + 1;
                    }
                }else if (futuresStrategy.getDirection().equals("S")){
                    selldTotalPal = selldTotalPal.add((futuresStrategy.getBuyPriceDeal().subtract(futuresStrategy.getSellPriceDeal()))
                            .multiply(new BigDecimal(futuresStrategy.getAmount())).multiply(futuresStrategy.getContractValue()));
                    if ((futuresStrategy.getSellPriceDeal().subtract(futuresStrategy.getBuyPriceDeal())).compareTo(new BigDecimal(0))<0){
                        yingStrategyAmount = yingStrategyAmount + 1;
                    }else if ((futuresStrategy.getSellPriceDeal().subtract(futuresStrategy.getBuyPriceDeal())).compareTo(new BigDecimal(0))>0){
                        kuiStrategyAmount = kuiStrategyAmount + 1;
                    }
                }
            }
            if (futuresStrategy.getStatus() == 4){
                cancelAmount = cancelAmount + 1;
            }
        }

        AdminAssesInfo adminAssesInfo = new AdminAssesInfo();
        adminAssesInfo.setBalance(balance);
        adminAssesInfo.setFundIn(fundIn);
        adminAssesInfo.setFunOut(funOut);
        adminAssesInfo.setTotalDeferCharge(totalDeferCharge);
        adminAssesInfo.setFrozenPrincepal(frozenPrincepal);
        adminAssesInfo.setTotalBuyMoney(totalBuyMoney);
        adminAssesInfo.setCurrenBuyMoney(currenBuyMoney);
        adminAssesInfo.setTotalServiceCharge(totalServiceCharge);
        adminAssesInfo.setTotalBuyAmount(totalBuyAmount);
        adminAssesInfo.setCurrentAmount(currentAmount);
        adminAssesInfo.setCancelAmount(cancelAmount);
        adminAssesInfo.setSelldTotalPal(selldTotalPal);
        adminAssesInfo.setYingStrategyAmount(yingStrategyAmount);
        adminAssesInfo.setKuiStrategyAmount(kuiStrategyAmount);
        adminAssesInfo.setCurrentPal(currentPal);
        return adminAssesInfo;
    }

    @Override
    public TotalAssetsVo getInfo(TimeQuery timeQuery) {

        BigDecimal totalIn = biFuturesUserAssetsMapper.getTotalFundIn(timeQuery);
        BigDecimal totalOut = biFuturesUserAssetsMapper.getTotalFundOut(timeQuery);

        Integer newPepal = biFuturesStrategyMapper.getNewPepal(timeQuery);
        timeQuery.setBiCode("BTC");
        Integer btcYhs = biFuturesStrategyMapper.selectYHS(timeQuery);
        timeQuery.setBiCode("BCH");
        Integer BCHYhs = biFuturesStrategyMapper.selectYHS(timeQuery);
        timeQuery.setBiCode("ETH");
        Integer ETHYhs = biFuturesStrategyMapper.selectYHS(timeQuery);
        timeQuery.setBiCode("EOS");
        Integer EOSYhs = biFuturesStrategyMapper.selectYHS(timeQuery);
        timeQuery.setBiCode("LTC");
        Integer LTCYhs = biFuturesStrategyMapper.selectYHS(timeQuery);
        timeQuery.setBiCode("XRP");
        Integer XRPYhs = biFuturesStrategyMapper.selectYHS(timeQuery);

        timeQuery.setBiCode("BTC");
        Integer BTCJYSL = biFuturesStrategyMapper.selectJYSL(timeQuery);
        if (BTCJYSL==null){
            BTCJYSL = 0;
        }
        timeQuery.setBiCode("BCH");
        Integer BCHJYSL = biFuturesStrategyMapper.selectJYSL(timeQuery);
        if (BCHJYSL==null){
            BCHJYSL = 0;
        }
        timeQuery.setBiCode("ETH");
        Integer ETHJYSL = biFuturesStrategyMapper.selectJYSL(timeQuery);
        if (ETHJYSL==null){
            ETHJYSL = 0;
        }
        timeQuery.setBiCode("EOS");
        Integer EOSJYSL = biFuturesStrategyMapper.selectJYSL(timeQuery);
        if (EOSJYSL==null){
            EOSJYSL = 0;
        }
        timeQuery.setBiCode("LTC");
        Integer LTCJYSL = biFuturesStrategyMapper.selectJYSL(timeQuery);
        if (LTCJYSL==null){
            LTCJYSL = 0;
        }
        timeQuery.setBiCode("XRP");
        Integer XRPJYSL = biFuturesStrategyMapper.selectJYSL(timeQuery);
        if (XRPJYSL==null){
            XRPJYSL = 0;
        }
        BigDecimal ccykBTC =BigDecimal.ZERO;
        BigDecimal ccykLTC=BigDecimal.ZERO;
        BigDecimal ccykEOS=BigDecimal.ZERO;
        BigDecimal ccykBCH=BigDecimal.ZERO;
        BigDecimal ccykETH=BigDecimal.ZERO;
        BigDecimal ccykXRP=BigDecimal.ZERO;

        BigDecimal jyzhBTC =BigDecimal.ZERO;
        BigDecimal jyzhLTC=BigDecimal.ZERO;
        BigDecimal jyzhEOS=BigDecimal.ZERO;
        BigDecimal jyzhBCH=BigDecimal.ZERO;
        BigDecimal jyzhETH=BigDecimal.ZERO;
        BigDecimal jyzhXRP=BigDecimal.ZERO;

        BigDecimal jsykBTC =BigDecimal.ZERO;
        BigDecimal jsykLTC=BigDecimal.ZERO;
        BigDecimal jsykEOS=BigDecimal.ZERO;
        BigDecimal jsykBCH=BigDecimal.ZERO;
        BigDecimal jsykETH=BigDecimal.ZERO;
        BigDecimal jsykXRP=BigDecimal.ZERO;

        BigDecimal dyfBTC =BigDecimal.ZERO;
        BigDecimal dyfLTC=BigDecimal.ZERO;
        BigDecimal dyfEOS=BigDecimal.ZERO;
        BigDecimal dyfBCH=BigDecimal.ZERO;
        BigDecimal dyfETH=BigDecimal.ZERO;
        BigDecimal dyfXRP=BigDecimal.ZERO;

        BigDecimal ptccBTC=BigDecimal.ZERO;
        BigDecimal ptccLTC=BigDecimal.ZERO;
        BigDecimal ptccEOS=BigDecimal.ZERO;
        BigDecimal ptccBCH=BigDecimal.ZERO;
        BigDecimal ptccETH=BigDecimal.ZERO;
        BigDecimal ptccXRP=BigDecimal.ZERO;

        FuturesStrategyQuery fuq = new FuturesStrategyQuery();
        fuq.setStatus(CollectionUtil.asList(3));
        fuq.setLeSellTime(timeQuery.getLeSellTime());
        fuq.setGeSellTime(timeQuery.getGeSellTime());
        fuq.setType(1);
        List<FuturesStrategy> futuresStrategy = biFuturesStrategyMapper.selectByUserId(fuq);
        for (FuturesStrategy fu : futuresStrategy) {
            if (fu.getStatus()==3&&fu.getBiCode().equals("BTC")){

                if (fu.getDirection().equals("B")){
                    jsykBTC=jsykBTC.add((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                    .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccBTC = ptccBTC.add(((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }else if (fu.getDirection().equals("S")){
                    jsykBTC=jsykBTC.add((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccBTC = ptccBTC.add(((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }

            }
            if (fu.getStatus()==3&&fu.getBiCode().equals("BCH")){

                if (fu.getDirection().equals("B")){
                    jsykBCH=jsykBCH.add((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccBCH = ptccBCH.add(((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }else if (fu.getDirection().equals("S")){
                    jsykBCH=jsykBCH.add((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccBCH = ptccBCH.add(((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }
            }
            if (fu.getStatus()==3&&fu.getBiCode().equals("LTC")){
                if (fu.getDirection().equals("B")){
                    jsykLTC=jsykLTC.add((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccLTC = ptccLTC.add(((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }else if (fu.getDirection().equals("S")){
                    jsykLTC=jsykLTC.add((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccLTC = ptccLTC.add(((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }
            }
            if (fu.getStatus()==3&&fu.getBiCode().equals("EOS")){
                if (fu.getDirection().equals("B")){
                    jsykEOS=jsykEOS.add((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccEOS = ptccEOS.add(((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }else if (fu.getDirection().equals("S")){
                    jsykEOS=jsykEOS.add((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccEOS = ptccEOS.add(((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }
            }
            if (fu.getStatus()==3&&fu.getBiCode().equals("ETH")){

                if (fu.getDirection().equals("B")){
                    jsykETH=jsykETH.add((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccETH = ptccETH.add(((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }else if (fu.getDirection().equals("S")){
                    jsykETH=jsykETH.add((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccETH = ptccETH.add(((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }
            }
            if (fu.getStatus()==3&&fu.getBiCode().equals("XRP")){

                if (fu.getDirection().equals("B")){
                    jsykXRP=jsykXRP.add((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccXRP = ptccXRP.add(((fu.getSellPriceDeal().subtract(fu.getBuyPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }else if (fu.getDirection().equals("S")){
                    jsykXRP=jsykXRP.add((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())));
                    if (((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                            .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())).compareTo(new BigDecimal(0))<0){
                        ptccXRP = ptccXRP.add(((fu.getBuyPriceDeal().subtract(fu.getSellPriceDeal())).multiply(fu.getContractValue())
                                .multiply(new BigDecimal(fu.getAmount())).add(fu.getPrincipal())));
                    }
                }
            }

        }

        FuturesStrategyQuery fq = new FuturesStrategyQuery();
        fq.setStatus(CollectionUtil.asList(2));
        fq.setType(1);
        List<FuturesStrategy> futuresStrategyl = biFuturesStrategyMapper.selectByUserId(fq);
        for (FuturesStrategy strategy : futuresStrategyl) {
            BigDecimal newPrice = userAssetsService.getNewPrice(strategy.getBiCode());
            if (strategy.getBiCode().equals("BTC")&&strategy.getStatus()==2){
                if (strategy.getDirection().equals("B")){
                    ccykBTC=ccykBTC.add((newPrice.subtract(strategy.getBuyPriceDeal())).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }else if (strategy.getDirection().equals("S")){
                    ccykBTC=ccykBTC.add((strategy.getBuyPriceDeal().subtract(newPrice)).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }
            }
            if (strategy.getBiCode().equals("BCH")&&strategy.getStatus()==2){
                if (strategy.getDirection().equals("B")){
                    ccykBCH=ccykBCH.add((newPrice.subtract(strategy.getBuyPriceDeal())).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }else if (strategy.getDirection().equals("S")){
                    ccykBCH=ccykBCH.add((strategy.getBuyPriceDeal().subtract(newPrice)).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }
            }
            if (strategy.getBiCode().equals("LTC")&&strategy.getStatus()==2){
                if (strategy.getDirection().equals("B")){
                    ccykLTC=ccykLTC.add((newPrice.subtract(strategy.getBuyPriceDeal())).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }else if (strategy.getDirection().equals("S")){
                    ccykLTC=ccykLTC.add((strategy.getBuyPriceDeal().subtract(newPrice)).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }
            }
            if (strategy.getBiCode().equals("EOS")&&strategy.getStatus()==2){
                if (strategy.getDirection().equals("B")){
                    ccykEOS=ccykEOS.add((newPrice.subtract(strategy.getBuyPriceDeal())).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }else if (strategy.getDirection().equals("S")){
                    ccykEOS=ccykEOS.add((strategy.getBuyPriceDeal().subtract(newPrice)).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }
            }
            if (strategy.getBiCode().equals("ETH")&&strategy.getStatus()==2){
                if (strategy.getDirection().equals("B")){
                    ccykETH=ccykETH.add((newPrice.subtract(strategy.getBuyPriceDeal())).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }else if (strategy.getDirection().equals("S")){
                    ccykETH=ccykETH.add((strategy.getBuyPriceDeal().subtract(newPrice)).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }
            }
            if (strategy.getBiCode().equals("XRP")&&strategy.getStatus()==2){
                if (strategy.getDirection().equals("B")){
                    ccykXRP=ccykXRP.add((newPrice.subtract(strategy.getBuyPriceDeal())).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }else if (strategy.getDirection().equals("S")){
                    ccykXRP=ccykXRP.add((strategy.getBuyPriceDeal().subtract(newPrice)).multiply(strategy.getContractValue())
                            .multiply(new BigDecimal(strategy.getAmount())));
                }
            }
        }
        List<FuturesJyzhf> list = biFuturesUserAssetsDetailMapper.selectJyshf(timeQuery);
        for (FuturesJyzhf futuresJyzhf : list) {
            if (futuresJyzhf.getBiCode().equals("BTC")){
                jyzhBTC = jyzhBTC.add(futuresJyzhf.getMoney());
            }
            if (futuresJyzhf.getBiCode().equals("BCH")){
                jyzhBCH = jyzhBCH.add(futuresJyzhf.getMoney());
            }
            if (futuresJyzhf.getBiCode().equals("LTC")){
                jyzhLTC = jyzhLTC.add(futuresJyzhf.getMoney());
            }
            if (futuresJyzhf.getBiCode().equals("EOS")){
                jyzhEOS = jyzhEOS.add(futuresJyzhf.getMoney());
            }
            if (futuresJyzhf.getBiCode().equals("ETH")){
                jyzhETH = jyzhETH.add(futuresJyzhf.getMoney());
            }
            if (futuresJyzhf.getBiCode().equals("XRP")){
                jyzhXRP = jyzhXRP.add(futuresJyzhf.getMoney());
            }
        }
        List<FuturesJyzhf> lists = biFuturesUserAssetsDetailMapper.selectDyf(timeQuery);
        for (FuturesJyzhf futuresJyzhf : lists) {
            if (futuresJyzhf.getBiCode().equals("BTC")){
                if (futuresJyzhf.getMoney()!=null){
                    dyfBTC = dyfBTC.add(futuresJyzhf.getMoney());
                }
            }
            if (futuresJyzhf.getBiCode().equals("BCH")){
                if (futuresJyzhf.getMoney()!=null){dyfBCH = dyfBCH.add(futuresJyzhf.getMoney());}
            }
            if (futuresJyzhf.getBiCode().equals("LTC")){
                if (futuresJyzhf.getMoney()!=null){
                    dyfLTC = dyfLTC.add(futuresJyzhf.getMoney());
                }
            }
            if (futuresJyzhf.getBiCode().equals("EOS")){
                if (futuresJyzhf.getMoney()!=null){
                    dyfEOS = dyfEOS.add(futuresJyzhf.getMoney());}
            }
            if (futuresJyzhf.getBiCode().equals("ETH")){
                if (futuresJyzhf.getMoney()!=null){
                    dyfETH = dyfETH.add(futuresJyzhf.getMoney());}
            }
            if (futuresJyzhf.getBiCode().equals("XRP")){
                if (futuresJyzhf.getMoney()!=null){
                    dyfXRP = dyfXRP.add(futuresJyzhf.getMoney());}
            }
        }

        TotalAssetsVo tav = new TotalAssetsVo();
        tav.setTotalFundIn(totalIn);
        tav.setTotalFundOut(totalOut);
        tav.setYhsBTC(btcYhs);
        tav.setYhsBCH(BCHYhs);
        tav.setYhsEOS(EOSYhs);
        tav.setYhsETH(ETHYhs);
        tav.setYhsLTC(LTCYhs);
        tav.setYhsXRP(XRPYhs);
        tav.setYhsTotal(btcYhs+BCHYhs+EOSYhs+ETHYhs+LTCYhs+XRPYhs);
        tav.setJysBTC(BTCJYSL);
        tav.setJysBCH(BCHJYSL);
        tav.setJysEOS(EOSJYSL);
        tav.setJysETH(ETHJYSL);
        tav.setJysLTC(LTCJYSL);
        tav.setJysXRP(XRPJYSL);
        tav.setJysTotal(BTCJYSL+BCHJYSL+EOSJYSL+ETHJYSL+LTCJYSL+XRPJYSL);
        tav.setCcykBTC(ccykBTC);
        tav.setCcykBCH(ccykBCH);
        tav.setCcykEOS(ccykEOS);
        tav.setCcykETH(ccykETH);
        tav.setCcykXRP(ccykXRP);
        tav.setCcykLTC(ccykLTC);
        tav.setCcykTotal(ccykBTC.add(ccykLTC).add(ccykEOS).add(ccykBCH).add(ccykETH).add(ccykXRP));
        tav.setJyzhBTC(jyzhBTC);
        tav.setJyzhBCH(jyzhBCH);
        tav.setJyzhEOS(jyzhEOS);
        tav.setJyzhETH(jyzhETH);
        tav.setJyzhLTC(jyzhLTC);
        tav.setJyzhXRP(jyzhXRP);
        tav.setJyzhTotal(jyzhBTC.add(jyzhLTC).add(jyzhEOS).add(jyzhBCH).add(jyzhETH).add(jyzhXRP));
        tav.setJsykBTC(jsykBTC);
        tav.setJsykBCH(jsykBCH);
        tav.setJsykEOS(jsykEOS);
        tav.setJsykETH(jsykETH);
        tav.setJsykLTC(jsykLTC);
        tav.setJsykXRP(jsykXRP);
        tav.setJsykTotal(jsykBTC.add(jsykLTC).add(jsykEOS).add(jsykBCH).add(jsykETH).add(jsykXRP));
        tav.setDyfBTC(dyfBTC);
        tav.setDyfBCH(dyfBCH);
        tav.setDyfEOS(dyfEOS);
        tav.setDyfETH(dyfETH);
        tav.setDyfXRP(dyfXRP);
        tav.setDyfLTC(dyfLTC);
        tav.setDyfTotal(dyfBTC.add(dyfLTC).add(dyfEOS).add(dyfBCH).add(dyfETH).add(dyfXRP));
        tav.setPtsrBTC(jyzhBTC.add(dyfBTC).subtract(jsykBTC));
        tav.setPtsrBCH(jyzhBCH.add(dyfBCH).subtract(jsykBCH));
        tav.setPtsrEOS(jyzhEOS.add(dyfEOS).subtract(jsykEOS));
        tav.setPtsrETH(jyzhETH.add(dyfETH).subtract(jsykETH));
        tav.setPtsrLTC(jyzhLTC.add(dyfLTC).subtract(jsykLTC));
        tav.setPtsrXRP(jyzhXRP.add(dyfXRP).subtract(jsykXRP));
        tav.setPtsrTotal(jyzhBTC.add(dyfBTC).subtract(jsykBTC).add(jyzhBCH.add(dyfBCH).subtract(jsykBCH)).add(jyzhEOS.add(dyfEOS).subtract(jsykEOS)).add(jyzhETH.add(dyfETH).subtract(jsykETH)).add(jyzhLTC.add(dyfLTC).subtract(jsykLTC)).add(jyzhXRP.add(dyfXRP).subtract(jsykXRP)));
        tav.setPtccBTC(ptccBTC);
        tav.setPtccBCH(ptccBCH);
        tav.setPtccEOS(ptccEOS);
        tav.setPtccETH(ptccETH);
        tav.setPtccLTC(ptccLTC);
        tav.setPtccXRP(ptccXRP);
        tav.setPtccTotal(ptccBTC.add(ptccLTC).add(ptccEOS).add(ptccBCH).add(ptccETH).add(ptccXRP));
        tav.setNewPepal(newPepal);
        return tav;
    }

    @Override
    @Transactional
    public Integer updateRisk(String name, String value,String biCode) throws ParserConfigurationException, SAXException, IOException {

        String risk = metaRiskContext.getMetaRisk(BiFuturesKindEnum.getByValue(biCode));
        JSONObject jsonObject = JSON.parseObject(risk);
        jsonObject.getJSONObject(name).put("value",value);
       /* MetaRisk metaRisk = metaRiskContext.getMetaRisk(BiFuturesKindEnum.getByValue(biCode));
        if (metaRisk == null){
            BizException.fail(999,"风险参数文件不存在");
        }
        SchemeRisk schemeRisk = metaRisk.findSchemeRiskByType(1);
        RiskParameter parameter = schemeRisk.getParameter(name);
        if (parameter.getRegex() != null) {
            Pattern pattern = Pattern.compile(parameter.getRegex());
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("格式错误");
            }
        }
        parameter.replaceValue(value);

        String xmlStr = riskToXmlString(metaRisk);*/
        String xmlStr = jsonObject.toJSONString();
        Params param = biFuturesParamMapper.getByName(RISK_XML_DATA+biCode);
        Integer result = 0;
        if (param!=null){
            Param mod = new Param(param.getId());
            mod.setValue(xmlStr);
            result = biFuturesParamMapper.update(mod);
        }
        return result;
    }

    @Override
    public void exportToExcel(Sheet sheet, PageInfo<FuturesStrategy> dataList, List<JSONObject> wrapedJson) {
        writeHeader(sheet, dataList.getPageNum());
        int start = (dataList.getPageNum() - 1) * dataList.getPageSize() + 2;

        for (JSONObject unitItem : wrapedJson) {
            writeUnitItem(sheet, unitItem, start);
            start++;
        }
    }

    @Override
    public WarningRatio getWaring() {
        String btc ="";
        String eos ="";
        String ltc ="";
        String bch ="";
        String eth ="";
        String xrp ="";

        List<Params> paramses = biFuturesParamMapper.getAll();
        for (Params paramse : paramses) {
            if (paramse.getName().equals("RISK_XML_DATA_BTC")){
                JSONObject jsonObject = JSON.parseObject(paramse.getValue());
                btc = jsonObject.getJSONObject("warningRatio").getString("value");
            }
            if (paramse.getName().equals("RISK_XML_DATA_EOS")){
                JSONObject jsonObject = JSON.parseObject(paramse.getValue());
                eos = jsonObject.getJSONObject("warningRatio").getString("value");
            }
            if (paramse.getName().equals("RISK_XML_DATA_LTC")){
                JSONObject jsonObject = JSON.parseObject(paramse.getValue());
                ltc = jsonObject.getJSONObject("warningRatio").getString("value");
            }
            if (paramse.getName().equals("RISK_XML_DATA_BCH")){
                JSONObject jsonObject = JSON.parseObject(paramse.getValue());
                bch = jsonObject.getJSONObject("warningRatio").getString("value");
            }
            if (paramse.getName().equals("RISK_XML_DATA_ETH")){
                JSONObject jsonObject = JSON.parseObject(paramse.getValue());
                eth = jsonObject.getJSONObject("warningRatio").getString("value");
            }
            if (paramse.getName().equals("RISK_XML_DATA_XRP")){
                JSONObject jsonObject = JSON.parseObject(paramse.getValue());
                xrp = jsonObject.getJSONObject("warningRatio").getString("value");
            }
        }
        WarningRatio w = new WarningRatio();
        w.setBtcRatio(btc);
        w.setBchRatio(bch);
        w.setEosRatio(eos);
        w.setEthRatio(eth);
        w.setXrpRatio(xrp);
        w.setLtcRatio(ltc);
        return w;
    }

    public String riskToXmlString(MetaRisk risk) {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("模型解析错误", e);
        }
        Document doc = docBuilder.newDocument();
        risk.convertToDOM(doc, null);
        String result = null;
        try {
            result = docToXmlString(doc);
        } catch (TransformerException e) {
            throw new RuntimeException("XML转换字符串错误", e);
        }

        return result;
    }
    private String docToXmlString(Document doc) throws TransformerException {
        TransformerFactory transFact = TransformerFactory.newInstance();
        Transformer transformer = transFact.newTransformer();
        StringWriter buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
        transformer.transform(new DOMSource(doc), new StreamResult(buffer));
        return buffer.toString();
    }

    /**
     * 写入策略单元头
     *
     * @param sheet
     * @param paginator
     */
    private void writeHeader(Sheet sheet, int paginator) {
        if (paginator == 1) {
            List<Object> title = new ArrayList<Object>();
            title.addAll(CollectionUtil
                    .asList(new String[9]));
            title.add("合约交易报表导出");
            ExcelUtil.writeContent3(sheet, 0, title);
            title = new ArrayList<Object>();
            title.add("订单号");
            title.add("委托用户");
            title.add("品种");
            title.add("发起时间");
            title.add("方向");
            title.add("数量");
            title.add("开仓价");
            title.add("保证金");
            title.add("浮动盈亏");
            title.add("强平价");
            title.add("止盈价");
            title.add("止损价");
            title.add("状态");
            ExcelUtil.writeContent3(sheet, 1, title);

        }

    }
    /**
     * 写入策略单元数据
     *
     * @param sheet    excel模型
     * @param unitItem 单元数据
     * @param start    起始位置
     */
    private void writeUnitItem(Sheet sheet, JSONObject unitItem, int start) {
        List<Object> data = new ArrayList<Object>();
        data.add(unitItem.get("id"));
        data.add(unitItem.get("username"));
        data.add(BiFuturesEnum.getByValue((String) unitItem.get("biCode")).getMessage());
        data.add(unitItem.get("buyOrderTime"));
        data.add(unitItem.get("direction"));
        data.add(unitItem.get("amount")+ "手");
        data.add(unitItem.get("buyPriceOrder"));
        data.add(unitItem.get("principal"));
        data.add(unitItem.get("profit"));
        data.add(unitItem.get("closePrice"));
        data.add(unitItem.get("gainPrice"));
        data.add(unitItem.get("lossPrice"));
        data.add(AdminFuturesStrategyStatusEnum.getByValue(Integer.valueOf(unitItem.get("status").toString())).getMessage());
        ExcelUtil.writeContent3(sheet, start, data);
    }
}
