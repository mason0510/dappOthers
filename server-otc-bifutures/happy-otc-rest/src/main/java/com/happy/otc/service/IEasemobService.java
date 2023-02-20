package com.happy.otc.service;

import com.happy.otc.enums.OrderStateEnum;

import java.util.List;

public interface IEasemobService {
    /**
     * 创建用户
     * @param userId
     */
    String newUser(Long userId, String mobile, String nickName);

    /**
     * 创建群组
     *
     * @param ownerId      商品发布者id
     * @param orderId      下单人id
     * @param groupName    群组名
     * @param leaveMessage 自动回复信息
     * @return
     */
    String createChatGroups(Long ownerId, Long orderId, String groupName, String leaveMessage);

    /**
     * 创建群组
     * @param groupName 群组名
     * @param owner     群主
     * @param members   成员
     * @return  群编号
     */
    String createChatGroups(String groupName, String owner, String... members);

    /**
     * 删除群组
     * @param groupId
     */
    void delChatGroups(String groupId);

    /**
     * 群组添加成员
     * @param groupId
     * @param member
     * @return
     */
    Boolean addChatGroupsMember(String groupId, String member);

    /**
     * 获取群组成员
     * @param groupId
     * @return
     */
    List<String> getMember(String groupId);

    /**
     * 发送消息
     * @param from
     * @param target
     * @param message
     */
    void sendMessage(String from, String target, String message);

    /**
     * 判断用户是否存在
     * @param username
     * @return
     */
    Integer checkHasUser(String username);

    void sendOrderMessage(Long userId, String target, String message, String kind);

}
