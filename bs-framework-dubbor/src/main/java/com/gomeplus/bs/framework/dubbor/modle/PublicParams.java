package com.gomeplus.bs.framework.dubbor.modle;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 
 * @Description 公共参数类 
 * @author zhaozhou 修改：token-->loginToken
 * @date 2016年5月3日 上午9:58:25
 */
@ToString
@Data
public class PublicParams implements Serializable{
	private static final long serialVersionUID = 8190396198047028562L;
	
	private String userId;
	private String adminUserId;
	private String loginToken;
	private String accessToken;
	private String device;
	private String app;//{AppId}/{From} 主要用于APP统计和适配
	private String net;//网络类型
	private String accept;//可接受的数据类型
	private String traceId;//日志追踪代码
	private String jsonp;//使用jsonp时的回调函数名称
	private String appVersion;//app版本号
	private String ip;//客户端ip
	private String sid;
}
