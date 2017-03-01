package com.gomeplus.bs.service.lol.util;

import java.lang.reflect.Field;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;

import com.gomeplus.bs.framework.dubbor.constants.RESTfulExceptionConstant;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.interfaces.lol.util.ValidAnnotation;

/**
 * @Description 字段校验类
 */
@Slf4j
public class ValidateUtil {

    /**
     * @Description 校验字段是否为空
     * @param value 被校验参数
     * @return
     */
    public static boolean isNull(Object value) {
        if (value == null) {
            return true;
        } else if ((value instanceof String) && (((String) value).length() == 0)) {
            return true;
        }
        return false;
    }

    /**
     * @Description 解析的入口,needValids是需要校验的实体类属性名，否则写有校验注解的属性值会被校验
     * @param value 被校验参数
     * @return
     */
	public static void valid(Object object, Object... needValids) {
		// 获取object的类型
		Class<? extends Object> clazz = object.getClass();
		// 循环父类获取父类声明的成员
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			// 获取该类型声明的成员
			Field[] fields = clazz.getDeclaredFields();
			// 遍历属性
			for (Field field : fields) {
				// 对于private私有化的成员变量，通过setAccessible来修改器访问权限
				field.setAccessible(true);
				if (needValids.length > 0) {
					for (Object obj : needValids) {
						if (obj.toString().equals(field.getName())) {
							validate(field, object);
							break;
						}
					}
				} else {
					validate(field, object);
				}
				// 重新设置会私有权限
				field.setAccessible(false);
			}
		}
	}
    
    /**
     * @Description 校验方法
     * @param field 被校验对象属性
     * @param object 被校验对象
     * @return
     */
    public static void validate(Field field, Object object) {

        Object value = null;
        ValidAnnotation dv;

        // 获取对象的成员的注解信息
        dv = field.getAnnotation(ValidAnnotation.class);
        try {
            value = field.get(object);
        } catch (Exception e) {
            log.error("获取对象的成员的注解参数值错误！", e);
        }

        if (dv == null)
            return;

        // 为null判断
        if (!dv.nullable()) {
            if (value == null || StringUtils.isBlank(value.toString())) {
    			throw new C422Exception(RESTfulExceptionConstant.CHECK_REQUEST_PARAM_FAILED_MSG).debugMessage("参数名"+field.getName()+"不能为空"); 
            }
        }
       
    }
}
