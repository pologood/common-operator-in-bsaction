package com.gomeplus.oversea.bs.service.user.controller;

import com.gomeplus.oversea.bs.common.exception.code4xx.C404Exception;
import com.gomeplus.oversea.bs.common.exception.code4xx.C409Exception;
import com.gomeplus.oversea.bs.common.exception.code4xx.C422Exception;
import com.gomeplus.oversea.bs.service.user.dao.UserDao;
import com.gomeplus.oversea.bs.service.user.dto.password.PasswordVO;
import com.gomeplus.oversea.bs.service.user.entity.SmsRecord;
import com.gomeplus.oversea.bs.service.user.entity.User;
import com.gomeplus.oversea.bs.service.user.service.SmsService;
import com.gomeplus.oversea.bs.service.user.util.CommonUtils;
import com.gomeplus.oversea.bs.service.user.util.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 密码模块接口
 * Created by yangzongbin on 2017/2/16.
 */
@Slf4j
@RestController("/user/password")
public class PasswordController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserDao userDao;

    /**
     * 会员没有登录，忘记密码
     * @param passwordVO 重置密码页面传过来的对象
     */
    @PutMapping
    public void reset(@RequestBody PasswordVO passwordVO){

        try {

            if(StringUtils.isEmpty(passwordVO.getPassword())){
                log.error("register exception:password contains illeage char error");
                throw new C422Exception("请输入密码为6-20位字母、数字或符号");
            }

            if(StringUtils.isEmpty(passwordVO.getConfirmedPassword())){
                log.error("register exception:password contains illeage char error");
                throw new C422Exception("请输入密码为6-20位字母、数字或符号");
            }

            if(StringUtils.isEmpty(passwordVO.getSmsToken())){
                throw new C422Exception("验证码错误，请重新输入");
            }

            //校验密码是否包含非法字符
            if(!CommonUtils.isContainIlleageChar(passwordVO.getPassword())){
                log.error("register exception:password contains illeage char error");
                throw new C422Exception("密码不符合要求，请输入英文字符");
            }

            //校验密码规则
            if(!CommonUtils.validatePassword(passwordVO.getPassword())){
                throw new C422Exception("请输入密码为6-20位字母、数字或符号");
            }
            if (!passwordVO.getPassword().equals(passwordVO.getConfirmedPassword())) {
                throw new C422Exception("密码两次输入不⼀致，请重新输入");
            }

            SmsRecord smsRecord = null;
            try {
                smsRecord = smsService.smsTokenVerify(passwordVO.getSmsToken());
            }catch (C404Exception c404Exception){
                log.error("Fail to Call smsTokenVerify:smsToken[{}]",passwordVO.getSmsToken(),c404Exception);
                throw new C422Exception("验证码错误，请重新输入");
            }catch (C409Exception c409Exception){
                log.error("Fail to Call smsTokenVerify:smsToken[{}]",passwordVO.getSmsToken(),c409Exception);
                throw new C422Exception("验证码错误，请重新输入");
            }catch (Exception e){
                log.error("Fail to Call smsTokenVerify:smsToken[{}]",passwordVO.getSmsToken(),e);
                throw new C422Exception("验证码错误，请重新输入");
            }

            User user = userDao.findByCountryCodeAndMobile(smsRecord.getCountryCode(),smsRecord.getMobile());
            user.setPassword(PasswordUtils.encrypt(passwordVO.getPassword()));
            userDao.save(user);
        }catch (Exception e){
            log.error("忘记密码接口异常:",e);
            throw e;
        }


    }
}
