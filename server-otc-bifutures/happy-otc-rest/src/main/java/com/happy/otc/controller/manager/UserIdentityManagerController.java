package com.happy.otc.controller.manager;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.vo.PageResult;
import com.bitan.common.vo.Result;
import com.bitan.oauth.vo.UserInfoVO;
import com.happy.otc.service.IUserService;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.enums.IdentityRejectReasonEnum;
import com.happy.otc.enums.IdentityStautsEnum;
import com.happy.otc.service.IUserIdentityService;
import com.happy.otc.vo.manager.AddSellerRequest;
import com.happy.otc.vo.manager.SellerSearchVo;
import com.happy.otc.vo.manager.UserIdentityRequest;
import com.happy.otc.vo.manager.UserIdentitySearchVO;
import com.happy.otc.vo.UserIdentityVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "otc-rest/m")
@Api(value = "otc-rest/m", description = "后台管理 用户认证 API")
public class UserIdentityManagerController {
    @Autowired
    private IUserIdentityService userIdentityService;

    @Autowired
    private IOauthService oauthService;

    @Autowired
    private IUserService userService;

    /**
     * 获取实名认证列表
     * @param searchVO
     * @return
     */
    @ApiOperation(value = "实名认证列表")
    @RequestMapping(value = "/user-identity-list", method = RequestMethod.POST)
    public PageResult<List<UserIdentityVO>> list(@RequestBody UserIdentitySearchVO searchVO){

        BizException.isNull(searchVO.getCurrentPage(), "当前页");
        BizException.isNull(searchVO.getPageSize(), "每页条数");

        PageInfo<UserIdentityVO> pageList = userIdentityService.getPageList(searchVO);

        return new PageResult(pageList.getList(), pageList.getPageNum()
                , pageList.getPageSize(), pageList.getTotal());
    }

    @ApiOperation("审核实名认证")
    @RequestMapping(value = "/authentication", method = RequestMethod.POST)
    public Result<Boolean> authentication(@RequestBody UserIdentityRequest request){

        //参数校验
        BizException.isNull(request.getId(), "实名认证编号");
        if (request.getStatus() != IdentityStautsEnum.PASS.getValue().byteValue() && request.getStatus() != IdentityStautsEnum.REJECT.getValue().byteValue()) {
            BizException.fail(ApiResponseCode.PARA_ERR, null);
        }
        if (request.getStatus() == IdentityStautsEnum.REJECT.getValue().byteValue()) {
            if ((request.getRejectReasons() == null || request.getRejectReasons().length == 0) && StringUtils.isBlank(request.getOtherReason())) {
                BizException.fail(ApiResponseCode.PARA_MISSING_ERR, "审核不通过理由");
            }
        }

        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setId(request.getId());
        userIdentity.setStatus(request.getStatus());
        if (request.getStatus() == IdentityStautsEnum.REJECT.getValue().byteValue()) {
            StringBuilder rejectReason = new StringBuilder();
            for (int i = 0; i < request.getRejectReasons().length; i++) {
                IdentityRejectReasonEnum reasonEnum = IdentityRejectReasonEnum.valueOf(request.getRejectReasons()[i]);
                rejectReason.append(reasonEnum.getValue());
                if (i < request.getRejectReasons().length - 1) {
                    rejectReason.append(",");
                }
            }
            userIdentity.setRejectReason(rejectReason.toString());
            userIdentity.setOtherReason(request.getOtherReason());
        }
        Boolean result = userIdentityService.updateUserIdentity(userIdentity);

        return Result.createSuccess(result);
    }

    @ApiOperation("商户列表")
    @PostMapping("/seller-list")
    public PageResult<List<UserInfoVO>> sellerList(@RequestBody SellerSearchVo sellerSearchVo){

        BizException.isNull(sellerSearchVo.getCurrentPage(), "当前页");
        BizException.isNull(sellerSearchVo.getPageSize(), "每页条数");

        //获取商户列表
        PageInfo<UserIdentityVO> pageList = userIdentityService.getSellerPageList(sellerSearchVo);

        //获取商户的信息
        List<Long> ids = new ArrayList<>();
        for (UserIdentityVO item : pageList.getList()) {
            ids.add(item.getUserId());
        }

        if (ids.isEmpty()){
            return new PageResult(new ArrayList(), pageList.getPageNum()
                    , pageList.getPageSize(), pageList.getTotal());
        }
        Result<Map<Long, UserInfoVO>> userInfoList = oauthService.getUserInfoByUserIdList(ids);

        return new PageResult(new ArrayList(userInfoList.getData().values()), pageList.getPageNum()
                , pageList.getPageSize(), pageList.getTotal());
    }

    @ApiOperation("添加商户")
    @PostMapping("/add-seller")
    public Result<Integer> addSeller(@RequestBody AddSellerRequest request) {
        BizException.isNull(request.getUserIdList(), "用户");
        Integer result = userIdentityService.addSeller(request.getUserIdList());
        return Result.createSuccess(result);
    }
}
