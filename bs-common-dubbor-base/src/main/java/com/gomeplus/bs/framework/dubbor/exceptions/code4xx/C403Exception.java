package com.gomeplus.bs.framework.dubbor.exceptions.code4xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;

import java.io.Serializable;

/**
 * @Description 403异常 ，Not Acceptable：请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体。
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C403Exception extends RESTfull4xxBaseException implements Serializable {

	private static final long serialVersionUID = 4956769958820573586L;

	public C403Exception(String message) {
		super(message);
	}
}