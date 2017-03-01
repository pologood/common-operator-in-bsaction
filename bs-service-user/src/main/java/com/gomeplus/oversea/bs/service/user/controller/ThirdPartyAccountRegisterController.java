package com.gomeplus.oversea.bs.service.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.gomeplus.oversea.bs.common.exception.code4xx.C409Exception;
import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.gomeplus.oversea.bs.service.user.cache.CacheKey;
import com.gomeplus.oversea.bs.service.user.dao.UserDao;
import com.gomeplus.oversea.bs.service.user.dao.UserSnsBindDao;
import com.gomeplus.oversea.bs.service.user.dto.SmsVerifyRequestDTO;
import com.gomeplus.oversea.bs.service.user.dto.thirdParty.ThirdPartyRegistDto;
import com.gomeplus.oversea.bs.service.user.dto.user.UserVO;
import com.gomeplus.oversea.bs.service.user.entity.User;
import com.gomeplus.oversea.bs.service.user.entity.UserSnsBind;
import com.gomeplus.oversea.bs.service.user.service.SmsService;
import com.gomeplus.oversea.bs.service.user.service.common.RedisService;
import com.gomeplus.oversea.bs.service.user.util.CommonUtils;
import com.gomeplus.oversea.bs.service.user.util.IdWorker;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 第三方登录注册绑定
 */
@RestController
@Slf4j
@RequestMapping("/user/thirdPartyAccountRegister")
public class ThirdPartyAccountRegisterController {
    private static Long [] ids = new Long[]{
            17024790161L,
            17024790162L,
            17024790163L,
            17024790164L,
            17024790165L,
            17024790166L,
            17024790167L,
            17024790168L,
            17024790169L,
            17024790170L,
            17024790171L,
            17024790172L,
            17024790173L,
            17024790174L,
            17024790175L,
            17024790176L,
            17024790177L,
            17024790178L,
            17024790179L,
            17024790180L,
            17024790181L,
            17024790182L,
            17024790183L,
            17024790184L,
            17024790185L,
            17024790186L,
            17024790187L,
            17024790188L,
            17024790189L,
            17024790190L
    };
    private static int length = 30;
    private static int index = 0;
    private static final String USER_NAME_PREFIX = "gm_";
    /**手机号校验正则*/
    private static final String PHONE_REGEX = "^[89][0-9]{9}$";

    @Autowired
    private UserDao userDao;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserSnsBindDao userSnsBindDao;

    @Autowired
    private RedisService redisService;

    /**
     * 第三方登录绑定注册
     * @param thirdPartyRegistDto 第三方注册实体对象
     * @return
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public Object doPost(@RequestBody ThirdPartyRegistDto thirdPartyRegistDto) throws Exception {
        log.info("Third part account regist with param[{}]", JSONObject.toJSONString(thirdPartyRegistDto));
        //验证必填字段
        ThirdPartyRegistDto.checkFieldValueNull(thirdPartyRegistDto);
        //校验手机号
        if(!Pattern.matches(PHONE_REGEX,thirdPartyRegistDto.getMobile())){
            throw new C422Exception("手机号格式错误");
        }
        //校验token和验证码
        SmsVerifyRequestDTO dto = new SmsVerifyRequestDTO();
        dto.setCountryCode(thirdPartyRegistDto.getCountryCode());
        dto.setMobile(thirdPartyRegistDto.getMobile());
        dto.setSmsCode(thirdPartyRegistDto.getSmsCode());
        dto.setSmsToken(thirdPartyRegistDto.getSmsToken());
        smsService.doPut(dto);
        //校验第三方账号是否已经绑定
        UserSnsBind usb = userSnsBindDao.findBySnsUserId(thirdPartyRegistDto.getThirdParty().getId());
        if(usb != null ) {
            throw new C409Exception("该第三方账号已经绑定过手机号");
        }
        //第三方登录了类型
        String type = thirdPartyRegistDto.getThirdParty().getType().toLowerCase().trim();
        StringBuffer sb = new StringBuffer(type);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        type = sb.toString();
        //注册新用户
        User user = new User();
        user.setId(getId());
        user.setNickname(thirdPartyRegistDto.getThirdParty().getName());
        user.setMobile(thirdPartyRegistDto.getMobile());
        if (!Strings.isNullOrEmpty(thirdPartyRegistDto.getRefereeUserId())){
            user.setInviteUserId(Long.parseLong(thirdPartyRegistDto.getRefereeUserId()));
        }
        if(!StringUtils.isBlank(thirdPartyRegistDto.getThirdParty().getFacePicUrl())){
            user.setHeadImageUrl(thirdPartyRegistDto.getThirdParty().getFacePicUrl());
        }
        user.setCountryCode(thirdPartyRegistDto.getCountryCode());
        user.setStatus("USED");
        user.setUserName(getUserName());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setRegisterOrigin(type);
        registerUserNew(user);
        Long userId = user.getId();
        // 封装要绑定的数据
        UserSnsBind userSnsBind = new UserSnsBind();
        userSnsBind.setId(IdWorker.getNextId());
        userSnsBind.setUserName(getUserName());
        userSnsBind.setUserId(userId);
        userSnsBind.setSnsType(type);
        userSnsBind.setSnsUserId(thirdPartyRegistDto.getThirdParty().getId());
        userSnsBind.setHeadImageUrl(thirdPartyRegistDto.getThirdParty().getFacePicUrl());
        userSnsBind.setNickname(thirdPartyRegistDto.getThirdParty().getName());
        userSnsBind.setCreateTime(new Date());
        userSnsBind.setUpdateTime(new Date());
        userSnsBindDao.save(userSnsBind);
        //绑定注册成功之后直接登录
        String loginToken = UUID.randomUUID().toString().replace("-","");
        UserVO userVO = UserVO.fromModel(user);
        Map<String,Object> data = Maps.newHashMap();
        data.put("user", userVO);
        data.put("loginToken", loginToken);
        log.info("Third part account regist response[{}]", data);
        return data;
    }

    /**
     * 第三方账户注册
     * @param user
     */
    private void registerUserNew(User user) {
        //判断是否唯一校验(是否注册过此号)
        User existUser = userDao.findByCountryCodeAndMobile(user.getCountryCode(),user.getMobile());
        if (existUser != null) {
//            Map<String,String> error =  Maps.newHashMap();
//            error.put("code", "100011");
//            error.put("message","该账号已被使用");
            throw new C422Exception("该账号已被使用");
        }
//        Long inviteUserId = user.getInviteUserId();
//            if (inviteUserId != null) {
//                if (!checkRecommenderUserId(user)) {
//                    throw new C422Exception("推荐人信息错误");
//                }
//        }
        userDao.save(user);
    }

//    /**
//     * 推荐人信息校验
//     * @param user
//     * @return
//     */
//
//    private Boolean checkRecommenderUserId(User user){
//        try {
//            //判断推荐人是否存在
//            User inviteUser = userDao.getOne(user.getInviteUserId());
//            if(inviteUser ==null){
//                return false;
//            }
//            if(user.getId().equals(user.getInviteUserId())) {
//                return false;
//            }
//        } catch (Exception e) {
//            log.error("method checkRecommenderUserId happens exception: userId is: [{}], inviteUserId is: {}", user.getId(), user.getInviteUserId(), e);
//            return false;
//        }
//        return true;
//    }

    //生成ID
    private Long getId() throws Exception {
        Random random = new Random();
        int i = random.nextInt(length);
        Long id = ids[i];
        User user = userDao.findOne(id);
        index ++;
        if(user != null){
            if(index < length){
                getId();
            }else{
                throw new Exception("账户ID已经分配完成");
            }
        }
        index = 0;
        return id;
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
        String userName = USER_NAME_PREFIX + CommonUtils.generatorRandomStr(10-tmpStr.length())+tmpStr;
        return userName;
    }
}
