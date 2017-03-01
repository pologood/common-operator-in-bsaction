/**   
 * Copyright © 2016 mx. All rights reserved.
 * 
 * @Title: AdminLoggedIn.java 
 * @Prject: bs-admin-web
 * @Package: com.gomeplus.bs.web.admin.base.annotation 
 * @Description: 运营后台登录注解
 * @author: sunyizhong   
 * @date: 2016年10月27日 上午10:53:08 
 * @version: V1.0   
 */
package com.gomeplus.bs.admin.web.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName: UnLoggedIn 
 * @Description: 运营后台登录注解
 * @author: sunyizhong
 * @date: 2016年10月27日 上午10:53:31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnLoggedIn {

	/**
	 * @Description 方法不需要登录校验注解;
     */
	boolean optional() default true;
}
