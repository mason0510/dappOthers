package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bitan.common.utils.*;
import com.happy.otc.enums.OrderStateEnum;
import com.happy.otc.service.IEasemobService;
import com.happy.otc.vo.easemob.EasemobToken;
import com.happy.otc.vo.easemob.ResultJson;
import com.happy.otc.vo.easemob.SendMessageRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class EasemobServiceImpl implements IEasemobService {

    private static final Logger logger = LogManager.getLogger(EasemobServiceImpl.class);

    @Value("${easemob.orgName}")
    private String orgName;
    @Value("${easemob.appName}")
    private String appName;
    @Value("${easemob.clientID}")
    private String clientID;
    @Value("${easemob.clientSecret}")
    private String clientSecret;
    @Value("${easemob.rootUrl}")
    private String rootUrl;
    @Value("${easemob.customerPrompt.username}")
    private String promptUsername;
    private final String tokenKey = "EASEMOB_TOKEN";
    private final String salt = "www.happy.exchange/otc!@#$%";

//    @Autowired
//    private EasemobMapper easemobMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public String newUser(Long userId, String mobile, String nickName) {
        /*//判断是否已注册
        Easemob easemob = easemobMapper.selectByUserId(userId);
        if (easemob != null) {
            BizException.fail(ApiResponseCode.DATA_EXIST, "已注册");
        }*/
        String ussername = "otc" + userId;
        String password = Md5Utils.parseStrToMd5L32(salt + mobile);

        //注册用户
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "Bearer " + getEasemobToken());
        String url = rootUrl + "users";
        Map<String, String> body = new HashMap<>();
        body.put("username", ussername);
        body.put("password", password);
        body.put("nickname", nickName);
        try {
            String json = EasemobHttpClientUtil.post(url, body, String.class, head);
            freshToken(json);
            logger.info("注册环信用户,{}", json);
        } catch (Exception e) {
            logger.error("注册环信用户失败,{}", e.getMessage());
            ussername = "";
        }

        /*
        2018/7/9 服务器不保存密码
        //记录环信账号
        Easemob newEasemob = new Easemob();
        newEasemob.setUserId(userId);
        newEasemob.setUsername(ussername);
        newEasemob.setPassword(password);
        newEasemob.setCreateTime(new Date());
        easemobMapper.insert(newEasemob);

        return newEasemob;*/
        return ussername;
    }

    /**
     * 获取环信TOKEN
     * @return
     */
    private String getEasemobToken(){

        String token = redisUtil.getStringValue(tokenKey);
        if (StringUtils.isEmpty(token)) {
            String url = rootUrl + "token";
            Map<String, String> body = new HashMap<>();
            body.put("grant_type", "client_credentials");
            body.put("client_id", clientID);
            body.put("client_secret", clientSecret);

            String tokenJson = null;
            try {
                tokenJson = EasemobHttpClientUtil.post(url, body, String.class);
                logger.info("获取环信token:{}", tokenJson);
            } catch (Exception e) {
                logger.error("获取环信token失败,{}", e.getMessage());
            }
            if (tokenJson != null) {
                EasemobToken easemobToken = JSONObject.parseObject(tokenJson, EasemobToken.class);

                token = easemobToken.getAccess_token();
                redisUtil.setStringValue(tokenKey, token, 5 *24 * 60* 60);
            }
        }
        return token;
    }

    /**
     * 创建群组
     * @param groupName 群组名
     * @param owner     群主名
     * @param members   成员名
     * @return
     */
    public String createChatGroups(String groupName, String owner, String... members){

        String url = rootUrl + "chatgroups";
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "Bearer " + getEasemobToken());
        Map<String, Object> body = new HashMap<>();
        //群组名称，此属性为必须的
        body.put("groupname", groupName);
        //群组描述，此属性为必须的
        body.put("desc", "对话群组");
        //是否是公开群，此属性为必须的
        body.put("public", false);
        //群组成员最大数（包括群主），值为数值类型，默认值200，最大值2000，此属性为可选的
        body.put("maxusers", 5);
        //加入群是否需要群主或者群管理员审批，默认是false
        body.put("members_only", true);
        //是否允许群成员邀请别人加入此群。 true：允许群成员邀请人加入此群，false：只有群主或者管理员才可以往群里加人。
        body.put("allowinvites", false);
        //群组的管理员，此属性为必须的
        body.put("owner", owner);
        //群组成员，此属性为可选的，但是如果加了此项，数组元素至少一个（注：群主jma1不需要写入到members里面）
        List<String> memberList = Arrays.asList(members);
        body.put("members", memberList);

        ResultJson resultJson = null;
        String json ="";
        try {
            json = EasemobHttpClientUtil.post(url, body, String.class, head);
            freshToken(json);
            resultJson = JSONObject.parseObject(json,ResultJson.class);
            logger.info("创建群组,{}", resultJson.toString());
        } catch (Exception e) {
            logger.error("创建群组失败,{}", e.getMessage());
        }

        String groupid = "";
        if (resultJson != null) {
            Map<String, String> group = (Map<String, String>) JSONObject.parse(resultJson.getData());
            groupid = group.get("groupid");
        }
        return groupid;
    }

    /**
     *  创建群组
     * @param ownerId           商品发布者id
     * @param orderId           下单人id
     * @param groupName         群组名
     * @param leaveMessage      自动回复信息
     * @return
     */
    public String createChatGroups(Long ownerId, Long orderId, String groupName, String leaveMessage){

        String ownerUsername = "otc" + ownerId;
        String orderUsername = "otc" + orderId;

        String groupid = createChatGroups(groupName, ownerUsername, orderUsername, promptUsername);
        //发送信息
        if (!StringUtils.isEmpty(groupid) && !StringUtils.isEmpty(leaveMessage)) {
            sendMessage(ownerUsername, groupid, leaveMessage);
        }
        return groupid;
    }

    /**
     * 发送信息
     * @param from
     * @param target
     */
    public void sendMessage(String from, String target, String message){
        String url = rootUrl + "messages";
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "Bearer " + getEasemobToken());

        SendMessageRequest body = new SendMessageRequest();
        //对象类型
        body.setTarget_type("chatgroups");
        //对象
        List<String> targetList = new ArrayList<>();
        targetList.add(target);
        body.setTarget(targetList);
        //信息
        Map<String, String> msg = new HashMap<>();
        msg.put("type", "txt");
        msg.put("msg", message);
        body.setMsg(msg);
        //发送者
        body.setFrom(from);

        ResultJson resultJson = null;
        String json = "";
        try {
            json = EasemobHttpClientUtil.post(url, body, String.class, head);
            freshToken(json);
            resultJson = JSONObject.parseObject(json,ResultJson.class);
            logger.info("发送信息,{}", resultJson.toString());
        } catch (Exception e) {
            logger.error("发送信息失败,{}", e.getMessage());
        }

    }
    @Override
    public void sendOrderMessage(Long userId, String target,String message,String kind){
            sendMessage(promptUsername,target,message);
    }


    /**
     * 删除群组
     *
     * @param groupId
     */
    @Override
    public void delChatGroups(String groupId) {
        String url = rootUrl + "chatgroups/" + groupId;
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "Bearer " + getEasemobToken());

        try {
            String result = EasemobHttpClientUtil.delete(url, head);
            freshToken(result);
            logger.info("删除群组,{}", groupId);
        } catch (Exception e) {
            logger.error("删除群组失败,{}", e.getMessage());
        }
    }

    public List<String> getMember(String groupId){
        String url = rootUrl + "chatgroups/" + groupId + "/users?pagenum=1&pagesize=10";
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "Bearer " + getEasemobToken());

        ResultJson resultJson = null;
        String json = "";
        try {
            json= EasemobHttpClientUtil.getOne(url, String.class, head);
            freshToken(json);
            resultJson = JSONObject.parseObject(json,ResultJson.class);
            logger.info("获取群组成员,{}", resultJson.getData());
        } catch (Exception e) {
            logger.error("获取群组成员失败,{}", e.getMessage());
            return null;
        }
        JSONArray list = JSONObject.parseArray(resultJson.getData());
        List<String> result = new ArrayList<>();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Map<String, String> member = (Map<String, String>) iter.next();
            String username = member.get("member");
            if (username == null) {
                username = member.get("owner");
            }
            if (!StringUtils.isEmpty(username)) {
                result.add(username);
            }
        }

        return result;
    }

    /**
     * 群组添加成员
     * @param groupId
     * @param member freshToken(result);
     * @return
     */
    @Override
    public Boolean addChatGroupsMember(String groupId, String member){
        String url = rootUrl + "chatgroups/" + groupId + "/users/" + member;
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "Bearer " + getEasemobToken());

        Boolean result = true;
        try {
            ResultJson resultJson = EasemobHttpClientUtil.post(url, null, ResultJson.class, head);
            logger.info("群组添加成员,{}", resultJson.toString());
        } catch (Exception e) {
            result = false;
            logger.error("群组添加成员失败,{}", e.getMessage());
        }
        return result;
    }

    /**
     * 判断用户是否存在
     * @param username
     * @return 0:不存在 1:存在 -1:异常
     */
    @Override
    public Integer checkHasUser(String username) {
        String url = rootUrl + "users/" + username;
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", "Bearer " + getEasemobToken());

        Integer result;
        try {
            String resultJson = EasemobHttpClientUtil.getOne(url, String.class, head);
            freshToken(resultJson);
            logger.info("用户存在,{}", username);
            result = 1;
        } catch (HttpInvokeException e) {
            if (e.getMessage().startsWith("http code : 404")) {
                logger.error("用户不存在,{}", username);
                result = 0;
            } else {
                result = -1;
            }
        } catch (Exception e) {
            result = -1;
        }
        return result;
    }

    /**
     * 刷新token
     */
    private void freshToken(String json){
        if (json.equals( "401" )){
            redisUtil.delete(tokenKey);
            getEasemobToken();
        }
    }
}
