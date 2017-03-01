package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 301异常：Moved Permanently：资源或API被迁移到新的URL，API 更名应该使用该code
 * @author zhaozhou
 * @date 2016/08/09
 */
public class C301Exception extends BaseCodeException {
		
	private static final long serialVersionUID = 2859775942753483009L;
	
	private String redirectPath;

	public C301Exception(String redirectPath) {
		super();
		this.redirectPath = redirectPath;
	}
	
	public C301Exception(String redirectPath, BaseExceptionMessage message) {
		super(message);
		this.redirectPath = redirectPath;
	}
	
	public String getRedirectPath() {
		return redirectPath;
	}
}
