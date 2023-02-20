package com.happy.otc.service;

import com.happy.otc.enums.OrderStateEnum;
import com.happy.otc.entity.CapitalDetail;
import com.happy.otc.vo.CapitalDetailInfoVO;
import com.happy.otc.vo.manager.CapitalDetailRequest;
import com.github.pagehelper.PageInfo;

public interface ICapitalDetailService {


    public PageInfo<CapitalDetailInfoVO> getCapitalDetailList(Integer status, Long userId, Integer pageIndex, Integer pageSize);

    public CapitalDetail getCapitalDetail(Long capitalDetailId);

    /**
     * 修改订单状态
     * 状态 1:未付款，2：已付款，3：申诉中，4：已取消，5：已完成
     * @param capitalDetailId
     * @param orderStateEnum
     * @return
     */
    public Integer updateCapitalDetail(Long capitalDetailId, OrderStateEnum orderStateEnum, String capitalCipher, Long userId);

    /**
     * 取消订单
     * @param capitalDetailId
     * @return
     */
//    public Boolean cancelOrder(Long capitalDetailId);

    public PageInfo<CapitalDetailInfoVO> getCapitalDetailPageList(CapitalDetailRequest request);

    /**
     * 获取环信群组ID
     * @param capitalDetailId   订单id
     * @return
     */
    String getEasemobGroupId(Long capitalDetailId);

    /**
     * 获取售后客服
     * @param capitalDetailId
     * @return
     */
    String getCustServiceAfterSale(Long capitalDetailId, Long userId);
}
