package com.happy.otc.service.impl;

import com.alibaba.fastjson.JSON;
import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.bitan.common.vo.Result;
import com.bitan.message.enums.MessageEnum;
import com.bitan.oauth.vo.LoginRequest;
import com.bitan.oauth.vo.RegisterUserRequest;
import com.bitan.oauth.vo.UserInfoVO;
import com.happy.otc.contants.Contants;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.dao.ServiceGroupMapper;
import com.happy.otc.dao.UserIdentityMapper;
import com.happy.otc.dto.UserDTO;
import com.happy.otc.entity.*;
import com.happy.otc.enums.IdentityStautsEnum;
import com.happy.otc.enums.LanguageEnum;
import com.happy.otc.enums.UserOtcTypeEnum;
import com.happy.otc.service.*;
import com.happy.otc.service.remote.IOauthService;
import com.happy.otc.vo.RegisterOtcUserRequest;
import com.happy.otc.vo.UserIdentityVO;
import com.happy.otc.vo.UserOtcInfoVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    @Autowired
    IOauthService oauthService;
    @Autowired
    UserIdentityMapper userIdentityMapper;
    @Autowired
    IUserIdentityService userIdentityService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ICapitalService capitalService;
    @Autowired
    IUserCurrencyAddressService userCurrencyAddressService;
    @Autowired
    ICapitalDetailCountService capitalDetailCountService;
    @Autowired
    IEasemobService easemobService;
    @Autowired
    ServiceGroupMapper serviceGroupMapper;
    @Value("${easemob.customerService.username}")
    String customerService;
    /*@Autowired
    private EasemobMapper easemobMapper;*/
    @Autowired
    ThreadPoolTaskExecutor executor;
    @Autowired
    IUserAddressOrderService userAddressOrderService;

    /**
     * ????????????
     * @param applicationId
     * @param applicationClientType
     * @param deviceUUID
     * @param userOtcRequest
     * @return
     */
    @Transactional
    @Override
    public UserOtcInfoVO doRegister(Long applicationId, Integer applicationClientType, String deviceUUID, String userFrom, RegisterOtcUserRequest userOtcRequest) {

        RegisterUserRequest userRequest = new RegisterUserRequest();
        BeanUtils.copyProperties(userOtcRequest, userRequest);

        //?????????OTC??????????????????
        Boolean isOtc = true;
        //??????OTC???????????????
        Result<UserInfoVO> userInfoVOResult = oauthService.searchUserByMobile(userOtcRequest.getMobile());
        //???????????????????????????
        if (userInfoVOResult.getCode() != 0 || !userInfoVOResult.isSuccess()) {
            BizException.fail(ApiResponseCode.getByCode(userInfoVOResult.getCode()), userInfoVOResult.getMessage());
        }
        if (userInfoVOResult.getData() != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userInfoVOResult.getData().getUserId());
            List<UserIdentityVO> userIdentityVOS = userIdentityMapper.selectByParam(params);
            if (userIdentityVOS.size() > 0) {
                BizException.fail(MessageCode.ALREADY_REGISTER, "??????????????????");
            } else {
                isOtc = false;
            }
        }

        Result<UserInfoVO> userInfoDTO = oauthService.registerOtc(applicationId,applicationClientType,deviceUUID, userFrom, userRequest);
        //???????????????????????????
        if (userInfoDTO.getCode() != 0 || !userInfoDTO.isSuccess()) {
            BizException.fail(userInfoDTO.getCode(), userInfoDTO.getMessage());
        }

        Long userId = userInfoDTO.getData().getUserId();
        //??????OTC????????????
        addOtcUserInfo(userOtcRequest, userId);

        //??????????????????
        UserOtcInfoVO userOtcInfoVO = new UserOtcInfoVO();
        BeanUtils.copyProperties(userInfoDTO.getData(), userOtcInfoVO);
        userOtcInfoVO.setIdentityStatus(IdentityStautsEnum.NO.getValue());
        userOtcInfoVO.setOtcType(UserOtcTypeEnum.NORMAL.getValue());
//        userOtcInfoVO.setEasemobUsername(easemob.getUsername());
//        userOtcInfoVO.setEasemobPassword(easemob.getPassword());
        Map<String, Object> extend = userOtcInfoVO.getUserAccountExtend();
        if (extend == null) {
            extend = new HashMap<>();
        }
        extend.put("isOTC", isOtc);

        return userOtcInfoVO;
    }

    /**
     * ??????OTC????????????
     * @param applicationId
     * @param applicationClientType
     * @param deviceUUID
     * @param userOtcRequest
     * @return
     */
    @Transactional
    @Override
    public UserOtcInfoVO setUserExtendInfo(Long applicationId, Integer applicationClientType, String deviceUUID, RegisterOtcUserRequest userOtcRequest) {

        //???????????????
        String captcha = redisUtil.getStringValue(userOtcRequest.getMobile() + "_" + MessageEnum.REGISTER.getValue());
        if (StringUtils.isEmpty(captcha)) {
            BizException.fail(MessageCode.CAPTCHA_EXPIRED, "???????????????");
        }
        if (!userOtcRequest.getCaptcha().equals(captcha)) {
            BizException.fail(MessageCode.CAPTCHA_ERR, "???????????????");
        }

        //??????OTC???????????????
        Result<UserInfoVO> userInfoVOResult = oauthService.searchUserByMobile(userOtcRequest.getMobile());
        //???????????????????????????
        if (userInfoVOResult.getCode() != 0 || !userInfoVOResult.isSuccess()) {
            BizException.fail(ApiResponseCode.getByCode(userInfoVOResult.getCode()), userInfoVOResult.getMessage());
        }
        //????????????????????????????????????????????????
        if (userInfoVOResult.getData() == null) {
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, null);
        }

        Long userId = userInfoVOResult.getData().getUserId();
        //????????????????????????OTC??????
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<UserIdentityVO> userIdentityVOS = userIdentityMapper.selectByParam(params);
        if (userIdentityVOS.size() > 0) {
            BizException.fail(MessageCode.ALREADY_REGISTER, "??????????????????");
        }

        if (userOtcRequest.getPassword().equals(userOtcRequest.getCapitalCipher())) {
            BizException.fail(MessageCode.PASSWORD_REPAT_ERR, "???????????????????????????????????????");
        }

        RegisterUserRequest userRequest = new RegisterUserRequest();
        BeanUtils.copyProperties(userOtcRequest, userRequest);
        Result<UserInfoVO> userInfoDTO = oauthService.setOtcUserInfo(applicationId, applicationClientType, deviceUUID, userRequest);
        //???????????????????????????
        if (userInfoDTO.getCode() != 0 || !userInfoDTO.isSuccess()) {
            BizException.fail(ApiResponseCode.getByCode(userInfoDTO.getCode()), userInfoDTO.getMessage());
        }

        //??????OTC????????????
        addOtcUserInfo(userOtcRequest, userId);

        //??????????????????
        UserOtcInfoVO userOtcInfoVO = new UserOtcInfoVO();
        BeanUtils.copyProperties(userInfoDTO.getData(), userOtcInfoVO);
        userOtcInfoVO.setIdentityStatus(IdentityStautsEnum.NO.getValue());
        userOtcInfoVO.setOtcType(UserOtcTypeEnum.NORMAL.getValue());
        Map<String, Object> extend = userOtcInfoVO.getUserAccountExtend();
        if (extend == null) {
            extend = new HashMap<>();
        }
        extend.put("isOTC", false);

        return userOtcInfoVO;
    }

    /**
     * ??????OTC????????????
     * @param userOtcRequest
     * @param userId
     */
    private void addOtcUserInfo(RegisterOtcUserRequest userOtcRequest, Long userId) {

        UserIdentity record = new UserIdentity();
        record.setUserId(userId);
        //record.setCapitalCipher(userOtcRequest.getCapitalCipher());
        if (!Objects.isNull(userOtcRequest.getLanguageType())){
            LanguageEnum languageEnum = LanguageEnum.getInstance(Integer.valueOf(userOtcRequest.getLanguageType()));
            if (Objects.isNull(languageEnum)){
                languageEnum = LanguageEnum.CHINESE;
            }
            record.setLanguageType(languageEnum.getValue().byteValue());
        }

        userIdentityMapper.insertSelective(record);

        logger.info("???????????????????????????????????????????????????, ??????id??? " + userId);

        //?????????????????????????????????
        capitalService.addCapitalInfo(userId);
        //??????????????????????????????????????????
        CapitalDetailCount capitalDetailCount = new CapitalDetailCount();
        capitalDetailCount.setUserId(userId);
        capitalDetailCountService.addCapitalDetailCount(capitalDetailCount);
        UserAddressOrder userAddressOrder = new UserAddressOrder();
        userAddressOrder.setUserId(userId);
       userAddressOrderService.addUserAddressOrder(userAddressOrder);
        //???????????????????????????
        UserCurrencyAddress address = new UserCurrencyAddress();
        address.setUserId(userId);
        address.setUserAddressId(userAddressOrder.getUserAddressId());
        userCurrencyAddressService.addUserCurrencyAddress(address);

        String mobile = userOtcRequest.getMobile();
        String nickName = userOtcRequest.getUserName();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //??????????????????
                String username = easemobService.newUser(userId, mobile, nickName);
                //??????????????????????????????
                String groupName = "service_" + username;
                String groupId = easemobService.createChatGroups(groupName, customerService, username);

                if (!StringUtils.isEmpty(groupId)) {

                    LanguageEnum languageEnum = LanguageEnum.CHINESE;
                    UserIdentity userIdentity = userIdentityService.getUserExtendIdentity(userId);

                    if (!Objects.isNull(userIdentity.getLanguageType())){
                        languageEnum = LanguageEnum.getInstance(Integer.valueOf(userIdentity.getLanguageType()));
                        if (Objects.isNull(languageEnum)) {
                            languageEnum = LanguageEnum.CHINESE;
                        }
                    }
                    //????????????
                    if (LanguageEnum.CHINESE == languageEnum) {
                        easemobService.sendMessage(customerService, groupId, "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                    } else {
                        easemobService.sendMessage(customerService, groupId, "Once you have any suggestion or advice, plz leave your comment and we'll reply you asap.");
                    }

                    //??????????????????
                    ServiceGroup serviceGroup = new ServiceGroup();
                    serviceGroup.setUserId(userId);
                    serviceGroup.setGroupId(groupId);
                    serviceGroup.setCreateTime(new Date());
                    serviceGroupMapper.insert(serviceGroup);
                }
            }
        });
    }

    /**
     * ????????????
     * @param applicationId
     * @param applicationClientType
     * @param deviceUUID
     * @param loginRequest
     * @return
     */
    @Transactional
    @Override
    public UserOtcInfoVO doLogin(Long applicationId, Integer applicationClientType, String deviceUUID, LoginRequest loginRequest) {

        //????????????????????????????????????
        Result<UserInfoVO> userInfoVOResult = oauthService.searchUserByMobile(loginRequest.getAccount().trim());
        //???????????????????????????
        if (userInfoVOResult.getCode() != 0 || !userInfoVOResult.isSuccess()){
            BizException.fail(ApiResponseCode.getByCode(userInfoVOResult.getCode()),userInfoVOResult.getMessage());
        }
        if (userInfoVOResult.getData() == null) {
            BizException.fail(MessageCode.NO_USER, "??????????????????");
        }

        if (!userInfoVOResult.getData().getCountryCode().equals(loginRequest.getCountryCode())) {
            BizException.fail(MessageCode.COUNTRY_CODE_ERR, "??????????????????????????????");
        }

        Long userId = userInfoVOResult.getData().getUserId();
        String nickName = userInfoVOResult.getData().getUserName();

        //????????????????????????
        Result<Boolean> booleanResult = oauthService.checkPasswordRepeat(userId, loginRequest.getPassword());
        if (booleanResult.getCode() != 0 || !booleanResult.isSuccess()) {
            BizException.fail(ApiResponseCode.getByCode(userInfoVOResult.getCode()), userInfoVOResult.getMessage());
        }

        if (!booleanResult.getData()) {
            BizException.fail(MessageCode.PASSWORD_ERR, "??????????????????");
        }

        //???????????????OTC????????????
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<UserIdentity> list = userIdentityMapper.selectByParam2(params);
        if (list.size() == 0) {
            BizException.fail(MessageCode.OTC_NOT_REGISTER, userInfoVOResult.getData().getUserName());
        }

        //????????????
        Result<UserInfoVO> userInfoVO = oauthService.login(applicationId,applicationClientType,deviceUUID,loginRequest);
        //???????????????????????????
        if (userInfoVO.getCode() != 0 || !userInfoVO.isSuccess()){
            BizException.fail(ApiResponseCode.getByCode(userInfoVO.getCode()),userInfoVO.getMessage());
        }

        UserOtcInfoVO userOtcInfoVO = new UserOtcInfoVO();
        BeanUtils.copyProperties(userInfoVO.getData(), userOtcInfoVO);

        userOtcInfoVO.setIdentityStatus((int) list.get(0).getStatus());
        userOtcInfoVO.setOtcType((int) list.get(0).getUserOtcType());
        userOtcInfoVO.setCapitalPasswordStatus(StringUtils.isEmpty(list.get(0).getCapitalCipher())? false : true);
        /*//??????????????????
        Easemob easemob = easemobMapper.selectByUserId(userId);
        if (easemob == null) {
            //???????????????????????????????????????
            easemobService.newUser(userId, loginRequest.getAccount());
        }*/

        //????????????????????????????????????????????????????????????
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String username = "otc" + userId;
                Integer result = easemobService.checkHasUser(username);
                if (result == 0) {
                    //????????????
                    easemobService.newUser(userId, loginRequest.getAccount(), nickName);
                }
            }
        });

        return userOtcInfoVO;
    }

    @Override
    public String getCustomerServiceGroup(Long userId) {
        ServiceGroup serviceGroup = serviceGroupMapper.selectByPrimaryKey(userId);
        if (serviceGroup == null) {
            String username = "otc" + userId;
            //??????????????????????????????
            String groupName = "service_" + username;
            String groupId = easemobService.createChatGroups(groupName, customerService, username);
            //??????????????????
            if (!StringUtils.isEmpty(groupId)) {
                serviceGroup = new ServiceGroup();
                serviceGroup.setUserId(userId);
                serviceGroup.setGroupId(groupId);
                serviceGroup.setCreateTime(new Date());
                serviceGroupMapper.insert(serviceGroup);
            }
        }
        return serviceGroup == null ? "" : serviceGroup.getGroupId();
    }

    @Override
    public Boolean updatePassword(Long applicationId, String countryCode, String account, String newPassword, String oldPassword, String captcha) {
        //???????????????
        String redisCaptcha = redisUtil.getStringValue(account + "_" + MessageEnum.MODIFY_PASSWORD.getValue());
        if (StringUtils.isEmpty(redisCaptcha)) {
            BizException.fail(MessageCode.CAPTCHA_EXPIRED, "??????????????????");
        }
        if (!redisCaptcha.equals(captcha)) {
            BizException.fail(MessageCode.CAPTCHA_ERR, "??????????????????");
        }

        Result<UserInfoVO> userInfoVOResult = oauthService.searchUserByMobile(account);
        if (!userInfoVOResult.isSuccess()) {
            BizException.fail(ApiResponseCode.getByCode(userInfoVOResult.getCode()), userInfoVOResult.getMessage());
        }
        if (userInfoVOResult.getData() == null) {
            BizException.fail(MessageCode.NO_USER, "??????????????????");
        }

//        if (!userInfoVOResult.getData().getCountryCode().equals(countryCode)) {
//            BizException.fail(MessageCode.COUNTRY_CODE_ERR, "??????????????????????????????");
//        }

        //??????????????????
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userInfoVOResult.getData().getUserId());
        List<UserIdentity> list = userIdentityMapper.selectByParam2(params);
        if (list.size() == 0) {
            BizException.fail(MessageCode.NO_USER, "??????????????????");
        }
        UserIdentity userIdentity = list.get(0);
        //?????????????????????????????????????????????
        if (newPassword.equals(userIdentity.getCapitalCipher())) {
            BizException.fail(MessageCode.PASSWORD_REPAT_ERR, "???????????????????????????????????????");
        }
        //????????????
        Result<Boolean> result = oauthService.updatePassword(applicationId, account, newPassword, oldPassword, captcha);

        return result.getData();
    }

    @Override
    public Boolean forgotPassword(String countryCode, String account, String password, String captcha) {
        //???????????????
        String redisCaptcha = redisUtil.getStringValue(account + "_" + MessageEnum.FORGET_PASSWORD.getValue());
        if (StringUtils.isEmpty(redisCaptcha)) {
            BizException.fail(MessageCode.CAPTCHA_EXPIRED, "??????????????????");
        }
        if (!redisCaptcha.equals(captcha)) {
            BizException.fail(MessageCode.CAPTCHA_ERR, "??????????????????");
        }

        Result<UserInfoVO> userInfoVOResult = oauthService.searchUserByMobile(account);
        if (!userInfoVOResult.isSuccess()) {
            BizException.fail(ApiResponseCode.getByCode(userInfoVOResult.getCode()), userInfoVOResult.getMessage());
        }
        if (userInfoVOResult.getData() == null) {
            BizException.fail(MessageCode.NO_USER, "??????????????????");
        }

        if (!userInfoVOResult.getData().getCountryCode().equals(countryCode)) {
            BizException.fail(MessageCode.COUNTRY_CODE_ERR, "??????????????????????????????");
        }

        //??????????????????
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userInfoVOResult.getData().getUserId());
        List<UserIdentity> list = userIdentityMapper.selectByParam2(params);
        if (list.size() == 0) {
            BizException.fail(MessageCode.NO_USER, "??????????????????");
        }
        UserIdentity userIdentity = list.get(0);
        //?????????????????????????????????????????????
        if (password.equals(userIdentity.getCapitalCipher())) {
            BizException.fail(MessageCode.PASSWORD_REPAT_ERR, "???????????????????????????????????????");
        }
        //????????????
        Result<Boolean> result = oauthService.forgotPassword(account, password, captcha);

        return result.getData();
    }

    /**
     * ???????????????????????????
     * @param userId
     * @return
     */
    @Override
    public UserDTO getUserInfoByUserId(Long userId) {
        //???????????????????????????
        String userInfo = redisUtil.getStringValue(Contants.REDIS_USER_INFO + userId);
        if (StringUtils.isEmpty(userInfo)) {

            Result<UserInfoVO> userInfoResult = oauthService.getUserInfo(userId);
            //????????????????????????
            if (!userInfoResult.isSuccess()) {
                BizException.fail(MessageCode.GET_USER_INFO_ERR, "????????????????????????");
            }
            //?????????????????????
            UserInfoVO userInfoVO = userInfoResult.getData();
            if (userInfoVO == null) {
                BizException.fail(ApiResponseCode.DATA_EXIST, null);
            }
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(userId);
            userDTO.setEmail(userInfoVO.getEmail());
            userDTO.setMobile(userInfoVO.getMobile());
            userDTO.setUserName(userInfoVO.getUserName());
            userDTO.setCountryCode(userInfoVO.getCountryCode());
            userInfo = JSON.toJSONString(userDTO);

            //???????????????????????????
            redisUtil.setStringValue(Contants.REDIS_USER_INFO + userId, userInfo);

            return userDTO;
        }
        UserDTO userDTO = JSON.parseObject(userInfo, UserDTO.class);
        return userDTO;
    }

    /**
     * ?????????????????????????????????
     * @param userIds
     * @return
     */
    @Override
    public Map<Long, UserDTO> UserInfoByUserIds(List<Long> userIds) {
        Map<Long, UserDTO> userDTOMap = new HashMap<>();
        List<Long> noFindList = new ArrayList<>();
        for (Long userId : userIds) {
            //???????????????????????????
            String userInfo = redisUtil.getStringValue(Contants.REDIS_USER_INFO + userId);
            if (StringUtils.isEmpty(userInfo)) {
                //??????????????????
                noFindList.add(userId);
            } else {
                //???????????????
                UserDTO userDTO = JSON.parseObject(userInfo, UserDTO.class);
                userDTOMap.put(userDTO.getUserId(), userDTO);
            }
        }
        //????????????????????????????????????????????????????????????
        if (noFindList.size() > 0) {
            Result<Map<Long, UserInfoVO>> userInfoByUserIdList = oauthService.getUserInfoByUserIdList(noFindList);
            //????????????????????????
            if (!userInfoByUserIdList.isSuccess()) {
                BizException.fail(ApiResponseCode.EXTERNAL_SERVICE__EXP, null);
            }
            //?????????????????????
            Map<Long, UserInfoVO> userMap = userInfoByUserIdList.getData();
            if (userMap.isEmpty()) {
                BizException.fail(ApiResponseCode.DATA_EXIST, null);
            }
            for (UserInfoVO userInfoVO : userMap.values()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(userInfoVO.getUserId());
                userDTO.setEmail(userInfoVO.getEmail());
                userDTO.setMobile(userInfoVO.getMobile());
                userDTO.setUserName(userInfoVO.getUserName());
                userDTO.setCountryCode(userInfoVO.getCountryCode());
                userDTOMap.put(userDTO.getUserId(), userDTO);

                //???????????????????????????
                String userInfo = JSON.toJSONString(userDTO);
                redisUtil.setStringValue(Contants.REDIS_USER_INFO + userInfoVO.getUserId(), userInfo);
            }
        }
        return userDTOMap;
    }

    /**
     * ?????????????????????????????????
     * @param easemobUsername
     * @return
     */
    @Override
    public String getNicknameByEasemobUsername(String easemobUsername) {
        String nickname;
        //??????
        if (customerService.equals(easemobUsername)) {
            nickname = "??????";
        } else {
            Long userId = 0l;
            try {
                userId = Long.parseLong(easemobUsername.replace("otc", ""));
            } catch (NumberFormatException e) {
                BizException.fail(ApiResponseCode.PARA_ERR, "??????ID?????????");
            }
            UserDTO user = getUserInfoByUserId(userId);
            nickname = user.getUserName();
        }
        return nickname;
    }
}
