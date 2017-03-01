package cn.com.mx.webapi.common.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UrlConstant {

    /** key为url value为变量名 **/
    public final static Map<String, String[]> passwordUrlMap;
    private static Set<String> passwordUrlSet;
    
    static{
        passwordUrlMap = new HashMap<>();
        // 会员
        passwordUrlMap.put("/user/login", new String[]{"password"});
        passwordUrlMap.put("/operator/login", new String[]{"password"});
        passwordUrlMap.put("/operator/updateOperatorPwd ", new String[]{"password",
                "oldPassword", "newPassword", "confirmPassword"});
        passwordUrlMap.put("/user/merchantUserLoginAction", new String[]{"password"});
        passwordUrlMap.put("/user/personalInfo", new String[]{"password"});
        passwordUrlMap.put("/user/thirdPartyRegistAction", new String[]{"password"});
        passwordUrlMap.put("/user/passwordResetAction", new String[]{"newPassword"});
        passwordUrlMap.put("/user/passwordModifyingAction", new String[]{"newPassword"});
        passwordUrlMap.put("/user/passwordModifyingCheckAction", new String[]{"oldPassword"});
        // 账户
        passwordUrlMap.put("/account/paymentPasswordVerification", new String[]{"paymentPassword"});
        passwordUrlMap.put("/account/paymentPassword", new String[]{"paymentPassword"});
        passwordUrlMap.put("/account/withdrawalApplication", new String[]{"paymentPassword"});
        
        passwordUrlSet = passwordUrlMap.keySet();
        
    }
    
    /**
     * 判断是否为需要进行特殊处理的URI
     * @return 配位上则返回对应uri,匹配不上返回null
     */
    public static String getPwdUrl(String requestURI){
        for (String url : passwordUrlSet) {
            if(requestURI.endsWith(url)){
                return url;
            }
        }
        return null;
    }
}
