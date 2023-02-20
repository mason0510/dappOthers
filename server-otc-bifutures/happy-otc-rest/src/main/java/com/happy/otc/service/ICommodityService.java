package com.happy.otc.service;

import com.happy.otc.entity.Commodity;
import com.happy.otc.proto.CommodityExtendVO;
import com.happy.otc.vo.CommodityInfoVO;
import com.github.pagehelper.PageInfo;

import java.math.BigDecimal;
import java.util.Map;

public interface ICommodityService {

    public PageInfo<CommodityInfoVO> getCommodityList(Integer commodityType, String kind, String relevantKind, Long userId, Integer pageIndex, Integer pageSize);

    public Long addCommodity(CommodityExtendVO.CommodityVOParam commodityVO, Long userId);

    public Long buyCommodity(Long commodityId, BigDecimal transactionVolume, BigDecimal transactionAmount,Long userId,String capitalCipher);

    public Boolean updateCommodity(CommodityExtendVO.CommodityVOParam commodityVO);

    /**
     * 把在卖的商品下架
     * @param commodityId
     * @return
     */
    public Boolean delCommodity(Long commodityId);

    PageInfo<CommodityInfoVO> getUserCommodityList(Long userId, Integer pageIndex, Integer pageSize);

    PageInfo<CommodityInfoVO> getUserCommodityList(Long userId,String kind, Integer pageIndex, Integer pageSize);

    Long countCommodity( Map<String, Object> params);

    public Commodity getCommodityById(Long commodityId);
    //校验
    public Boolean validateCommodity(Long commodityId,Long userId,Integer versionNumber);

    public PageInfo<CommodityInfoVO> getCommodityListByParam(Map<String, Object> params, Integer pageIndex, Integer pageSize);


}
