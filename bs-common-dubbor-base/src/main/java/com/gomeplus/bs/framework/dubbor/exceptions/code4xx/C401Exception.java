package com.gomeplus.bs.framework.dubbor.exceptions.code4xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;

import java.io.Serializable;

/**
 * @Description 401异常 ，Unauthorized：当前请求需要用户验证
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
@SuppressWarnings("SerializableHasSerializationMethods")
public class C401Exception extends RESTfull4xxBaseException implements Serializable {

	private static final long serialVersionUID = -8923853041057121290L;

	public C401Exception(String message) {
		super(message);
	}
}
