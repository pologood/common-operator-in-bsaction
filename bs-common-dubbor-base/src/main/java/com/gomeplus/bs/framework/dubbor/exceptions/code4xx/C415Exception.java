package com.gomeplus.bs.framework.dubbor.exceptions.code4xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;

import java.io.Serializable;

/**
 * @Description 415异常 ，Unsupported Media Type：对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式，因此请求被拒绝。
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C415Exception extends RESTfull4xxBaseException implements Serializable {
	private static final long serialVersionUID = 551808489769571904L;

	public C415Exception(String message) {
		super(message);
	}
}
