/**   
 * Copyright © 2016 mx. All rights reserved.
 * 
 * @Title: WebAppConfig.java 
 * @Prject: bs-admin-web
 * @Package: com.gomeplus.bs.web.admin.base.config 
 * @Description: TODO
 * @author: sunyizhong   
 * @date: 2016年10月27日 上午11:44:53 
 * @version: V1.0   
 */
package com.gomeplus.bs.admin.web.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.gomeplus.bs.admin.web.common.interceptor.LogInInterceptor;


@Configuration
public class WebAppConfig extends WebMvcConfigurerAdapter {
	
	@Autowired LogInInterceptor logInInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		 registry.addInterceptor(logInInterceptor);
	}

}
