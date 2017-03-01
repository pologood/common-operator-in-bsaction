package com.gomeplus.oversea.bs.service.user.util;

import java.util.Random;

/**
 * Created by liuliyun-ds on 2017/2/13.
 * 生成随机手机验证码
 */
public class SmsCodeGenerator {
    public static final String RANDOM_STRS = "0123456789";
    private static Random random = new Random();

    /**
     * 获取随机的字符
     */
    private static String getRandomString(int num) {
        return String.valueOf(RANDOM_STRS.charAt(num));
    }


    public static String smsCapture(int length){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<length;i++){
            stringBuilder.append(getRandomString(random.nextInt(RANDOM_STRS
                    .length())));
        }
        return stringBuilder.toString();
    }
}
