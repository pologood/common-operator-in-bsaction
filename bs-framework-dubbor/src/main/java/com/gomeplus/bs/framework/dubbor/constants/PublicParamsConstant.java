package com.gomeplus.bs.framework.dubbor.constants;

/**
 *  API提供URL Query Parameters传参和Header 发送参数两种形式，
 *  如果两个参数同时存在，优先取URL传递的参数
 * @Description 公共参数常量 
 * @author wanggang-ds6
 * @date 2016年1月25日 上午11:42:54
 */
public class PublicParamsConstant {

    //header公共参数
	public static final String X_GOMEPLUS_USER_ID = "x-gomeplus-user-id";
	public static final String X_GOMEPLUS_ADMIN_USER_ID = "x-gomeplus-admin-user-id";
	public static final String X_GOMEPLUS_TOKEN = "x-gomeplus-login-token";
	public static final String X_GOMEPLUS_ACCESSTOKEN = "x-gomeplus-access-token";
	public static final String X_GOMEPLUS_DEVICE = "x-gomeplus-device";
	public static final String X_GOMEPLUS_APP = "x-gomeplus-app";
	public static final String X_GOMEPLUS_NET = "x-gomeplus-net";
	public static final String X_GOMEPLUS_TRACE_ID = "x-gomeplus-trace-id";
	public static final String ACCEPT = "accept";
	public static final String JSONP = "jsonp";

	public static final String X_GOMEPLUS_IP = "x-gomeplus-ip";
	public static final String X_GOMEPLUS_SID = "x-gomeplus-sid";

	//query公共参数
	public static final String QUERY_PARAM_X_GOMEPLUS_USER_ID = "userId";
	public static final String QUERY_PARAM_X_GOMEPLUS_ADMIN_USER_ID = "adminUserId";
	public static final String QUERY_PARAM_X_GOMEPLUS_TOKEN = "loginToken";
	public static final String QUERY_PARAM_X_GOMEPLUS_ACCESSTOKEN = "accessToken";
	public static final String QUERY_PARAM_X_GOMEPLUS_DEVICE = "device";
	public static final String QUERY_PARAM_X_GOMEPLUS_APP =  "app";
	public static final String QUERY_PARAM_X_GOMEPLUS_NET =  "net";
	public static final String QUERY_PARAM_ACCEPT = "accept";
	public static final String QUERY_PARAM_TRACEID = "traceId";
	public static final String QUERY_PARAM_JSONP = "jsonp";
	public static final String QUERY_PARAM_APPVERSION = "appVersion";

	public static final String QUERY_PARAM_IP = "ip";
	public static final String QUERY_PARAM_SID = "sid";


	// Accept
	public static final String ACCEPT_TYPE_JSON ="application/json";
	public static final String ACCEPT_TYPE_JAVASCRIPT ="application/javascript";
	public static final String ACCEPT_TYPE_ANYTHING ="*/*";
	public static final String ACCEPT_TYPE_APPLICATION_ANYTHING ="application/*";

	//Content-Type
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";

	// 日志会话id
	public static final String LOG_SESSION_ID = "logSessionId";

	// 公共参数实体名称
	public static final String PUBLIC_PARAMS_NAME = "publicParams";
	// 调试状态
	public static final String WEBAPI_DEBUG_STATUS = "WEBAPI_DEBUG_STATUS";
	// 请求实体
	public static final String REQUEST_ENTITY = "requestEntity";
	// Http body
	public static final String REQUEST_BODY = "requestBody";

	//response jsonschema校验常量
	public static final String REQUEST = "request";
	public static final String RESPONSE = "response";

}
