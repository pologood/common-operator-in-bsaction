package cn.com.mx.webapi.common.model;

import java.io.Serializable;
/**
 * 
 * @Description 公共参数类 
 * @author zhaozhou 修改：token-->loginToken
 * @date 2016年5月3日 上午9:58:25
 */
public class PublicParams implements Serializable{
	private static final long serialVersionUID = 8190396198047028562L;
	
	private String userId;
	private String loginToken;
	private String accessToken;
	private String device;
	private String app;//{AppId}/{From} 主要用于APP统计和适配
	private String net;//网络类型
	private String accept;//可接受的数据类型
	private String traceId;//日志追踪代码
	private String jsonp;//使用jsonp时的回调函数名称
	private String appVersion;//app版本号
	//添加两个公参  by zhaozhao
	private String ip;//客户端ip

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLoginToken() {
		return loginToken;
	}
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getNet() {
		return net;
	}
	public void setNet(String net) {
		this.net = net;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	public String getJsonp() {
		return jsonp;
	}
	public void setJsonp(String jsonp) {
		this.jsonp = jsonp;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}
