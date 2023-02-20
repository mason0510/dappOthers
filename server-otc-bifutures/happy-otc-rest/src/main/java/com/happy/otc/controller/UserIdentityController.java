package com.happy.otc.controller;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.login.LoginDataHelper;
import com.bitan.common.utils.MaskUtil;
import com.bitan.common.vo.Result;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.enums.LanguageEnum;
import com.happy.otc.proto.ResultInfo;
import com.happy.otc.service.IUserIdentityService;
import com.happy.otc.proto.AuthInfo;
import com.happy.otc.util.ProtoUtils;
import com.happy.otc.vo.UserIdentityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(value = "/otc-rest")
@Api(value = "/otc-rest", description = "用户认证Api")
public class UserIdentityController {

    @Autowired
    private IUserIdentityService userIdentityService;

    /**
     * 用户实名认证
     * @return
     */
    @ApiOperation("实名认证")
    @PostMapping("/authentication")
    public ResultInfo.ResultBooleanVO authentication(@RequestBody AuthInfo.AuthInfoVO authInfoVO){

        //参数验证
        BizException.isNull(authInfoVO.getRealName(), "真实姓名");
        BizException.isNull(authInfoVO.getDocumentType(), "证件类别");
        BizException.isNull(authInfoVO.getIdentityNumber(), "证件号码");
        BizException.isNull(authInfoVO.getImageAddress1(), "证件照正面");
        if (AuthInfo.AuthType.ID_CARD.equals(authInfoVO.getDocumentType())) {
            BizException.isNull(authInfoVO.getImageAddress2(), "证件照反面");
        }
        BizException.isNull(authInfoVO.getImageAddress3(), "手持证件照");

        Long userId = LoginDataHelper.getUserId();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUserId(userId);
        userIdentity.setDocumentType((byte) authInfoVO.getDocumentType().getNumber());
        userIdentity.setRealName(authInfoVO.getRealName());
        userIdentity.setIdentityNumber(authInfoVO.getIdentityNumber());
        userIdentity.setImageAddress1(authInfoVO.getImageAddress1());
        userIdentity.setImageAddress2(authInfoVO.getImageAddress2());
        userIdentity.setImageAddress3(authInfoVO.getImageAddress3());

        //添加处理
        Boolean result = userIdentityService.saveUserIdentity(userIdentity);

        return ProtoUtils.createBoolSuccess(result);
    }

    @ApiOperation("获取实名认证信息")
    @GetMapping("/user-identity-info")
    public ResultInfo.ResultAuthInfoVO getUserIdentity(){
        Long userId = LoginDataHelper.getUserId();

        UserIdentityVO userIdentityVO = userIdentityService.getByUserId(userId);
        if (userIdentityVO == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "数据不存在");
        }

        AuthInfo.AuthInfoVO.Builder authBuild = AuthInfo.AuthInfoVO.newBuilder();
        authBuild.setId(userIdentityVO.getId());
        authBuild.setDocumentType(AuthInfo.AuthType.forNumber(userIdentityVO.getDocumentType()));
        if (StringUtils.isNotBlank(userIdentityVO.getIdentityNumber())) {
            authBuild.setIdentityNumber(MaskUtil.maskUsername(userIdentityVO.getIdentityNumber()));
        }
        if (StringUtils.isNotBlank(userIdentityVO.getImageAddress1())) {
            authBuild.setImageAddress1(userIdentityVO.getImageAddress1());
        }
        if (StringUtils.isNotBlank(userIdentityVO.getImageAddress2())) {
            authBuild.setImageAddress2(userIdentityVO.getImageAddress2());
        }
        if (StringUtils.isNotBlank(userIdentityVO.getImageAddress3())) {
            authBuild.setImageAddress3(userIdentityVO.getImageAddress3());
        }
        if (StringUtils.isNotBlank(userIdentityVO.getRealName())) {
            authBuild.setRealName(userIdentityVO.getRealName());
        }
        if (StringUtils.isNotBlank(userIdentityVO.getOtherReason())) {
            authBuild.setOtherReason(userIdentityVO.getOtherReason());
        }
        authBuild.setStatus(AuthInfo.AuthStatus.forNumber(userIdentityVO.getStatus()));
        if (StringUtils.isNotBlank(userIdentityVO.getRejectReason())) {
            String[] reasons = userIdentityVO.getRejectReason().split(",");
            for (int i = 0; i < reasons.length; i++) {
                authBuild.addRejectReason(AuthInfo.AuthRejectReason.forNumber(Integer.parseInt(reasons[i])));
            }
        }

        ResultInfo.ResultAuthInfoVO resultAuthInfoVO = ResultInfo.ResultAuthInfoVO.getDefaultInstance();
        ResultInfo.ResultAuthInfoVO result = ProtoUtils.createResultSuccess(authBuild.build(), resultAuthInfoVO);
        return result;
    }

    @ApiOperation("判断是否为商户")
    @GetMapping("/check-is-seller")
    public ResultInfo.ResultBooleanVO checkIsSeller(){
        Long userId = LoginDataHelper.getUserId();
        Boolean result = userIdentityService.isSeller(userId);
        return ProtoUtils.createBoolSuccess(result);
    }

    @ApiOperation(value = "用户语言设置")
    @RequestMapping(value = "/set-user-language", method = RequestMethod.POST)
    public Result<Boolean> setUserLanguage(@ApiParam("语言 1 cn 2 en") @RequestHeader("languageType") Integer languageType) throws BizException {

        BizException.isNull(languageType, "语言类别");
        Long userId = LoginDataHelper.getUserId();
        UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);
        LanguageEnum languageEnum = LanguageEnum.getInstance(Integer.valueOf(languageType));
        if (Objects.isNull(languageEnum)){
            languageEnum = LanguageEnum.CHINESE;
        }
        userIdentity.setLanguageType(languageEnum.getValue().byteValue());
         userIdentityService.updateUserIdentity(userIdentity);

        return Result.createSuccess(true);

    }
}
