package com.happy.otc.controller;

import com.happy.otc.service.IEasemobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "环信聊天Api")
public class EasemobController {

    @Autowired
    private IEasemobService easemobService;

    @ApiOperation(value = "环信消息发送")
    @GetMapping(value = "/easemob-message-send")
    public void easemobMessageSend(@RequestParam(value = "from") String from,
                                   @RequestParam(value = "groupId") String groupId,
                                   @RequestParam(value = "message") String message){
      easemobService.sendMessage(from, groupId, message);
    }

    @ApiOperation(value = "删除群组")
    @GetMapping(value = "/del-groupId")
    public void delChatGroups(@RequestParam(value = "groupId") String groupId){
        easemobService.delChatGroups(groupId);
    }
}
