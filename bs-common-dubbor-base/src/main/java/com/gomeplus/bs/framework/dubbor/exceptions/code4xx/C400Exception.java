package com.gomeplus.bs.framework.dubbor.exceptions.code4xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;

import java.io.Serializable;

/**
 * <p>400异常 :Bad Request 请求格式错误.如Content-Type错误</p>
 *
 */
public class C400Exception extends RESTfull4xxBaseException implements Serializable {

    private static final long serialVersionUID = -5601106178975839965L;

    public C400Exception(String message) {
		super(message);
	}
}