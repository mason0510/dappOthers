package com.happy.otc.dao;

import com.happy.otc.entity.TransactionPair;

import java.util.List;
import java.util.Map;

public interface TransactionPairMapper {
    int deleteByPrimaryKey(Integer transactionPairId);

    int insert(TransactionPair record);

    int insertSelective(TransactionPair record);

    TransactionPair selectByPrimaryKey(Integer transactionPairId);

    int updateByPrimaryKeySelective(TransactionPair record);

    int updateByPrimaryKey(TransactionPair record);

    List<TransactionPair> selectByParam(Map<String, Object> params);
}