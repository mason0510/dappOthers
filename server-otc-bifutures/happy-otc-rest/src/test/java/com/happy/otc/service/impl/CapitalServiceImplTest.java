package com.happy.otc.service.impl;

import com.bitan.common.component.ApiResponseCode;
import com.github.pagehelper.PageInfo;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.entity.Capital;
import com.happy.otc.entity.Commodity;
import com.happy.otc.test.mock.MockCapitalDetailMapper;
import com.happy.otc.test.mock.MockCapitalMapper;
import com.happy.otc.test.mock.MockCommodityMapper;
import com.happy.otc.test.mock.MockUserIdentityMapper;
import com.happy.otc.test.utils.AssertUtils;
import com.happy.otc.vo.CapitalInfoVO;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CapitalServiceImplTest {
    private MockServiceStack mocks = MockServiceStack.DEFAULT;
    private MockCapitalMapper capitalMapper = mocks.capitalMapper;
    private CapitalServiceImpl service = mocks.capitalService;

    private Long buyUser = MockUserIdentityMapper.DEFAULT_ID_1;
    private Long sellUser = MockUserIdentityMapper.DEFAULT_ID_2;

    @Before
    public void setup() {
        mocks.makeDefault();
    }

    @Test
    public void testListCapitalInfo() {
        capitalMapper.generate(100, index -> {
            Capital result = new Capital();
            result.setUserId(index % 3);
            result.setCapitalId(index);
            return result;
        });

        assertEquals(service.getCapitalInfoVO(10L, 0, 0).getSize(), 0);

        PageInfo<CapitalInfoVO> result = service.getCapitalInfoVO(2L, 0, 0);
        assertEquals(result.getSize(), 33);
        CapitalInfoVO first = result.getList().get(0);
        assertEquals(first.getCapitalId().longValue(), 2L);
        assertEquals(first.getUserId().longValue(), 2L);
    }

    @Test
    public void testCapitalNotExist() {
        capitalMapper.clear();
        AssertUtils.assertErrorCode(() -> service.transferCapital(10L), ApiResponseCode.DATA_NOT_EXIST.get());
    }

    @Test
    public void testNegativeValue() {
        mocks.capitalDetailMapper.selectByPrimaryKey(MockCapitalDetailMapper.DEFAULT_ID).setTransactionVolume(new BigDecimal(-10));
        AssertUtils.assertErrorCode(() -> service.transferCapital(17L), MessageCode.POSITIVE_MONEY_ERR);
    }

    @Test
    public void testVolumeExceedFrozen() {
        mocks.capitalDetailMapper.selectByPrimaryKey(MockCapitalDetailMapper.DEFAULT_ID).setTransactionVolume(new BigDecimal(10000));
        AssertUtils.assertErrorCode(() -> service.transferCapital(17L), MessageCode.NOT_ENOUGH_FROZEN_GOODS);
    }

    @Test
    public void testTransfer() {
        service.transferCapital(17L);

        Capital buyCapital = capitalMapper.selectByUserId(buyUser, 1L);
        Capital sellCapital = capitalMapper.selectByUserId(sellUser, 1L);

        assertEquals(9200, buyCapital.getCapitalAvailable().intValue());
        assertEquals(290, sellCapital.getCapitalFrozen().intValue());

        assertEquals(1, mocks.capitalDetailCountMapper.selectByUserId(buyUser).getSuccessTradeCount().intValue());
        assertEquals(1, mocks.capitalDetailCountMapper.selectByUserId(sellUser).getSuccessTradeCount().intValue());

        Commodity commodity = mocks.commodityMapper.selectByPrimaryKey(MockCommodityMapper.DEFAULT_ID);
        assertEquals(1800, commodity.getFrozenQuantity().intValue());
    }
}
