package com.gomeplus.oversea.bs.service.user.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录注册的设备信息
 * Created by baixiangzhu on 2017/2/22.
 */
@Data
public class DeviceVO implements Serializable{
    private static final long serialVersionUID = -6354789480622107963L;

    /**
     * 操作系统
     */
    private String osType;
    /**
     * 系统版本号
     */
    private String osVsersion;

    /**
     * 设置型号
     */
    private String deviceModel;

    /**
     * 设备Id
     */
    private String deviceId;

    public DeviceVO(){}

    public DeviceVO(String osType,String osVsersion,String deviceModel,String deviceId){
        this.osType = osType;
        this.osVsersion = osVsersion;
        this.deviceModel = deviceModel;
        this.deviceId = deviceId;
    }


}
