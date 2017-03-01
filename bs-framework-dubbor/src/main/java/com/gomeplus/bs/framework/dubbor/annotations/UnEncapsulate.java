package com.gomeplus.bs.framework.dubbor.annotations;

import java.lang.annotation.*;

/**
 * @author zhaozhou
 * @Description 默认返回json被封装一层message，data；如果在方法上添加此注解，将不对返回值封装。
 * @date 2016/12/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnEncapsulate {
}