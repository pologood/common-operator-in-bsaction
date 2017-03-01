package com.gomeplus.bs.image_web.exceptions;

/**
 * @Description 异常信息 
 * @author wanggang-ds6
 * @date 2016年1月26日 上午10:51:26
 */
public class BaseExceptionMessage {
	
	private String message;
	
	private Object[] args;
	
	protected BaseExceptionMessage(String message) {
		this.message = message;
	}
	
	public BaseExceptionMessage(String format, Object... args) {
		this.message = String.format(format, args);
	}
	
	/**
	 * @Description 设置参数 
	 * @author wanggang-ds6
	 * @date 2016年1月26日 上午11:55:03
	 * @param args
	 * @return
	 */
	public BaseExceptionMessage setArgs(Object... args) {
		this.args = args;
		return this;
	}
	
	@Override
	public String toString() {
		return String.format(message, args);
	}
	public static final String CHECK_DATA_FAILED_MSG = "参数校验失败。";
	public static final String CHECK_JSON_MSG = "请求信息校验失败。";
	public static final String CHECK_RESPONSEJSON_MSG = "Response JsonSchema Check Failed";
	
	public static final BaseExceptionMessage SYSTEM_BUSY = new  BaseExceptionMessage("系统繁忙，请稍后重试。");
	public static final BaseExceptionMessage UNAUTHORIZED = new  BaseExceptionMessage("登录失效，请尝试重新登录。");
		
	public static final BaseExceptionMessage INTERNAL_SERVER_ERROR = new  BaseExceptionMessage("服务暂不可用，请稍后再试。");
	
	public static final BaseExceptionMessage PARSING_BODY_ERROR = new BaseExceptionMessage("提交信息格式不正确，请修改后重试。");
	
	public static final BaseExceptionMessage PARSING_PARAMETERS_ERROR = new BaseExceptionMessage("解析请求参数出错。");

	public static final BaseExceptionMessage CHECK_DATA_FAILED = new  BaseExceptionMessage("校验数据失败:%s");
	public static final BaseExceptionMessage CHECK_PARAMDATA_FAILED = new  BaseExceptionMessage(CHECK_DATA_FAILED_MSG+":%s");
	
//	public static final BaseExceptionMessage RESOURCE_NOT_FOUND = new  BaseExceptionMessage("资源未找到");
	public static final BaseExceptionMessage RESOURCE_FORBIDDEN = new  BaseExceptionMessage("资源不可用， 错误代码[403]");
	
	public static final BaseExceptionMessage UNSUPPORTED_MEDIA_TYPE = new BaseExceptionMessage("服务端不支持Content-Type指定的类型");
	
	public static final BaseExceptionMessage NOT_ACCEPTABLE = new BaseExceptionMessage("不可接受");
	public static final BaseExceptionMessage SYSTEM_ERROR = new BaseExceptionMessage("系统错误");
}
