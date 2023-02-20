package com.happy.otc.enums;

/**
 * 状态 1:未付款，2：已付款，3：申诉中，4：已取消，5：已完成
 */
public enum OrderStateEnum {

    //ALL(0, "全部", "【#app#】用户已成功下单，请注意查收汇款。", "【#app#】Verification Code#code#,Welcome to sign up Taohaifang,the verification code will be valid within 10 minutes."),
    /**
     * 未付款
     */

    UNPAID(1, "未付款", "用户已成功下单，请注意查收汇款。", "【#app#】Verification Code#code#,Welcome to sign up Taohaifang,the verification code will be valid within 10 minutes."),
    /**
     * 已付款
     */
    ALREADY_PAID(2, "已付款", "已成功划转，请等待用户确认", "【#app#】code:#code#.valid for 5 minutes"),
    /**
     * 申诉中
     */
    APPEAL(3, "申诉中", "您已提交申诉，请等待审核。", "【#app#】Verification Code#code#,You are in the process of retrieving the password，the verification code will be valid within 10 minutes. Do not leak any information "),
    /**
     * 已取消
     */
    CANCELLED(4, "已取消", "【#app#】订单超时已被自动取消。", "【#app#】Verification Code#code#,You are in the process of binding mailbox,the verification code will be valid within 10 minutes. Do not leak any information."),
    /**
     * 已完成
     */
    COMPLETED(5, "已完成", "【#app#】订单已完成，如有疑问，请联系客服。", "【#app#】Verification Code#code#,You are in the process of binding cell phone number,the verification code will be valid within 10 minutes. Do not leak any information."),

    /**
     * 执行发币
     */
    EXECUTECURRENCY(6, "执行发币", "【#app#】订单已完成，如有疑问，请联系客服。", "【#app#】Verification Code#code#,You are in the process of binding cell phone number,the verification code will be valid within 10 minutes. Do not leak any information."),
    /**
     * 统合1，2，3的状态记录
     */
    EXECUTE(7, "执行发币", "【#app#】统合1，2，3的状态记录", "【#app#】统合1，2，3的状态记录");

    private Integer value;
    private String type;
    private String template;
    private String enTemplate;

    private OrderStateEnum(Integer value, String type, String template, String enTemplate) {
        this.value = value;
        this.type = type;
        this.template = template;
        this.enTemplate = enTemplate;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getTemplate() {
        return template;
    }

    public String getEnTemplate() {
        return enTemplate;
    }

    public String getType() {
        return type;
    }

    public static OrderStateEnum getInstance(Integer value) {
        if (value != null) {
            OrderStateEnum[] instArray = OrderStateEnum.values();
            for (OrderStateEnum instance : instArray) {
                if (instance.getValue() == value) {
                    return instance;
                }
            }

        }
        return null;
    }
}
