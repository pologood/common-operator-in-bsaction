package com.gomeplus.oversea.bi.service.item.utils;

import com.gomeplus.oversea.bs.common.exception.code4xx.C404Exception;

/**
 * 2017/2/14
 * 校验工具类
 *
 */
public class ValidateUtils {
	/**
	 * 校验参数非空
	 * @param obj 参数
	 * @param message 错误信息
	 */
	public static void checkNotNull(Object obj, String message) {
		if(obj == null){
			throw new C404Exception(message);
		}
	}
	
}
