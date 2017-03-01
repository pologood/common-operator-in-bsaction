package com.gomeplus.oversea.bs.service.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.gomeplus.oversea.bs.service.user.dao.UserDao;
import com.gomeplus.oversea.bs.service.user.dao.UserSnsBindDao;
import com.gomeplus.oversea.bs.service.user.dto.user.UserVO;
import com.gomeplus.oversea.bs.service.user.entity.User;
import com.gomeplus.oversea.bs.service.user.entity.UserSnsBind;
import com.gomeplus.oversea.bs.service.user.util.HttpsClientUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;


/**
 * 第三方登录
 */
@RestController
@Slf4j
@RequestMapping("/user/thirdPartyAccountLogin")
public class ThirdPartyAccountLoginController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSnsBindDao userSnsBindDao;

    private static final String accessToken = "125552021295455%7c002ec05d2c8b3e47ebcf7ca7a3a80cf6";

    private static  final String charset = "UTF-8";

    /**
     * 第三方登录（首次登陆、非首次登陆）
     * @param id 第三方用户id
     * @param thirdPartyAccountType 第三方登录类型
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Object doGet(@RequestParam String id,String thirdPartyAccountType,String thirdPartyAccessToken) {
        try {
            log.info("Third party login with param id[{}],thirdPartyAccountType[{}],thirdPartyAccessToken[{}]",id,thirdPartyAccountType,thirdPartyAccessToken);
            if (StringUtils.isBlank(id)|| StringUtils.isBlank(thirdPartyAccountType)|| StringUtils.isBlank(thirdPartyAccessToken)){
                throw new C422Exception("数据校验失败");
            }
            //校验第三方登录token令牌
            checkToken(thirdPartyAccessToken);
            Map<String, Object> data = Maps.newHashMap();
            //判断是否是第一次登录
            UserSnsBind userSnsBind = userSnsBindDao.findBySnsUserId(id);
            if (userSnsBind != null){//表示用第三方非第一次登录（已经绑定了手机号）
                //通过第三方id查询已绑定的会员id
                Long userId = userSnsBind.getUserId();
                User user = userDao.findOne(userId);
                if(user == null){
                    log.error("third part login bind user is not exist. userId is [{}]", userId);
                    throw new C422Exception("会员不存在");
                }
                if(user.getStatus() != null && user.getStatus().equals("FROZEN")){
                    log.info("current user is frozen: userId is: {}", userId);
                    throw new C422Exception("当前用户已被冻结");
                }
                //登录成功后生成 Token信息，并存入缓存中
                // registeOrLoginUtil.RegisteOrLoginTodo(userModel, params, token,scn);
                String token = UUID.randomUUID().toString().replace("-","");
                data.put("isBound", true);
                UserVO vo = UserVO.fromModel(user);
                data.put("user", vo);
                data.put("loginToken", token);
            } else {
                data.put("isBound", false);
            }
            log.info("Third party login response[{}]",data);
            return data;
        } catch (Exception e){
            log.error("Third party login have exception, id[{}],thirdPartyAccountType[{}],errMsg:",id,thirdPartyAccountType,e);
            throw e;
        }
    }

    /**
     * 校验第三方登录令牌
     * @param thirdPartyAccessToken
     */
    public void checkToken(String thirdPartyAccessToken) {
        String getUrl = "https://graph.facebook.com/debug_token?access_token="+accessToken+"&input_token="+thirdPartyAccessToken;
        String resp =HttpsClientUtil.doHttpsGet(getUrl,charset);
        if (!Strings.isNullOrEmpty(resp)){
            JSONObject jsonObject = JSONObject.parseObject(resp);
            Map map = (Map)jsonObject;
            String isValid=((Map)map.get("data")).get("is_valid").toString();
            if (!"true".equals(isValid)){
                throw new C422Exception("第三方登录认证失败");
            }
        }else{
            throw new C422Exception("第三方登录认证失败");
        }
    }
//    public static void main(String[] args){
//        String test = "EAAByMFuhUV8BABdC5N97d6LiBiqxAWFHmbsVhjqthvpqzfJvkEA5U4yqY6R39ZAXiVhzbkFs7mxNZCdZACpbZBaaNRBDeLPgylJXhkNK2VNU6j6G2HmhH4OGhoZC1ugCGqBX25WvQZC6eKIrGXffyIxmucqZBfE4IkBlr2hvvZBXQZC0X7iP4PVs7TFwx5RMWwjUNrhQCJtkP8s0YFxxazDQdmuAm8f20DaAZD";
//        checkToken(test);
//    }

}
