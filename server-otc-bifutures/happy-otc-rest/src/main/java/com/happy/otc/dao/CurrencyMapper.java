package com.happy.otc.dao;

import com.happy.otc.entity.Currency;

import java.util.List;

public interface CurrencyMapper {
    int deleteByPrimaryKey(Long currencyId);

    int insert(Currency record);

    int insertSelective(Currency record);

    Currency selectByPrimaryKey(Long currencyId);

    int updateByPrimaryKeySelective(Currency record);

    int updateByPrimaryKey(Currency record);

    List<Currency> selectByPrarm();
}