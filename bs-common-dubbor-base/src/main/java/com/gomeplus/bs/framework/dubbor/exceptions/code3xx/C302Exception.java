package com.gomeplus.bs.framework.dubbor.exceptions.code3xx;

import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull3xxBaseException;

import java.io.Serializable;

/**
 * 302异常 ，Move temporarily：请求的资源临时从不同的 URI响应请求
 * @author zhaozhou
 */

public class C302Exception extends RESTfull3xxBaseException implements Serializable{

	private static final long serialVersionUID = -4669887293221187463L;

	public C302Exception(String message) {
		super(message);
	}
}