package cn.com.mx.webapi.common.annotation.apimeta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

/**
 * @Description 扫包映射方法 
 * @author yuanchangjun
 * @date 2016年5月25日 下午3:15:25
 */
public @interface MethodResource {
	
	String memo() default "";
	
	String name() default "";
	
	String parameters() default "";
}