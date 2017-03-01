package cn.com.mx.webapi.common.annotation.apimeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @Description 扫包影射类 
 * @author yuanchangjun
 * @date 2016年5月25日 下午3:15:38
 */
public @interface Resource {
	
	//模块
	String module() default "";
	
	//类名称
	String name() default "";
	
	//版本
	int version = 1;
}
