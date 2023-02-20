package com.happy.otc.contants;

public interface MessageCode {
    int NO_AUTHORITY = 10001;               //当前用户无权限操作
    int CAPITAL_CIPHER_ERR = 10002;         //资金密码错误
    int GET_USER_INFO_ERR = 10003;          //获取用户信息失败
    int PASSWORD_REPAT_ERR = 10004;         //登录密码不可与资金密码一致
    int NO_CHAT_GROUP = 10005;              //聊天群组已不存在
    int GET_GROUP_MEMBER_ERR = 10006;       //获取群组成员失败
    int ADD_GROUP_MEMBER_ERR = 10007;       //添加群组成员失败
    int ADDRESS_ERR = 10008;                //地址不正确
    int POSITIVE_MONEY_ERR = 10009;         //金额需为正数
    int NOT_ENOUGH_ERR = 10010;             //用户可用资金不足
    int ALREADY_DEAL = 10011;               //该订单已处理
    int NOT_ENOUGH_FROZEN_GOODS = 10012;    //商品的冻结数量不足
    int NOT_ENOUGH_FROZEN_MONEY = 10013;    //用户冻结资金不足
    int ALREADY_REGISTER = 10014;           //该用户已注册
    int NO_USER = 10015;                    //该用户不存在
    int OTC_NOT_REGISTER = 10016;           //OTC未注册
    int CAPTCHA_EXPIRED = 10017;            //验证码已过期
    int CAPTCHA_ERR = 10018;                //验证码不正确
    int IDENTITY_ALREADY_USED = 10019;      //该证件已用于实名认证
    int IDENTITY_CODE_ERR = 10020;          //身份证号码不正确
    int SHIELD_SELF_ERR = 10021;            //不能屏蔽自己
    int APPEAL_TIME_ERR = 10022;            //付款成功后5分钟才可以发起申诉
    int EDIT_COMMODITY_ERR = 10023;         //您有广告在发布，无法修改账户
    int FEE_CALCULATE_ERR = 10024;          //手续费计算失败
    int POSITIVE_QUANTITY_ERR = 10025;      //数量需为正数
    int POSITIVE_PRICE_ERR = 10026;         //价格需为正数
    int PRICE_AREA_ERR = 10027;             //浮动价格区间不合理
    int PAYMENT_ERR = 10028;                //非法的支付方式
    int CANCEL_ORDER_OVER = 10029;          //取消交易次数过多，交易权限24小时内受限
    int COMMODITY_OFFLINE = 10030;          //商品已下架
    int BUY_SELF_ERR = 10031;               //不能购买自身发布的商品
    int NOT_ENOUGH_GOODS = 10032;           //商品的数量不足
    int COMMODITY_UPDATED = 10033;          //该订单商家已修改价格
    int NOT_IDENTITY = 10034;               //您尚未实名认证
    int NO_PAYMENT_ERR = 10035;             //您需要添加支付方式
    int PASSWORD_ERR = 10036;               //登录密码错误
    int CAPITAL_CIPHER_ERR_OVER = 10037;    //资金密码错误超过3次，账户被冻结24小时
    int ORDER_STATUS_ERR = 10038;           //订单状态已变更，请重新刷新获取
    int ALREADY_APPEAL = 10039;             //订单已申诉
    int OUT_NOT_ENOUGH_ERR = 10040;         //提币数量小于最小提币数量
    int NICK_NAME_ERR = 10041;              //昵称格式不正确
    int NICK_NAME_REPEAT = 10042;           //昵称重复，请重新输入
    int NO_USDT_TRANSACTION = 10043;        //暂无未处理交易
    int CREATE_TRANSACTION_ERR = 10044;     //生成未处理交易失败
    int TARGETADDRESS_ERR = 10045;          //不能提款到目标地址
    int COMMODITY_STATUS_ERR = 10046;       //商品价格已变更，请重新刷新下单
    int MAX_ENOUGH_ERR = 10047;             //用户最大可用金额
    int MIN_TRANSACTION_MONEY_ERR = 10048;  //用户最小交易额设置不对
    int COUNTRY_CODE_ERR = 10049;           //该手机的国家区号不正确
    int WAIT_IDENTITY = 10050;               //等待实名认证

    int ORDER_COUNT_LIMIT = 20001;          //交易笔数要大于XX次
    int CAPITAL_CIPHER_ERR_EXTEND = 20002;  //资金密码错误，还剩余XX次
    int CAPITAL_CIPHER_ERR_EMPTY = 20003;  //没有设置资金密码
    /**************环信的信息代码*********************/
    int ORDER_UNPAID = 40001;        //订单创建成功
    int ORDER_ALREADY_PAID = 40002;  //买家标记已付款
    int ORDER_COMPLETED = 40003;     //商家放行
    int ORDER_APPEAL = 40004;        //申诉
    int ORDER_APPEAL_CANCELLED = 40005;  //本方取消申诉
    int ORDER_CANCELLED = 40006;     //取消订单
    int ORDER_TIME_OUT_CANCELLED = 40007;  //付款超时导致订单取消
}
