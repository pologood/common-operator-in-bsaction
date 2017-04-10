package com.gomeplus.oversea.bs.service.gateway.util;

/**
 * Created by shangshengfang on 2017/4/5.
 */
public class LogUtil {
    public static String[]  sensitiveUrls={
            "post#/user/register#password",
            "put#/user/modifyPasswordAction#password,oldPassword",
            "put#/user/password#password,confirmedPassword",
            "post#/user/login#password",
            "post#/user/user#password"
    };
}
