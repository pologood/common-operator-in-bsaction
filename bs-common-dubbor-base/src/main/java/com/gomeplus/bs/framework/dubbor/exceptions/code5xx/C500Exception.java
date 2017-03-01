package com.gomeplus.bs.framework.dubbor.exceptions.code5xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull5xxBaseException;

import java.io.Serializable;

/**
 * @Description 500异常 ，Internal Server Error：服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C500Exception extends RESTfull5xxBaseException implements Serializable{

	private static final long serialVersionUID = 3292618827942305707L;

	public C500Exception(String message) {
		super(message);
	}
}
