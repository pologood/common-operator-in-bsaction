package com.gomeplus.oversea.bs.service.user.controller;

import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.gomeplus.oversea.bs.service.user.dto.SmsResponseDTO;
import com.gomeplus.oversea.bs.service.user.dto.SmsSendRequestDTO;
import com.gomeplus.oversea.bs.service.user.dto.SmsVerifyRequestDTO;
import com.gomeplus.oversea.bs.service.user.enums.SmsServiceType;
import com.gomeplus.oversea.bs.service.user.service.SmsService;
import com.gomeplus.oversea.bs.service.user.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by liuliyun-ds on 2017/2/14.
 * 短信接口类
 */
@RestController
@Slf4j
@RequestMapping("/user/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    /**
     * 发送短信验证码
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public SmsResponseDTO doPost(@RequestBody SmsSendRequestDTO dto){
        log.info("sms request body = [{}]" ,dto);
        if(dto == null || StringUtils.isEmpty(dto.getMobile()) || !CommonUtils.validateMobile(dto.getMobile())){
            throw new C422Exception("手机号格式错误");
        }
        if(StringUtils.isEmpty(dto.getCountryCode()) ||
                StringUtils.isEmpty(dto.getType()) || !SmsServiceType.contain(dto.getType())){
            log.info(dto.toString());
            throw new C422Exception("参数校验失败");
        }

        SmsResponseDTO responseDTO = smsService.doPost(dto);
        log.info("sms response body = [{}]" ,responseDTO);
        return responseDTO;
    }

    /**
     * 验证短信验证码
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public SmsResponseDTO doPut(@RequestBody SmsVerifyRequestDTO dto){
        log.info("sms verify request body = [{}]" ,dto);
        if(dto == null || StringUtils.isEmpty(dto.getSmsToken()) || StringUtils.isEmpty(dto.getSmsCode())
                || StringUtils.isEmpty(dto.getCountryCode()) || StringUtils.isEmpty(dto.getMobile())){
            log.info(dto.toString());
            throw new C422Exception("参数校验失败");
        }
        SmsResponseDTO responseDTO = smsService.doPut(dto);
        log.info("sms verify response body = [{}]" ,responseDTO);
        return responseDTO;
    }

}
