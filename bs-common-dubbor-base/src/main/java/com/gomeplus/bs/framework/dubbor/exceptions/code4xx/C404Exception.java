package com.gomeplus.bs.framework.dubbor.exceptions.code4xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;

import java.io.Serializable;

/**
 * @Description 404异常 ，Not Found：请求失败，请求所希望得到的资源未被在服务器上发现
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C404Exception extends RESTfull4xxBaseException implements Serializable {

	private static final long serialVersionUID = 4127623155490726561L;

	public C404Exception(String message) {
		super(message);
	}
}