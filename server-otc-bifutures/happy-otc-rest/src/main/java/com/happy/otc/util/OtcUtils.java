package com.happy.otc.util;

import com.happy.otc.enums.OrderStateEnum;
import com.happy.otc.proto.Order;
import com.happy.otc.vo.CapitalDetailInfoVO;

import java.util.Date;

/**
 * Otc工具类
 */
public class OtcUtils {

    /**
     * 判断到底是买方在查看详情，还是卖方在查看
     * @param userId
     * @param buyUserId
     * @param sellUserId
     * @return 1 :买方 2：卖方
     */

    public static Integer lookUserInfo(Long userId,Long buyUserId,Long sellUserId){

        if (userId.compareTo(buyUserId) == 0){
            return  Order.OrderRole.ORDER_BUY.getNumber();
        }else if (userId.compareTo(sellUserId) == 0){
            return  Order.OrderRole.ORDER_SELL.getNumber();
        }
        return  null;
    }

    /**
     * 根据订单的状态给出不同的留言
     * @param orderStateEnum
     * @param capitalDetail
     * @return
     */
    public static  String messageLave (OrderStateEnum orderStateEnum, CapitalDetailInfoVO capitalDetail){
        String message = "";
        switch (orderStateEnum){
            case  CANCELLED:
                message = orderStateEnum.getTemplate().replace("【#app#】",capitalDetail.getOrderNumber());
                break ;
            case  COMPLETED:
                message = orderStateEnum.getTemplate().replace("【#app#】",capitalDetail.getOrderNumber());
                break ;
            default:
                break;

        }
        return message;
    }


    /**
     * 判断浮动价格时，区间合理性
     * @param CurrencyPrice
     * @return true:合理 false：不合理
     */

    public static boolean floatBandCheck(double CurrencyPrice){

        if (CurrencyPrice >= -99D && CurrencyPrice <=100.00D){
            return true;
        }
        return  false;
    }

    /**
     * 计算平均放行时间
     * @return
     */

    public static Long exchangeHour(Long successTradeCount,Long payTime,Long exchangeHour){

        exchangeHour = ( (successTradeCount-1) * exchangeHour
                + ((new Date().getTime() - payTime)  /1000))/successTradeCount;

        return  exchangeHour;
    }

    /**
     * 商品支付类型数据库保存处理
     * @return
     */
    public static String buildPayMethodListListToData(String data){

        return   data.replace("[","").replace("]","");
    }


    /**
     * 商品支付类型页面显示处理
     * @return
     */
    public static String buildPayMethodListListToView(String data){
        if (data.contains( "[" )){
            return data;
        }else {
            return   "["+data+"]";
        }
    }
}
