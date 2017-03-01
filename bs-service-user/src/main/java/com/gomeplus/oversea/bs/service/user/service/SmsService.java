package com.gomeplus.oversea.bs.service.user.service;

import com.gomeplus.oversea.bs.common.MessageSender.SenderResponse;
import com.gomeplus.oversea.bs.common.MessageSender.SmsSender;
import com.gomeplus.oversea.bs.common.exception.code4xx.C404Exception;
import com.gomeplus.oversea.bs.common.exception.code4xx.C409Exception;
import com.gomeplus.oversea.bs.common.exception.code4xx.C410Exception;
import com.gomeplus.oversea.bs.common.exception.code4xx.C415Exception;
import com.gomeplus.oversea.bs.common.exception.code5xx.C500Exception;
import com.gomeplus.oversea.bs.service.user.dao.SmsRecordDao;
import com.gomeplus.oversea.bs.service.user.dao.UserDao;
import com.gomeplus.oversea.bs.service.user.dto.SmsResponseDTO;
import com.gomeplus.oversea.bs.service.user.dto.SmsSendRequestDTO;
import com.gomeplus.oversea.bs.service.user.dto.SmsVerifyRequestDTO;
import com.gomeplus.oversea.bs.service.user.entity.SmsRecord;
import com.gomeplus.oversea.bs.service.user.entity.User;
import com.gomeplus.oversea.bs.service.user.enums.ErrorCodeEnum;
import com.gomeplus.oversea.bs.service.user.enums.RegisterOriginEnum;
import com.gomeplus.oversea.bs.service.user.enums.SmsServiceType;
import com.gomeplus.oversea.bs.service.user.util.DateUtil;
import com.gomeplus.oversea.bs.service.user.util.IdWorker;
import com.gomeplus.oversea.bs.service.user.util.SmsCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Created by liuliyun-ds on 2017/2/14.
 * 短信接口Service类
 */
@Service("smsService")
@Slf4j
public class SmsService {

    @Autowired
    private SmsRecordDao smsRecordDao;

    @Autowired
    private UserDao userDao;

    @Value("${user.sms.switch}")
    private String smsSwitch;

    private static SmsSender smsSender = new SmsSender();

    /**
     * 发送短信验证码
     * @param dto
     * @return
     */
    public SmsResponseDTO doPost(SmsSendRequestDTO dto){

        User user = userDao.findByCountryCodeAndMobile(dto.getCountryCode(),dto.getMobile());

        if(SmsServiceType.REGIST.name().equals(dto.getType()) && user != null){
            //下发注册短信验证码时，手机号已注册提示
            if(RegisterOriginEnum.PHONE.name().equals(user.getRegisterOrigin())){
                throw new C409Exception(ErrorCodeEnum.ACCOUNT_ALREADY_EXIST.getMessage());
            }else{
                throw new C409Exception(ErrorCodeEnum.THIRD_PARTY_ACCOUNT_LOGIN_MSG.getMessage());
            }
        }else if(SmsServiceType.FINDPWD.name().equals(dto.getType()) && user == null){
            //下发找回密码短信验证码时，手机号未注册提示
            throw new C404Exception(ErrorCodeEnum.USER_NOT_EXIST.getMessage());
        }else if(SmsServiceType.BINDPHONE.name().equals(dto.getType()) && user != null){
            //下发第三方绑定手机号短信验证码时，手机号已绑定提示
            if(RegisterOriginEnum.PHONE.name().equals(user.getRegisterOrigin())){
                throw new C409Exception(ErrorCodeEnum.ACCOUNT_ALREADY_EXIST.getMessage());
            }else{
                throw new C415Exception(ErrorCodeEnum.MOBILE_HAS_BOUND.getMessage());
            }

        }

        //检验自然日同一业务下发短信验证码次数是否超过5次
        Long count = 0L;
        Long beginDay = DateUtil.beginDay();
        Long endDay = beginDay + 24*60*60*1000;
        count = smsRecordDao.count(dto.getCountryCode(),dto.getMobile(),dto.getType(),"USER",new Date(beginDay),new Date(endDay));
        if(count >= 5){
            throw new C410Exception(ErrorCodeEnum.SMS_DAY_TIMES_LIMITED.getMessage());
        }
        Long time = System.currentTimeMillis();
        Date date = new Date(time);
        SmsRecord smsRecord = new SmsRecord();
        //        smsRecord.setSmsCode(SmsCodeGenerator.smsCapture(6));
        smsRecord.setSmsCode("111111");
        SenderResponse response = null;
        String message = SmsServiceType.valueOf(dto.getType()).getMessage().replace("smsCode",smsRecord.getSmsCode());
        String mobile = dto.getCountryCode().replace("+","") + dto.getMobile();
        if("on".equals(smsSwitch)){
            try {
                log.info("sms service send message = [{}] to mobile = [{}]",message,mobile);
//                response = smsSender.sendSms(message,mobile);
                response = smsSender.sendSms("【无限云】来自gomeplus您的验证码为：098765，请在一分钟内使用，谢谢！","8615820210066");
            }catch (Exception e){
                log.error("invoke sms service error,exception message = [{}]",e.getMessage());
                throw new C500Exception("调用短信服务异常，");
            }
            if(response.getStatus() != 0){
                log.error("invoke sms service error,status code = [{}]",response.getStatusCode());
                throw new C500Exception("调用短信服务异常");
            }
            //记录短信服务返回的messageid
            smsRecord.setRemark1("{\"messageId\":\"" + response.getMessageId() + "\"}");
        }
        smsRecord.setId(IdWorker.getNextId());
        smsRecord.setUserId(user == null? null:user.getId());
        smsRecord.setCountryCode(dto.getCountryCode());
        smsRecord.setMobile(dto.getMobile());
        smsRecord.setServiceType(dto.getType());
        smsRecord.setBusinessType("USER");
        smsRecord.setValidPeriod(10*60);
        smsRecord.setMsg(message);
        smsRecord.setToken(UUID.randomUUID().toString().replace("-",""));
        smsRecord.setSmsSeq((count.intValue()+2)*5);
        smsRecord.setBeginTime(date);
        smsRecord.setEndTime(new Date(time+smsRecord.getValidPeriod()*1000));
        smsRecord.setStatus((byte)0);
        smsRecord.setCreateTime(date);
        smsRecord.setUpdateTime(date);
        smsRecordDao.save(smsRecord);
        SmsResponseDTO smsResponseDTO = new SmsResponseDTO();
        smsResponseDTO.setSmsToken(smsRecord.getToken());
        smsResponseDTO.setSequence(smsRecord.getSmsSeq());
        return smsResponseDTO;
    }

    /**
     * 验证短信验证码是否正确
     * @param dto
     * @return
     */
    public SmsResponseDTO doPut(SmsVerifyRequestDTO dto){
        SmsRecord record = smsRecordDao.findByToken(dto.getSmsToken());

        //短信验证码不存在
        if(record == null || !record.getMobile().equals(dto.getMobile()) || !record.getCountryCode().equals(dto.getCountryCode())){
            throw new C404Exception(ErrorCodeEnum.SMS_CODE_NOT_EXIST.getMessage());
        }

        long time = System.currentTimeMillis();
        //短信验证码已过期
        if(time >= record.getEndTime().getTime()){
            throw new C410Exception(ErrorCodeEnum.SMS_CODE_INVALID.getMessage());
        }

        //输入短信验证码不正确
        if(!record.getSmsCode().equals(dto.getSmsCode())){
            throw new C409Exception(ErrorCodeEnum.SMS_CODE_ERROR.getMessage());
        }

        //短信验证码已被使用
        if(record.getStatus().equals((byte)1)){
            throw new C404Exception(ErrorCodeEnum.SMS_CODE_INVALID.getMessage());
        }

        Long count = smsRecordDao.countByGtSendTime(record.getCountryCode(),record.getMobile(),
                record.getServiceType(),record.getBusinessType(),record.getCreateTime());
        if(count > 0){
            //下发过最新的验证码，此次验证码过期
            throw new C410Exception(ErrorCodeEnum.SMS_CODE_INVALID.getMessage());
        }
        if(SmsServiceType.FINDPWD.name().equals(record.getServiceType())){
            record.setToken(UUID.randomUUID().toString().replace("-",""));
        }
        record.setStatus((byte)1);
        record.setUpdateTime(new Date(time));
        smsRecordDao.save(record);
        SmsResponseDTO responseDTO = new SmsResponseDTO();
        responseDTO.setSmsToken(record.getToken());
        responseDTO.setSequence(record.getSmsSeq());
        return responseDTO;
    }

    /**
     * 找回密码校验token接口
     * @param smsToken
     * @return
     */
    public SmsRecord smsTokenVerify(String smsToken){
        SmsRecord record = smsRecordDao.findByToken(smsToken);
        //检验token是否存在
        if(record == null || !SmsServiceType.FINDPWD.name().equals(record.getServiceType())
                || record.getCreateTime().getTime() == record.getUpdateTime().getTime()){
            throw new C404Exception(ErrorCodeEnum.SMS_CODE_NOT_EXIST.getMessage());
        }
        //检验token是否过期
        long time = System.currentTimeMillis();
        if(time >= record.getEndTime().getTime()){
            throw new C409Exception(ErrorCodeEnum.SMS_CODE_INVALID.getMessage());
        }
        return record;
    }
    
}
