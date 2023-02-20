package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.bitan.common.vo.Result;
import com.bitan.message.enums.MessageEnum;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.service.IUserService;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.contants.Contants;
import com.happy.otc.dao.UserIdentityMapper;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.UserIdentity;
import com.happy.otc.enums.IdentityStautsEnum;
import com.happy.otc.enums.UserOtcTypeEnum;
import com.happy.otc.proto.Account;
import com.happy.otc.service.IUserIdentityService;
import com.happy.otc.util.*;
import com.happy.otc.vo.UserIdentityVO;
import com.happy.otc.vo.manager.SellerSearchVo;
import com.happy.otc.vo.manager.UserIdentitySearchVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserIdentityServiceImpl implements IUserIdentityService {


    Logger logger = LoggerFactory.getLogger(UserIdentityServiceImpl.class);

    @Autowired
    UserIdentityMapper userIdentityMapper;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private IOauthService oauthService;
    @Autowired
    private IUserService userService;
    @Value("${capital.cipher.error.count}")
    private Integer cipherErrCount;
    private String bucketName = "happyex.oss";

    @Autowired
    private ThreadPoolTaskExecutor executor;

    /**
     * 实名认证处理
     * @param userIdentity
     * @return
     */
    @Transactional
    @Override
    public Boolean saveUserIdentity(UserIdentity userIdentity) {

        //查询该证件是否已被实名认证
        Map<String, Object> params = new HashMap<>();
        params.put("identityNumber", userIdentity.getIdentityNumber());
        params.put("notUserId", userIdentity.getUserId());
        params.put("status", IdentityStautsEnum.PASS.getValue());
        List<UserIdentityVO> userIdentityVOs = userIdentityMapper.selectByParam(params);
        if (userIdentityVOs != null && userIdentityVOs.size() > 0) {
            BizException.fail(MessageCode.IDENTITY_ALREADY_USED, "该证件已用于实名认证");
        }

        //证件为身份证时，校验身份证号码
        if (userIdentity.getDocumentType() == 0 && !JIdCardUtils.validate(userIdentity.getIdentityNumber())) {
            BizException.fail(MessageCode.IDENTITY_CODE_ERR, "身份证号码不正确");
        }

        //查询该用户是否已实名认证
        params.clear();
        params.put("userId", userIdentity.getUserId());
        userIdentityVOs = userIdentityMapper.selectByParam(params);
        Integer result = 0;
        Long identityId;
        if (userIdentityVOs != null && userIdentityVOs.size() > 0) {
            //----------已存在的情况-----------------
            UserIdentityVO old = userIdentityVOs.get(0);
            identityId = old.getId();
            //判断是否已通过实名认证
            if (IdentityStautsEnum.PASS.getValue().equals(old.getStatus())) {
                BizException.fail(ApiResponseCode.DATA_EXIST, "该用户已通过实名认证");
            }
            //修改处理
            UserIdentity oldUserIdentity = BeanUtils.copyObject(old, UserIdentity.class);
            oldUserIdentity.setDocumentType(userIdentity.getDocumentType());
            oldUserIdentity.setRealName(userIdentity.getRealName());
            oldUserIdentity.setIdentityNumber(userIdentity.getIdentityNumber());
            oldUserIdentity.setImageAddress1(userIdentity.getImageAddress1());
            oldUserIdentity.setImageAddress2(userIdentity.getImageAddress2());
            oldUserIdentity.setImageAddress3(userIdentity.getImageAddress3());
            oldUserIdentity.setStatus(IdentityStautsEnum.WAIT.getValue().byteValue());
            result = userIdentityMapper.updateByPrimaryKeySelective(oldUserIdentity);
        } else {
            //----------未存在的情况------------------
            //添加实名认证
            userIdentity.setStatus(IdentityStautsEnum.WAIT.getValue().byteValue());
            userIdentity.setCreateTime(new Date());
            result = userIdentityMapper.insert(userIdentity);
            identityId = userIdentity.getId();
        }

        /* 暂不使用
        //只有是身份证类别时才进行人脸识别
        if (userIdentity.getDocumentType() == 0 ){
            //人脸识别
            executor.execute( new Runnable() {
                @Override
                public void run() {

                    //获取人脸图片
                    File imageFile = AwsS3Util.getObject( bucketName, userIdentity.getImageAddress3() );
                    //图片转Base64
                    String imageStr = Img2Base64Util.getImgStr( imageFile.getAbsolutePath() );
                    //验证是否为本人照片
                    String json = FaceCheckUtils.checkFaceWithIDCard( userIdentity.getIdentityNumber(), userIdentity.getRealName(), imageStr );
                    logger.info("人脸识别:" + json);
                    JSONObject jsonObject = JSON.parseObject( json );
                    Boolean result = false;
                    String errMessage = null;
                    if (jsonObject.getInteger("ResponseCode") == 100) {
                        if (jsonObject.getInteger("Result") == (Integer) 1) {
                            result = true;
                        } else {
                            errMessage = jsonObject.getString("ResultText");
                        }
                    } else {
                        errMessage = jsonObject.getString("ResponseText");
                    }
                    //识别不通过，直接驳回
                    if (!result) {
                        logger.error( "身份表id: " + identityId + " 用户id " + userIdentity.getUserId() + "人脸识别未通过的理由： " + errMessage );
                        UserIdentity update = new UserIdentity();
                        update.setId( identityId );
                        update.setStatus( IdentityStautsEnum.REJECT.getValue().byteValue() );
                        update.setOtherReason( "手持身份证未通过，请重新验证" );
                        userIdentityMapper.updateByPrimaryKeySelective( update );
                    }
                }
            } );
        }*/
        return result > 0;
    }

    /**
     * 获取实名认证列表
     * @param searchVO
     * @return
     */
    @Override
    public PageInfo<UserIdentityVO> getPageList(UserIdentitySearchVO searchVO) {

        Map<String, Object> params = new HashMap<>();
        params.put("status",searchVO.getStatus());
        PageHelper.startPage(searchVO.getCurrentPage(), searchVO.getPageSize(), Contants.PAGEHELPER_COUNT
                ,Contants.PAGEHELPER_REASONABLE);
        List<UserIdentityVO> list = userIdentityMapper.selectByParam(params);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 修改实名认证信息
     * @param userIdentity
     * @return
     */
    @Override
    public Boolean updateUserIdentity(UserIdentity userIdentity) {

/*        //查询该证件是否已被实名认证
        Map<String, Object> params = new HashMap<>();
        params.put("identityNumber", userIdentity.getIdentityNumber());
        List<UserIdentityVO> userIdentityVOs = userIdentityMapper.selectByParam(params);
        if (userIdentityVOs != null && userIdentityVOs.size() > 0) {
            for (UserIdentityVO item : userIdentityVOs) {
                if (item.getId() != userIdentity.getId()) {
                    BizException.fail(ApiResponseCode.PARA_ERR, "该证件已用于实名认证");
                    return false;
                }
            }
        }*/

        //修改实名认证
        Integer result = userIdentityMapper.updateByPrimaryKeySelective(userIdentity);
        return result > 0;
    }

    /**
     * 根据用户ID，获取实名认证信息
     * @param userId    用户ID
     * @return
     */
    @Override
    public UserIdentityVO getByUserId(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<UserIdentityVO> list = userIdentityMapper.selectByParam(params);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 初始设置资金密码
     * @param userExtendInfo
     * @return
     */
    @Override
    public Boolean addUserExtendIdentity(Long userId,Account.UpdateCapitalCipherParamVO userExtendInfo){

        //校验用户是否当前手机号
        UserDTO user = userService.getUserInfoByUserId(userId);
        if (user == null) {
            BizException.fail(MessageCode.NO_USER, "该用户不存在");
        }
        if (!user.getMobile().equals(userExtendInfo.getMobile())){
            BizException.fail(MessageCode.NO_AUTHORITY, "当前用户无权限操作");
        }
        //校验资金密码是否与登录密码一致
        Result<Boolean> repeatCheck = oauthService.checkPasswordRepeat(userId, userExtendInfo.getCapitalCipher());
        if (repeatCheck.getData()) {
            BizException.fail(MessageCode.PASSWORD_REPAT_ERR, "登录密码不可与资金密码一致");
        }

        //校验验证码
        String redisCaptcha = redisUtil.getStringValue(userExtendInfo.getMobile() + "_" + MessageEnum.LOGIN.getValue());
        if (StringUtils.isEmpty(redisCaptcha)) {
            BizException.fail(MessageCode.CAPTCHA_EXPIRED, "验证码已过期");
        }
        if (!redisCaptcha.equals(userExtendInfo.getCaptcha())) {
            BizException.fail(MessageCode.CAPTCHA_ERR, "验证码不正确");
        }

        UserIdentity userIdentity = getUserExtendIdentity(userId);;
        userIdentity.setCapitalCipher(userExtendInfo.getCapitalCipher());
        Boolean result = updateUserIdentity(userIdentity);
        return  result;
    }

    /**
     * 根据用户ID，获取用户额外信息
     * @param userId    用户ID
     * @return
     */
    @Override
    public UserIdentity getUserExtendIdentity(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<UserIdentity> list = userIdentityMapper.selectByParam2(params);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取商户列表
     * @param searchVO
     * @return
     */
    @Override
    public PageInfo<UserIdentityVO> getSellerPageList(SellerSearchVo searchVO) {

        Map<String, Object> params = new HashMap<>();
        params.put("userOtcType", UserOtcTypeEnum.SELLER.getValue());
        PageHelper.startPage(searchVO.getCurrentPage(), searchVO.getPageSize());

        List<UserIdentityVO> list = userIdentityMapper.selectByParam(params);

        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 添加商户
     * @param userIds    用户ID
     * @return
     */
    @Override
    public Integer addSeller(List<Long> userIds) {
        Integer count = userIdentityMapper.addSeller(userIds);
        return count;
    }

    /**
     * 判断是否为商户
     * @param userId
     * @return
     */
    @Override
    public Boolean isSeller(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<UserIdentityVO> list = userIdentityMapper.selectByParam(params);
        UserIdentityVO userIdentityVO = null;
        if (list.size() == 0) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "不存在此用户");
        } else {
            userIdentityVO = list.get(0);
        }
        return UserOtcTypeEnum.SELLER.getValue().byteValue() == userIdentityVO.getUserOtcType();
    }

    /**
     * 判读资金密码是否错误
     * @param input
     * @param pw
     * @param userId
     */
    public void checkCapitalCipher(String input, String pw, Long userId){
        //判断资金密码是否正确
        if (!input.equals(pw)) {
            //资金密码错误，累积错误次数
            String count = redisUtil.getStringValue(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId);
            Integer intCount = org.apache.commons.lang3.StringUtils.isBlank(count) ? 0 : Integer.parseInt(count);
            if (intCount == 0 || intCount == cipherErrCount) {
                //设置次数及剩余时间
                redisUtil.setStringValue(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId, (intCount + 1) + "", 60 * 60 * 24);
            } else {
                //获取剩余过期时间
                Long time = redisUtil.getResource().ttl(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId);
                redisUtil.setStringValue(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId, (intCount + 1) + "", time.intValue());
            }

            BizException.fail(MessageCode.CAPITAL_CIPHER_ERR_EXTEND, (cipherErrCount - intCount - 1) + "");
        } else {
            //资金密码正确，清空错误次数
            redisUtil.delete(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId);
        }
    }
}
