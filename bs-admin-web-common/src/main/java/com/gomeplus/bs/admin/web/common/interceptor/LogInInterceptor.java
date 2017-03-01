/**   
 * Copyright © 2016 mx. All rights reserved.
 * 
 * @Title: logInInterceptor.java 
 * @Prject: bs-admin-web
 * @Package: com.gomeplus.bs.web.admin.base.interceptor 
 * @Description: 登录拦截器
 * @author: sunyizhong   
 * @date: 2016年10月27日 上午11:00:56 
 * @version: V1.0   
 */
package com.gomeplus.bs.admin.web.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.gomeplus.bs.admin.web.common.annotation.UnLoggedIn;
import com.gomeplus.bs.admin.web.common.util.RedisTemplate;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C401Exception;

@Component
public class LogInInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			UnLoggedIn unLoggedIn = ((HandlerMethod) handler).getMethodAnnotation(UnLoggedIn.class);
			if (unLoggedIn != null && unLoggedIn.optional() == true) {
				return true;
			} else {
				// 获取公参
				String token = request.getParameter("adminLoginToken") != null ? request.getParameter("adminLoginToken") : request.getHeader("x-gomeplus-admin-login-token");
				String adminUserId = request.getParameter("adminUserId") != null ? request.getParameter("adminUserId") : request.getHeader("x-gomeplus-admin-user-id");
				//校验公参
				if (token == null || adminUserId == null) {
					throw new C401Exception("登录失效，请尝试重新登录");
				}else {
					//验证公参是否正确
					String queryToken = RedisTemplate.get(stringRedisTemplate, Long.parseLong(adminUserId));
					if (token.equals(queryToken)) {
						return true;
					}else {
						throw new C401Exception("admin-login-token失效，请尝试重新登录");
					}
				}
			}
		} else {
			return true;
		}
	}

}
