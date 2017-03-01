package cn.com.mx.webapi.common.annotation;

import java.lang.annotation.*;

/**
 * @Description 是否需要登录 
 * @author wanggang-ds6
 * @date 2016年1月21日 下午3:25:32
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ImageLoggedIn {
	/**
	 * @Description 方法登录校验注解;
	 * 	1.使用@LoggedIn注解：登录校验，客户端需传loginToken和userId
	 * 	2.使用@LoggedIn(optional=true):校验可选（即传loginToken和userId校验，不传不校验）
	 * @return 登录校验是否可选
     */
	boolean optional() default false;
}
