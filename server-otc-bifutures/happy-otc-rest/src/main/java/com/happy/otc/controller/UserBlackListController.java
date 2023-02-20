package com.happy.otc.controller;

import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.happy.otc.entity.UserBlackList;
import com.happy.otc.proto.BlackListInfo;
import com.happy.otc.proto.PageResultInfo;
import com.happy.otc.proto.ResultInfo;
import com.happy.otc.util.ProtoUtils;
import com.happy.otc.service.IUserBlackListService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "用户黑名单")
public class UserBlackListController {
    @Autowired
    private IUserBlackListService userBlackListService;

    @ApiOperation("添加被屏蔽人")
    @PostMapping("/add-black-list")
    public ResultInfo.ResultBooleanVO addBlackList(@ApiParam("被屏蔽人ID") @RequestParam("targetId") Long targetId){
        BizException.isNull(targetId, "被屏蔽人ID");
        Long userId = LoginDataHelper.getUserId();
        Boolean result = userBlackListService.addBlackList(userId, targetId);
        return ProtoUtils.createBoolSuccess(result);
    }

    @ApiOperation("删除被屏蔽人")
    @PostMapping("/del-black-list")
    public ResultInfo.ResultBooleanVO delBlackList(@ApiParam("被屏蔽人ID") @RequestParam("targetId") Long targetId){
        BizException.isNull(targetId, "被屏蔽人ID");
        Long userId = LoginDataHelper.getUserId();
        Boolean result = userBlackListService.delBlackList(userId, targetId);
        return ProtoUtils.createBoolSuccess(result);
    }

    @ApiOperation("屏蔽人列表")
    @PostMapping("/black-list")
    public PageResultInfo.PageResultBlackListInfoVO blackList(@ApiParam("当前页") @RequestParam("currentPage") Integer currentPage,
                                                              @ApiParam("每页大小") @RequestParam("pageSize") Integer pageSize) {
        BizException.isNull(currentPage, "当前页");
        BizException.isNull(pageSize, "每页大小");

        Long userId = LoginDataHelper.getUserId();
        PageInfo<UserBlackList> pageInfo = userBlackListService.selectByUserId(userId, currentPage, pageSize);
        List<BlackListInfo.BlackListInfoVO> result = new ArrayList<>();
        for (UserBlackList item : pageInfo.getList()) {
            BlackListInfo.BlackListInfoVO.Builder blackListBuilder = BlackListInfo.BlackListInfoVO.newBuilder();
            blackListBuilder.setBlackListId(item.getBlackListId());
            blackListBuilder.setUserId(item.getUserId());
            blackListBuilder.setTargetId(item.getTargetId());
            blackListBuilder.setTargetName(item.getTargetName());
            blackListBuilder.setCreateTime(item.getCreateTime().getTime());
            result.add(blackListBuilder.build());
        }
        PageResultInfo.PageResultBlackListInfoVO page = PageResultInfo.PageResultBlackListInfoVO.getDefaultInstance();
        return ProtoUtils.createPageResultSuccess(result, pageInfo.getPageNum(), pageInfo.getSize(), pageInfo.getTotal(), page);
    }

    @ApiOperation("判断是否被屏蔽")
    @PostMapping("/check-in-black-list")
    public ResultInfo.ResultBooleanVO checkInBlackList(@ApiParam("被屏蔽人ID") @RequestParam("targetId") Long targetId){
        BizException.isNull(targetId, "被屏蔽人ID");

        Long userId = LoginDataHelper.getUserId();
        UserBlackList blackList = userBlackListService.selectByUserIdAndTargetId(userId, targetId);
        return ProtoUtils.createBoolSuccess(blackList != null);
    }
}
