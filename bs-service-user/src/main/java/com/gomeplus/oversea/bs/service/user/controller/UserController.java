package com.gomeplus.oversea.bs.service.user.controller;

import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.gomeplus.oversea.bs.service.user.cache.CacheKey;
import com.gomeplus.oversea.bs.service.user.client.UserIdClient;
import com.gomeplus.oversea.bs.service.user.constant.UserConstants;
import com.gomeplus.oversea.bs.service.user.dto.SmsVerifyRequestDTO;
import com.gomeplus.oversea.bs.service.user.dto.user.*;
import com.gomeplus.oversea.bs.service.user.enums.RegisterOriginEnum;
import com.gomeplus.oversea.bs.service.user.enums.UserStatusEnum;
import com.gomeplus.oversea.bs.service.user.service.ImService;
import com.gomeplus.oversea.bs.service.user.service.SmsService;
import com.gomeplus.oversea.bs.service.user.service.common.RedisService;
import com.gomeplus.oversea.bs.service.user.util.CommonUtils;
import com.gomeplus.oversea.bs.service.user.util.PasswordUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import com.gomeplus.oversea.bs.service.user.dao.UserDao;
import com.gomeplus.oversea.bs.service.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * 用户模块相关接口
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RedisService redisService;


    @RequestMapping(value="testRedis",method = RequestMethod.GET)
    public String testRedis() {
        redisService.set("abc",System.currentTimeMillis());
        String str=redisService.get("abc").toString();
        return str;
    }

    @Autowired
    private UserDao userDao;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ImService imService;

    @Autowired
    private UserIdClient userIdClient;

    @GetMapping()
    public String test(){

        UserIdDto userId = userIdClient.getUserId();
        return userId.getId().toString();
    }


    @GetMapping("/user")
    public UserVO get(@RequestHeader("X-Gomeplus-User-Id") String userId){

        log.info("get user params userId=[{}]",userId);

        if(StringUtils.isEmpty(userId)){
            throw new C422Exception("参数校验失败");
        }

        Long id = Long.parseLong(userId);

        User user = userDao.findOne(id);

        if(user == null){
            log.error("get user is not exist");
            throw new C422Exception("该用户不存在");
        }

        UserResponseDto userResponseDto = buildUserResponseDto(user);

        return userResponseDto.getUser();
    }

    /**
     * 用户注册
     * @param requestUser
     */
    @PostMapping("/user")
    public UserResponseDto register(@RequestBody UserRequestDto requestUser,@RequestHeader("X-Gomeplus-Device") String device) throws Exception {

        log.info("register request params = [{}]",requestUser);
        long beginTime=System.currentTimeMillis();

        String countryCode = requestUser.getCountryCode();
        String smsCode = requestUser.getSmsCode();
        String mobile = requestUser.getMobile();
        String password = requestUser.getPassword();
        //String refereeUserId = requestUser.getRefereeUserId();
        String smsToken = requestUser.getSmsToken();

        //非空校验
        if(StringUtils.isEmpty(mobile)
                || StringUtils.isEmpty(countryCode)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(smsCode)
                || StringUtils.isEmpty(smsToken)
                || StringUtils.isEmpty(device)){
            log.error("register exception:params is null");
            throw new C422Exception("数据校验失败");
        }

        //校验手机号
        if(!CommonUtils.validateMobile(mobile)){
            log.error("register exception:mobile matches error");
            throw new C422Exception("手机号格式错误");
        }

        //去除两边空格
        password = password.trim();

        //校验密码是否包含非法字符
        if(!CommonUtils.isContainIlleageChar(password)){
            log.error("register exception:password contains illeage char error");
            throw new C422Exception("密码不符合要求，请输入英文字符");
        }

        if(password.length() < 6 || password.length()>20){
            log.error("register exception:pwd matches error");
            throw new C422Exception("请输入密码为6-20位字母、数字或字符");
        }

        //校验密码规则
        if(!CommonUtils.validatePassword(password)){
            log.error("register exception:pwd matches error");
            throw new C422Exception("请输入密码为6-20位字母、数字或字符");
        }



        //验证码校验
        try{
            SmsVerifyRequestDTO smsDto = new SmsVerifyRequestDTO();
            smsDto.setSmsCode(smsCode);
            smsDto.setSmsToken(smsToken);
            smsDto.setCountryCode(countryCode);
            smsDto.setMobile(mobile);
            smsService.doPut(smsDto);
        }catch (Exception e){
            log.error("smsCode is error!message = [{}]",e.getMessage());
            throw new C422Exception(e.getMessage());
        }


        //校验用户是否存在
        User oldUser = userDao.findByCountryCodeAndMobile(countryCode,mobile);
        if(oldUser != null){
            log.error("register exception:mobile has bean register");
            //用户已经存在
            throw new C422Exception("该手机号已被注册，请直接登录");
        }


        //获取用户ID
        UserIdDto userIdDto = this.userIdClient.getUserId();
        Long userId = userIdDto.getId();

        User user = new User();
        //注册IM用户
        try {
            String imUserId = imService.imUserRegister(userId);
            user.setImUserId(imUserId);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        user.setId(userId);
        user.setUserName(getUserName());
        user.setNickname(getNickname());
        user.setCountryCode(countryCode);
        user.setMobile(mobile);
        user.setPassword(PasswordUtils.encrypt(password));
        //TODO:默认头像暂未确认
        user.setHeadImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSg0XMdXF807hNgRbuNFZ9skSPsSADrOKhT4FSeodDHLYZpWE_a");
        user.setRegisterOrigin(RegisterOriginEnum.PHONE.toString());
        user.setStatus(UserStatusEnum.USED.toString());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userDao.save(user);

        UserResponseDto userResponseDto = buildUserResponseDto(user);

        //获取登录token
        DeviceVO deviceVO = CommonUtils.parseDevice(device);
        String loginToken = this.getLoginToken(userId.toString(), deviceVO);
        userResponseDto.setLoginToken(loginToken);

        long endTime=System.currentTimeMillis();

        log.info(" register time = [{}ms] , response = [{}]",(endTime-beginTime),userResponseDto);

        return userResponseDto;
    }

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody UserRequestDto requestUser,
                                 @RequestHeader("X-Gomeplus-Device") String device){

        Long startTime = System.currentTimeMillis();

        String countryCode = requestUser.getCountryCode();
        String mobile = requestUser.getMobile();
        String password = requestUser.getPassword();

        log.info("login param mobile = [{}]",mobile);

        if(StringUtils.isEmpty(mobile)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(countryCode)
                || StringUtils.isEmpty(device)){
            log.error("param data is error");
            throw new C422Exception("数据校验失败");
        }

        User user = userDao.findByCountryCodeAndMobile(countryCode,mobile);
        if(user == null){
            log.error("user is null");
            throw  new C422Exception("用户名或密码错误，请重新输入");
        }

        String userPassword = user.getPassword();

        //拼接密码错误次数的key
        String pwdErrorTimesKey = CacheKey.USER_LOGIN_PASSWORD_ERROR_TIMES + CacheKey.SEPERATOR + user.getId();

        //记录密码错误
        Integer errorTimes = (Integer) redisService.get(pwdErrorTimesKey);

        //获取失效时间
        Long exipreTime = redisService.getExipre(pwdErrorTimesKey);

        if(!PasswordUtils.match(password,userPassword)){

            //如果为空，置入1
            if(errorTimes == null){
                Integer pwdErrorTime = 1;
                redisService.set(pwdErrorTimesKey,pwdErrorTime, UserConstants.USER_LOGIN_PASSWORD_ERROR_EXPIRE_TIME);
                log.error("password is error first");
                throw  new C422Exception("用户名或密码错误，请重新输入");
            }else if(errorTimes < 4 ){

                errorTimes += 1;
                redisService.set(pwdErrorTimesKey,errorTimes,exipreTime);
                log.error("password is error < 4 times,times = [{}]",exipreTime);
                throw  new C422Exception("用户名或密码错误，请重新输入");

            }else if (errorTimes < 9){

                errorTimes += 1;
                log.error("password is error < 9 times,times = [{}]",exipreTime);
                redisService.set(pwdErrorTimesKey,errorTimes,exipreTime);
                throw  new C422Exception("您还有"+(10-errorTimes)+"次机会，请重新输入");

            }else if(errorTimes == 9 ){
                errorTimes += 1;
                redisService.set(pwdErrorTimesKey,errorTimes,UserConstants.USER_LOGIN_FROZEN_TIME);
                log.error("password is error = 10 times,user is frozen");
                throw  new C422Exception("您的账号存在安全隐患，请一小时后再尝试");
            }else if(errorTimes > 9){
                log.error("password is error > 10 times,user is frozen");
                throw  new C422Exception("您的账号存在安全隐患，请一小时后再尝试");
            }
        }else{

            if(errorTimes != null){
                if(errorTimes > 9){
                    log.error("password is right,but user is frozen");
                    throw new C422Exception("您的账号存在安全隐患，请一小时后再尝试");
                }else{
                    //删除缓存次数
                    redisService.del(pwdErrorTimesKey);
                }
            }

        }

        UserResponseDto userResponseDto = buildUserResponseDto(user);
        DeviceVO deviceVO = CommonUtils.parseDevice(device);

        //强制踢人
        kickUser(user.getId().toString(),deviceVO);

        //获取登录token
        String loginToken = this.getLoginToken(user.getId().toString(), deviceVO);
        userResponseDto.setLoginToken(loginToken);


        Long expenseTime = System.currentTimeMillis()-startTime;
        log.info("login success, expense time = [{}ms], response = [{}]",expenseTime,userResponseDto);

        return userResponseDto;

    }

    /**
     * 强制踢人操作
     * @param userId
     * @param deviceVO
     */
    private void kickUser(String userId, DeviceVO deviceVO) {

        String osType = deviceVO.getOsType();
        String deviceId = deviceVO.getDeviceId();

        //获取redis中的token
        String loginTokenKey = CommonUtils.getLoginTokenKey(userId, osType);
        String  loginToken = (String) redisService.get(loginTokenKey);

        if(StringUtils.isNotEmpty(loginToken)){

            //如果登录的手机不是本机的话，发出踢人请求
            if(!CommonUtils.validLoginToken(userId,deviceId,loginToken)){

                //发出踢人请求
                log.info("踢出上一个同设备的登录状态");

                //清除redis中的token
                redisService.del(loginTokenKey);
            }
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/loginout")
    public void loginOut(@RequestHeader("X-Gomeplus-User-Id") String userId,
                         @RequestHeader("X-Gomeplus-Device") String device){

        log.info("loginout is success!userId=[{}]",userId);

        if(StringUtils.isEmpty(userId)
                || StringUtils.isEmpty(device)){
            throw new C422Exception("参数校验失败");
        }

        //清除redis中的loginToken
        String loginTokenKey = CommonUtils.getLoginTokenKey(userId, CommonUtils.parseDevice(device).getOsType());
        redisService.del(loginTokenKey);

    }


    private UserResponseDto buildUserResponseDto(User user){
        UserResponseDto responseDto = new UserResponseDto();
        UserVO userVO = new UserVO();
        userVO.setId(user.getId().toString());
        userVO.setFacePicUrl(user.getHeadImageUrl());
        userVO.setImUserId(user.getImUserId());
        if(null!=user.getInviteUserId()){
            userVO.setRefereeUserId(user.getInviteUserId()+"");
        }
        userVO.setNickname(user.getNickname());
        userVO.setUserName(user.getUserName());
        userVO.setMobile(user.getMobile());
        userVO.setCountryCode(user.getCountryCode());
        userVO.setRegisterTime(user.getCreateTime().getTime());

        responseDto.setUser(userVO);
        return responseDto;
    }

    /**
     *获取登录token
     * @param userId   用户ID
     * @param deviceVO  登录设备信息
     * @return
     */
    private String getLoginToken (String userId,DeviceVO deviceVO){

        String osType = deviceVO.getOsType();
        String deviceId = deviceVO.getDeviceId();

        //组装放入redis中的token
        String loginToken = DigestUtils.sha1Hex(userId+deviceId);

        //获取缓存的key
        String loginTokenKey = CommonUtils.getLoginTokenKey(userId,osType);

        //存放redis中
        redisService.set(loginTokenKey,loginToken,UserConstants.USER_LOGIN_TOKEN_EXPIRE_TIME);
        return loginToken;
    }

    /**
     * 获取账号
     * 规则：
     * gm_+10位随机数字字母
     * @return
     */
    private String getUserName(){

        //先获取redis的序列号，使用序列号，防止重复，不需要做重复校验
        Long incr = redisService.incr(CacheKey.USER_REGISTER_NAME_INCR);
        String tmpStr = incr.toString();

        String userName = UserConstants.USER_NAME_PREFIX + CommonUtils.generatorRandomStr(8-tmpStr.length())+tmpStr;
        return userName;
    }

    /**
     * 获取昵称
     * 规则：第一位小写字母+9随机数字
     * 长度：10位
     * @return
     */
    private String getNickname(){
        return CommonUtils.generatorRandomChar(1)+CommonUtils.generatorRandomNum(9);
    }

}
