package com.happy.otc.service.impl;

import com.github.pagehelper.PageInfo;
import com.happy.otc.entity.Capital;
import com.happy.otc.entity.CapitalLog;
import com.happy.otc.entity.Commodity;
import com.happy.otc.enums.CapitalLogTypeEnum;
import com.happy.otc.proto.CommodityExtendVO;
import com.happy.otc.test.mock.MockCapitalMapper;
import com.happy.otc.test.mock.MockCommodityMapper;
import com.happy.otc.test.mock.MockUserIdentityMapper;
import com.happy.otc.vo.CommodityInfoVO;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CommodityServiceImplTest {
    private MockServiceStack mocks = MockServiceStack.DEFAULT;
    private MockCapitalMapper capitalMapper = mocks.capitalMapper;
    private MockCommodityMapper commodityMapper = mocks.commodityMapper;
    private CommodityServiceImpl service = mocks.commodityService;
    private Long buyUser = MockUserIdentityMapper.DEFAULT_ID_1;
    private Long sellUser = MockUserIdentityMapper.DEFAULT_ID_2;

    @Before
    public void setup() {
        mocks.makeDefault();
    }

    @Test
    public void testCreateCommodity() {
        CommodityExtendVO.CommodityVOParam param = CommodityExtendVO.CommodityVOParam.newBuilder()
                .setCapitalCipher( "fundPassword1" )
                .setCommodityType( CommodityExtendVO.CommodityType.SELL )
                .addAllPayMethodList( Collections.singleton( 1 ) )
                .setKind( "btc" )
                .setCurrencyAmount( 100 )
                .setTradePrice( 40000 )
                .build();
        long id = service.addCommodity( param, MockUserIdentityMapper.DEFAULT_ID_1 );

        Commodity created = commodityMapper.selectByPrimaryKey( id );
        assertEquals( 12L, created.getUserId().longValue() );
        assertEquals( 100, created.getCurrencyQuantity().intValue() );
        assertEquals( 5, created.getFeeQuantity().intValue() );

        Capital capital = mocks.capitalMapper.selectByUserId( 12L );
        assertEquals( 1105, capital.getCapitalFrozen().intValue() );
        assertEquals( 8895, capital.getCapitalAvailable().intValue() );

        List<CapitalLog> logs = mocks.capitalLogMapper.getAll();
        assertEquals( 1, logs.size() );
        CapitalLog log = logs.get( 0 );
        assertEquals( id, log.getOrderId().longValue() );
        assertEquals( CapitalLogTypeEnum.FROZEN.getValue().byteValue(), log.getType().byteValue() );
        assertEquals( -105, log.getAvailableNumber().intValue() );
        assertEquals( 105, log.getFrozenNumber().intValue() );
    }

    //@Test
    public void testListCommodityInfo() {
        commodityMapper.generate(100, index -> {
            Commodity result = new Commodity();
            result.setUserId(index % 3);
            result.setCommodityId(index);
            result.setKind(MockCommodityMapper.DEFAULT_KIND);
            result.setRelevantKind(MockCommodityMapper.DEFAULT_RELEVANTKIND);
            result.setCommodityType(MockCommodityMapper.COMMODITY_TYPE);
            result.setTradeMethod( MockCommodityMapper.TRADEMETHOD );
            result.setStatus(MockCommodityMapper.COMMODITY_STATUS);
            result.setCurrencyQuantity(MockCommodityMapper.COMMODITY_QUANTITY);
            result.setCurrencyPrice( new BigDecimal( index) );
            return result;
        });
        Map<String, Object> params = new HashMap<>();
        params.put("commodityType",MockCommodityMapper.COMMODITY_TYPE);
        params.put("relevantKind",MockCommodityMapper.DEFAULT_RELEVANTKIND);
        params.put("kind",MockCommodityMapper.DEFAULT_KIND);
        params.put("selfUserId",MockCommodityMapper.SELF_USER_ID);
        params.put("userId",2L);
       assertEquals(service.getCommodityListByParam(params, 0, 0).getSize(), 0);
        params.put("selfUserId",MockCommodityMapper.SELF_USER_ID);
        PageInfo<CommodityInfoVO> result = service.getCommodityListByParam(params, 0, 0);

        assertEquals(result.getTotal(), 33);
        CommodityInfoVO first = result.getList().get(0);
        assertEquals(first.getCommodityId().longValue(), 2L);
        assertEquals(first.getUserId().longValue(), 2L);
    }

    //@Test
    public void testBuyCommodity() {

        Long commodityId = MockCommodityMapper.DEFAULT_ID;
        BigDecimal transactionVolume = new BigDecimal(10);
        BigDecimal transactionAmount = new BigDecimal(10000);
        Long userId=MockUserIdentityMapper.DEFAULT_ID_2;
        String capitalCipher="123456";
        service.buyCommodity( commodityId, transactionVolume, transactionAmount, userId, capitalCipher );
        Map<String, Object> params = new HashMap<>();
        params.put("commodityId",commodityId);
        Commodity commodity = commodityMapper.findByParam(params).get( 0 );

        assertEquals(commodity.getCommodityId(), commodityId);
        assertEquals(commodity.getFrozenQuantity().intValue(), 2010);
        assertEquals(commodity.getCurrencyQuantity().intValue(), 4990);
    }
}
