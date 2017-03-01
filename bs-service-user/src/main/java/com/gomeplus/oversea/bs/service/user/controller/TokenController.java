package com.gomeplus.oversea.bs.service.user.controller;

import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.gomeplus.oversea.bs.service.user.dto.user.DeviceVO;
import com.gomeplus.oversea.bs.service.user.service.common.RedisService;
import com.gomeplus.oversea.bs.service.user.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by baixiangzhu on 2017/2/21.
 */
@RestController
@Slf4j
@RequestMapping("/user/token")
public class TokenController {

    @Autowired
    private RedisService redisService;

    /**
     * 校验token是否成功
     * @param userId
     * @param device
     * @param loginToken
     * @return
     */
    @GetMapping
    public void validToken(@RequestHeader("X-Gomeplus-User-Id") String userId,
                           @RequestHeader("X-Gomeplus-Device") String device,
                           @RequestHeader("X-Gomeplus-Login-Token") String loginToken ){

        if(StringUtils.isEmpty(userId)
                || StringUtils.isEmpty(device)
                || StringUtils.isEmpty(loginToken)){
            throw new C422Exception("参数校验失败");
        }

        DeviceVO deviceVO = CommonUtils.parseDevice(device);
        String osType = deviceVO.getOsType();
        String deviceId = deviceVO.getDeviceId();

        //检查token是否匹配
        if(!CommonUtils.validLoginToken(userId,deviceId,loginToken)){
            throw new C422Exception("loginToken校验失败");
        }

        //获取redis中存在的loginToken
        String redisToken = (String) redisService.get(CommonUtils.getLoginTokenKey(userId, osType));

        if(StringUtils.isEmpty(redisToken)){
            throw new C422Exception("loginToken校验失败");
        }

        //校验token是否匹配
        if(!loginToken.equals(redisToken)){
            throw new C422Exception("loginToken校验失败");
        }
    }
}