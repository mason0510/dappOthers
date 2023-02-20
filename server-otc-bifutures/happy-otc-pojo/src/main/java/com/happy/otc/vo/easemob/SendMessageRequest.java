package com.happy.otc.vo.easemob;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SendMessageRequest implements Serializable {
    // users 给用户发消息。chatgroups: 给群发消息，chatrooms: 给聊天室发消息
    private String target_type;
    // 注意这里需要用数组，数组长度建议不大于20，即使只有一个用户，
    // 也要用数组 ['u1']，给用户发送时数组元素是用户名，给群组发送时
    // 数组元素是groupid
    private List<String> target;
    //消息内容，{"type" : "txt","msg" : "hello from rest" }
    private Map<String, String> msg;
    //表示消息发送者。无此字段Server会默认设置为"from":"admin"，有from字段但值为空串("")时请求失败
    private String from;

    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }

    public Map<String, String> getMsg() {
        return msg;
    }

    public void setMsg(Map<String, String> msg) {
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
