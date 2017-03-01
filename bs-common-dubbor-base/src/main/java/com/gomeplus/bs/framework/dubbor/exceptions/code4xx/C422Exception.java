package com.gomeplus.bs.framework.dubbor.exceptions.code4xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;

import java.io.Serializable;

/**
 * @Description 422异常 ，Unprocessable Entity：请求格式正确，但是由于含有语义错误，无法响应
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C422Exception extends RESTfull4xxBaseException implements Serializable {
	private static final long serialVersionUID = -2312393803704717855L;
	public C422Exception(String message) {
		super(message);
	}
}
