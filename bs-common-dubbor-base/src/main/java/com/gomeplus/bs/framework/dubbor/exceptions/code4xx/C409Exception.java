package com.gomeplus.bs.framework.dubbor.exceptions.code4xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;

import java.io.Serializable;

/**
 * @Description 409异常，Conflict 通用冲突
 * @author lichengzhen
 * @date 2016年5月13日 下午2:21:54
 */
public class C409Exception extends RESTfull4xxBaseException implements Serializable{

	private static final long serialVersionUID = 5041250399945421364L;

	public C409Exception(String message) {
		super(message);
	}
}
