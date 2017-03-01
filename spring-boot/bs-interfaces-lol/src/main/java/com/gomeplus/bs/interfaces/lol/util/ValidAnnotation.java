package com.gomeplus.bs.interfaces.lol.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 校验注解 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface ValidAnnotation {

	//是否可以为空
    boolean nullable() default true;
     
    //最大长度
    int maxLength() default 0;
     
    //最小长度
    int minLength() default 0;
     
}
