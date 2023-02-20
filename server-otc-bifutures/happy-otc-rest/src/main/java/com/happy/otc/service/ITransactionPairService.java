package com.happy.otc.service;

import com.bitan.market.vo.OriginalExchangeInfoVO;
import com.happy.otc.entity.TransactionPair;
import com.happy.otc.enums.KindEnum;
import com.happy.otc.enums.RelevantKindEnum;
import com.github.pagehelper.PageInfo;
import com.happy.otc.vo.ExchangeInfoVO;

import java.util.List;

public interface ITransactionPairService {

    public PageInfo<TransactionPair> getTransactionPairList(String kind, String relevantKind, Integer pageIndex, Integer pageSize);

    public Double getRealTimeOnePoint(KindEnum kindEnum, RelevantKindEnum relevantKindEnum);

    public OriginalExchangeInfoVO getMarkertPrice(String kind);

    public ExchangeInfoVO getExchangeInfoVO(String kind);

    public List<OriginalExchangeInfoVO> getExchangeInfoList(List<String> kind);
}
