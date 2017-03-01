package com.gomeplus.oversea.bs.service.user.util;

import com.gomeplus.oversea.bs.service.user.cache.CacheKey;
import com.gomeplus.oversea.bs.service.user.dto.user.DeviceVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by baixiangzhu on 2017/2/15.
 */
public class CommonUtils {

    //字母
    private static final String CHAR = "char";
    //数字
    private static final String NUM = "num";
    private static Random random = new Random();

    /**手机号校验正则*/
    private static final String MOBILE_REGEX = "^[89][0-9]{9}$";
    /**密码校验规则*/
    // private static final String PASSWORD_REGEX="^(?!\\d+$)(?![a-zA-Z]+$)([a-zA-Z\\d`\\!@#\\$%^&*()_+-= ~【】、{}|•\\[\\]\\\\ ；‘：“，。、《》？/?/<>,.]){6,20}$";
    private static final String PASSWORD_REGEX="^(?!\\d+$)(?![a-zA-Z]+$)([a-zA-Z\\d`~\\!@#\\$%^&*()-_+=\\{\\}\\[\\]:;\"'|<>,./?/]){6,20}$";
    /**校验是否包含非法字符 （除数字、字母、英文符号之外的）*/
    private static final String PASSWORD_ILLEGAL_CHAR_REGEX = "^[\\w`~\\!@#\\$%^&*()-_+=\\{\\}\\[\\]:;\"'|<>,./?/]+$";

    /**
     * 获取随机字母+数字
     * @param length  长度
     * @return
     */
    public static   String generatorRandomStr(int length){

        //生成随机数字和字母,
        String val = "";

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? CHAR : NUM;

            //输出字母还是数字
            if( CHAR.equalsIgnoreCase(charOrNum) ) {
                val+=generatorRandomChar(1);
            } else if( NUM.equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }

        return val;
    }

    /**
     * 获取随机的小写字母
     * @param length  长度
     * @return
     */
    public static String generatorRandomChar(int length){

        String val = "";

        for(int i = 0; i < length; i++) {

            //输出是大写字母或小写字母
            //int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
            int temp = 97;   //小写字母
            val += (char)(random.nextInt(26) + temp);
        }

        return val;
    }

    /**
     *获取随机数字类字符串
     * @return
     */
    public static String generatorRandomNum(int length){

        String val = "";

        for(int i = 0; i < length; i++) {
            //获取
            val += String.valueOf(random.nextInt(10));
        }
        return val;

    }

    /**
     * 手机号格式校验
     * @param mobile
     * @return
     */
    public static boolean validateMobile(String mobile){
        return Pattern.matches(MOBILE_REGEX,mobile);
    }

    /**
     * 密码格式校验
     * 暂时存在缺陷：所有字符会通过
     * @param password
     * @return
     */
    @Deprecated
    public static boolean validatePasswordOld(String password){
        return Pattern.matches(PASSWORD_REGEX,password);
    }

    /**
     * 校验密码是否包含非法字符
     * @param password
     * @return
     */
    public static boolean isContainIlleageChar(String password){

        return Pattern.matches(PASSWORD_ILLEGAL_CHAR_REGEX,password);
    }

    /**
     * 获取登录token缓存redis中的key
     * @param userId    用户ID
     * @param osType    设备系统
     * @return
     */
    public static String getLoginTokenKey(String userId,String osType){

        return CacheKey.USER_LOGIN_TOKEN + CacheKey.SEPERATOR+osType+CacheKey.SEPERATOR+userId;
    }

    /**
     * 解析设备信息
     * device:{OSType}/{OSVersion}/{DeviceModel}/{DeviceId}
     * eg:Android/5.33.2/Mi3/dsa2432212
     * @return
     */
    public static DeviceVO parseDevice(String device){

        if(StringUtils.isEmpty(device)){
            return null;
        }
        String[] devices = device.split("/");

        String osType = devices[0];
        String osVsersion = devices[1];
        String deviceModel = devices[2];
        String deviceId = devices[3];

        return  new DeviceVO(osType,osVsersion,deviceModel,deviceId);
    }

    public static boolean validatePassword(String password){
        //合格的字符
        //不是全字母
        //不是全数字
        //不是符号

        //全角字符除空格
        //！"#$%&'（）*+，－．/：；<=>？@[\]＾_`{|}～
        //转换后
//        ^[!"#$%&'()*+,-./:;<=>?@\[\]^_`{}~\\|！"#$%&'（）*+，－．/：；<=>？@\[\]＾_`{}～\\|]{2,90}$
        //半角字符(除空格)
        //!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
//        String regex="^[a-zA-Z\\d`\\!@#\\$%^&*()_+\\-=
        String
                charStr="!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{}~\\\\|！\"#$%&'（）*+，－．/：；<=>？@\\[\\]＾_`{}～\\\\|";

        String regex="^[a-zA-Z\\d"+charStr+"]{6,20}$";
        String regex1="^[a-zA-Z]{6,20}$";
        String regex2="^[\\d]{6,20}$";
        String regex3="^["+charStr+"]{6,20}$";

        boolean  isOk= Pattern.matches(regex,password);
        if(!isOk){
            //存在不认可的字符或长度不够
            return isOk;
        }
        isOk= Pattern.matches(regex1,password);
        if(isOk){
            //全是字母
            return !isOk;
        }
        isOk= Pattern.matches(regex2,password);
        if(isOk){
            //全是数字
            return !isOk;
        }
        isOk= Pattern.matches(regex3,password);
        if(isOk){
            //全是字符
            return !isOk;
        }
        return !isOk;
    }


    /**
     * 校验token是否匹配
     * @param userId
     * @param deviceId
     * @param token
     * @return
     */
    public static boolean validLoginToken(String userId,String deviceId,String token){

        if(StringUtils.isEmpty(token))
            return false;

        String loginToken = DigestUtils.sha1Hex(userId+deviceId);
        return token.equals(loginToken);
    }

}
