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
     * ??????????????????
     * @param userIdentity
     * @return
     */
    @Transactional
    @Override
    public Boolean saveUserIdentity(UserIdentity userIdentity) {

        //???????????????????????????????????????
        Map<String, Object> params = new HashMap<>();
        params.put("identityNumber", userIdentity.getIdentityNumber());
        params.put("notUserId", userIdentity.getUserId());
        params.put("status", IdentityStautsEnum.PASS.getValue());
        List<UserIdentityVO> userIdentityVOs = userIdentityMapper.selectByParam(params);
        if (userIdentityVOs != null && userIdentityVOs.size() > 0) {
            BizException.fail(MessageCode.IDENTITY_ALREADY_USED, "??????????????????????????????");
        }

        //?????????????????????????????????????????????
        if (userIdentity.getDocumentType() == 0 && !JIdCardUtils.validate(userIdentity.getIdentityNumber())) {
            BizException.fail(MessageCode.IDENTITY_CODE_ERR, "????????????????????????");
        }

        //????????????????????????????????????
        params.clear();
        params.put("userId", userIdentity.getUserId());
        userIdentityVOs = userIdentityMapper.selectByParam(params);
        Integer result = 0;
        Long identityId;
        if (userIdentityVOs != null && userIdentityVOs.size() > 0) {
            //----------??????????????????-----------------
            UserIdentityVO old = userIdentityVOs.get(0);
            identityId = old.getId();
            //?????????????????????????????????
            if (IdentityStautsEnum.PASS.getValue().equals(old.getStatus())) {
                BizException.fail(ApiResponseCode.DATA_EXIST, "??????????????????????????????");
            }
            //????????????
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
            //----------??????????????????------------------
            //??????????????????
            userIdentity.setStatus(IdentityStautsEnum.WAIT.getValue().byteValue());
            userIdentity.setCreateTime(new Date());
            result = userIdentityMapper.insert(userIdentity);
            identityId = userIdentity.getId();
        }

        /* ????????????
        //????????????????????????????????????????????????
        if (userIdentity.getDocumentType() == 0 ){
            //????????????
            executor.execute( new Runnable() {
                @Override
                public void run() {

                    //??????????????????
                    File imageFile = AwsS3Util.getObject( bucketName, userIdentity.getImageAddress3() );
                    //?????????Base64
                    String imageStr = Img2Base64Util.getImgStr( imageFile.getAbsolutePath() );
                    //???????????????????????????
                    String json = FaceCheckUtils.checkFaceWithIDCard( userIdentity.getIdentityNumber(), userIdentity.getRealName(), imageStr );
                    logger.info("????????????:" + json);
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
                    //??????????????????????????????
                    if (!result) {
                        logger.error( "?????????id: " + identityId + " ??????id " + userIdentity.getUserId() + "????????????????????????????????? " + errMessage );
                        UserIdentity update = new UserIdentity();
                        update.setId( identityId );
                        update.setStatus( IdentityStautsEnum.REJECT.getValue().byteValue() );
                        update.setOtherReason( "??????????????????????????????????????????" );
                        userIdentityMapper.updateByPrimaryKeySelective( update );
                    }
                }
            } );
        }*/
        return result > 0;
    }

    /**
     * ????????????????????????
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
     * ????????????????????????
     * @param userIdentity
     * @return
     */
    @Override
    public Boolean updateUserIdentity(UserIdentity userIdentity) {

/*        //???????????????????????????????????????
        Map<String, Object> params = new HashMap<>();
        params.put("identityNumber", userIdentity.getIdentityNumber());
        List<UserIdentityVO> userIdentityVOs = userIdentityMapper.selectByParam(params);
        if (userIdentityVOs != null && userIdentityVOs.size() > 0) {
            for (UserIdentityVO item : userIdentityVOs) {
                if (item.getId() != userIdentity.getId()) {
                    BizException.fail(ApiResponseCode.PARA_ERR, "??????????????????????????????");
                    return false;
                }
            }
        }*/

        //??????????????????
        Integer result = userIdentityMapper.updateByPrimaryKeySelective(userIdentity);
        return result > 0;
    }

    /**
     * ????????????ID???????????????????????????
     * @param userId    ??????ID
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
     * ????????????????????????
     * @param userExtendInfo
     * @return
     */
    @Override
    public Boolean addUserExtendIdentity(Long userId,Account.UpdateCapitalCipherParamVO userExtendInfo){

        //?????????????????????????????????
        UserDTO user = userService.getUserInfoByUserId(userId);
        if (user == null) {
            BizException.fail(MessageCode.NO_USER, "??????????????????");
        }
        if (!user.getMobile().equals(userExtendInfo.getMobile())){
            BizException.fail(MessageCode.NO_AUTHORITY, "???????????????????????????");
        }
        //?????????????????????????????????????????????
        Result<Boolean> repeatCheck = oauthService.checkPasswordRepeat(userId, userExtendInfo.getCapitalCipher());
        if (repeatCheck.getData()) {
            BizException.fail(MessageCode.PASSWORD_REPAT_ERR, "???????????????????????????????????????");
        }

        //???????????????
        String redisCaptcha = redisUtil.getStringValue(userExtendInfo.getMobile() + "_" + MessageEnum.LOGIN.getValue());
        if (StringUtils.isEmpty(redisCaptcha)) {
            BizException.fail(MessageCode.CAPTCHA_EXPIRED, "??????????????????");
        }
        if (!redisCaptcha.equals(userExtendInfo.getCaptcha())) {
            BizException.fail(MessageCode.CAPTCHA_ERR, "??????????????????");
        }

        UserIdentity userIdentity = getUserExtendIdentity(userId);;
        userIdentity.setCapitalCipher(userExtendInfo.getCapitalCipher());
        Boolean result = updateUserIdentity(userIdentity);
        return  result;
    }

    /**
     * ????????????ID???????????????????????????
     * @param userId    ??????ID
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
     * ??????????????????
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
     * ????????????
     * @param userIds    ??????ID
     * @return
     */
    @Override
    public Integer addSeller(List<Long> userIds) {
        Integer count = userIdentityMapper.addSeller(userIds);
        return count;
    }

    /**
     * ?????????????????????
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
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "??????????????????");
        } else {
            userIdentityVO = list.get(0);
        }
        return UserOtcTypeEnum.SELLER.getValue().byteValue() == userIdentityVO.getUserOtcType();
    }

    /**
     * ??????????????????????????????
     * @param input
     * @param pw
     * @param userId
     */
    public void checkCapitalCipher(String input, String pw, Long userId){
        //??????????????????????????????
        if (!input.equals(pw)) {
            //???????????????????????????????????????
            String count = redisUtil.getStringValue(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId);
            Integer intCount = org.apache.commons.lang3.StringUtils.isBlank(count) ? 0 : Integer.parseInt(count);
            if (intCount == 0 || intCount == cipherErrCount) {
                //???????????????????????????
                redisUtil.setStringValue(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId, (intCount + 1) + "", 60 * 60 * 24);
            } else {
                //????????????????????????
                Long time = redisUtil.getResource().ttl(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId);
                redisUtil.setStringValue(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId, (intCount + 1) + "", time.intValue());
            }

            BizException.fail(MessageCode.CAPITAL_CIPHER_ERR_EXTEND, (cipherErrCount - intCount - 1) + "");
        } else {
            //???????????????????????????????????????
            redisUtil.delete(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId);
        }
    }
}
