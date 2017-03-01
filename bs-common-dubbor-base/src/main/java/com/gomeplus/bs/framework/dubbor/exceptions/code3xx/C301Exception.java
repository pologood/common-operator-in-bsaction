package com.gomeplus.bs.framework.dubbor.exceptions.code3xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull3xxBaseException;

import java.io.Serializable;

/**
 * 301异常：Moved Permanently：资源或API被迁移到新的URL，API 更名应该使用该code
 * @author zhaozhou
 */
public class C301Exception extends RESTfull3xxBaseException implements Serializable {

	private static final long serialVersionUID = 2471232079442861956L;
	public C301Exception(String message) {
		super(message);
	}
}