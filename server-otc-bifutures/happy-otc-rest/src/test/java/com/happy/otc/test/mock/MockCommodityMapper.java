package com.happy.otc.test.mock;

import com.happy.otc.dao.CommodityMapper;
import com.happy.otc.entity.Commodity;
import com.happy.otc.enums.CommodityTypeEnum;
import com.happy.otc.vo.CommodityInfoVO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MockCommodityMapper extends BaseMockMapper<Commodity, CommodityInfoVO> implements CommodityMapper {
    public static Long DEFAULT_ID = 15L;
    public static String DEFAULT_KIND = "BTC";
    public static String DEFAULT_RELEVANTKIND = "CNY";
    public static Long SELF_USER_ID = 1234L;
    public static int COMMODITY_TYPE = 1;
    public static int TRADEMETHOD = 0;
    public static int COMMODITY_STATUS = 0;
    public static BigDecimal COMMODITY_QUANTITY = new BigDecimal( 10 );
    public static Long CURRENCY_ID = 5004L;

    public MockCommodityMapper() {
        super(Commodity.class, CommodityInfoVO.class, "commodityId");
    }

    @Override
    public List<CommodityInfoVO> selectByParam(Map<String, Object> params) {
        return findVOByParam(params);
    }

    @Override
    public Long countCommodity(Map<String, Object> params) {
        return (long) findByParam(params).size();
    }

    @Override
    public int updateQuantity(Long commodityId,
                              BigDecimal currencyQuantity,
                              BigDecimal frozenQuantity,
                              BigDecimal feeQuantity,
                              Integer version) {
        Commodity commodity = selectByPrimaryKey(commodityId);
        if (commodity.getVersion().equals(version)) {
            Commodity result = new Commodity();
            result.setCommodityId(commodityId);
            result.setCurrencyQuantity(commodity.getCurrencyQuantity().add(currencyQuantity));
            result.setFrozenQuantity(commodity.getFrozenQuantity().add(frozenQuantity));
            result.setFeeQuantity(commodity.getFeeQuantity().add(feeQuantity));
            updateByPrimaryKeySelective(result);
            return 1;
        }
        return 0;
    }

    @Override
    public List<Commodity> defaultValue() {
        Commodity commodity = new Commodity();
        commodity.setCommodityId(DEFAULT_ID);
        commodity.setCurrencyId(CURRENCY_ID);
        commodity.setCurrencyId(1L);
        commodity.setCurrencyQuantity(new BigDecimal(5000));
        commodity.setFeeQuantity(new BigDecimal(800));
        commodity.setFrozenQuantity(new BigDecimal(2000));
        commodity.setVersion(1);
        commodity.setStatus( 0 );
        commodity.setUserId(MockUserIdentityMapper.DEFAULT_ID_1);
        commodity.setCommodityType(CommodityTypeEnum.SELL.getValue());
        return Collections.singletonList(commodity);
    }
}
