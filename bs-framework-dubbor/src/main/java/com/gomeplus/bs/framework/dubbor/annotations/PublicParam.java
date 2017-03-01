package com.gomeplus.bs.framework.dubbor.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/10/12 17:33
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PublicParam {
    String name();
    boolean required() default true;
}
