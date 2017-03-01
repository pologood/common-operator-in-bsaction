package cn.com.mx.webapi.common.annotation;

import cn.com.mx.webapi.common.model.NeedLogInEnum;

import java.lang.reflect.Method;

/**
 * @Description 登录注解处理 
 * @author wanggang-ds6
 * @date 2016年1月22日 上午11:55:41
 */
public class ImageLoggedInProcessor {
	
	/**
	 * @Description 是否需要登录 
	 * @author wanggang-ds6
	 * @date 2016年1月22日 上午11:54:47
	 * @param obj 类
	 * @param methodName 方法名
	 * @param parameterTypes 参数类型
	 * @return 是否需要登录校验枚举
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static NeedLogInEnum needLogIn(Object obj, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
		Method method = obj.getClass().getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		if (!method.isAnnotationPresent(LoggedIn.class)) {
			return NeedLogInEnum.NO;
		}
		LoggedIn loggedin = method.getAnnotation(LoggedIn.class);
		if(loggedin.optional()){
            return NeedLogInEnum.OPTIONAL;
        }else {
            return NeedLogInEnum.YES;
        }
	}

}
