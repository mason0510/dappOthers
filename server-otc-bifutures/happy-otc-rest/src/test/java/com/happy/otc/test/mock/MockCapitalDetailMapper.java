package com.happy.otc.test.mock;

import com.happy.otc.dao.CapitalDetailMapper;
import com.happy.otc.entity.CapitalDetail;
import com.happy.otc.enums.CommodityTypeEnum;
import com.happy.otc.enums.OrderStateEnum;
import com.happy.otc.vo.CapitalDetailInfoVO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MockCapitalDetailMapper extends BaseMockMapper<CapitalDetail, CapitalDetailInfoVO> implements CapitalDetailMapper {
    public static final Long DEFAULT_ID = 17L;

    public MockCapitalDetailMapper() {
        super(CapitalDetail.class, CapitalDetailInfoVO.class, "capitalDetailId");
    }

    @Override
    public List<CapitalDetailInfoVO> selectByParam(Map<String, Object> params) {
        return findVOByParam(params);
    }

    @Override
    public List<CapitalDetail> defaultValue() {
        CapitalDetail capitalDetail = new CapitalDetail();
        capitalDetail.setCapitalDetailId(DEFAULT_ID);
        capitalDetail.setBuyUserId(MockUserIdentityMapper.DEFAULT_ID_1);
        capitalDetail.setSellUserId(MockUserIdentityMapper.DEFAULT_ID_2);
        capitalDetail.setCommodityId(15L);
        capitalDetail.setCurrencyId(1L);
        capitalDetail.setTransactionPrice(new BigDecimal(40000));
        capitalDetail.setPayTime(new Date());
        capitalDetail.setStatus(OrderStateEnum.UNPAID.getValue());
        capitalDetail.setTransactionVolume(new BigDecimal(200));
        capitalDetail.setServiceFee(new BigDecimal(10));
        capitalDetail.setCommodityType(CommodityTypeEnum.SELL.getValue());

        return Collections.singletonList(capitalDetail);
    }
}
