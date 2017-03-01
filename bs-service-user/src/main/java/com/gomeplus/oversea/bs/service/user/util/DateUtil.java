package com.gomeplus.oversea.bs.service.user.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by liuliyun-ds on 2017/2/14.
 * 日期工具类
 */
public class DateUtil {

    /**
     * 当天0点0分0秒的毫秒数
     * @return
     */
    public static Long beginDay(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTimeInMillis();
    }

    /**
     * 当天结束时刻的毫秒数
     * @return
     */
    public static Long endDay(){
        return beginDay() + 24*60*60*1000;
    }
}
