package com.gomeplus.oversea.bs.service.user.enums;

/**
 * Created by liuliyun-ds on 2017/2/14.
 * 短信业务类型
 */
public enum SmsServiceType {

    /**
     * 用户注册下发短信验证码
     */
    REGIST(10,"注册","[GomePlus] 您的验证码为smsCode，请于10 分钟内正确输入。不要告诉别人哦！"),
    /**
     * 找回密码下发短信验证码
     */
    FINDPWD(20,"找回密码","[GomePlus] 您正在找回登录密码，验证码为smsCode，请于10 分钟内正确输入！"),
    /**
     * 修改密码下发短信验证码
     */
    UPDATEPWD(40,"修改密码","[GomePlus] 您正在修改登录密码，验证码为smsCode，请于10 分钟内正确输入！"),
    /**
     * 绑定手机号下发短信验证码
     */
    BINDPHONE(30,"绑定手机号","[GomePlus] 您的验证码为smsCode，请于10 分钟内正确输入。不要告诉别人哦！"),
    /**
     * 邀请用户下发短信验证码
     */
    RECOMMEND_MSG(50,"邀请用户","HI，我最近在玩“Gomeplus”扫货，找便宜，拿返利，抢红包，点击http:XXXXXXXXX和我一起玩吧！");
    private int code;
    private String desc;
    private String message;
    private SmsServiceType(int code, String desc,String message){
        this.code = code;
        this.desc = desc;
        this.message = message;
    }

    public static SmsServiceType getEnum(int code){
        for (SmsServiceType item : SmsServiceType.values()) {
            if(item.code == code){
                return item;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getMessage(){
        return message;
    }

    public static boolean contain(String name){
        try {
            SmsServiceType.valueOf(name);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
