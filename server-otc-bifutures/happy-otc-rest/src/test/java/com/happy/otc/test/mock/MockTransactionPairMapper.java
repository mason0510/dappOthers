package com.happy.otc.test.mock;

import com.happy.otc.dao.TransactionPairMapper;
import com.happy.otc.entity.TransactionPair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockTransactionPairMapper extends BaseMockMapper<TransactionPair, TransactionPair> implements TransactionPairMapper {
    public static Long DEFAULT_ID = 15L;

    public MockTransactionPairMapper() {
        super(TransactionPair.class, TransactionPair.class, "transactionPairId");
    }

    @Override
    public int deleteByPrimaryKey(Integer transactionPairId) {
        return 0;
    }

    @Override
    public TransactionPair selectByPrimaryKey(Integer transactionPairId) {
        return null;
    }

    @Override
    public List<TransactionPair> selectByParam(Map<String, Object> params) {
        return findByParam(params);
    }


    @Override
    public List<TransactionPair> defaultValue() {
        TransactionPair transactionPair = new TransactionPair();
        transactionPair.setKind( "BTC" );
        transactionPair.setRelevantKind( "CNY" );
        transactionPair.setTransactionPairId( 1 );
        return Collections.singletonList(transactionPair);
    }
}
