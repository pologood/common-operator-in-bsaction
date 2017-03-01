package com.gomeplus.bs.framework.dubbor.annotations.resolvers;

import com.gomeplus.bs.framework.dubbor.annotations.PublicParam;
import com.gomeplus.bs.framework.dubbor.constants.RESTfulExceptionConstant;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/10/12 17:34
 */
@Slf4j
@Component("publicParamResolver")
public class PublicParamResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation annotation : annotations) {
            if (PublicParam.class.isInstance(annotation))
                return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
        Annotation[] annotations = parameter.getParameterAnnotations();
        Class<?> parameterType = parameter.getParameterType();
        for (Annotation annotation : annotations) {
            if (PublicParam.class.isInstance(annotation)) {
                PublicParam ann = (PublicParam) annotation;
                String name = ann.name();
                boolean required = ann.required();
                HttpServletRequest httpServletRequest = (HttpServletRequest)webRequest.getNativeRequest();

                if(httpServletRequest.getAttribute(name) == null &&  required){
                    throw new C422Exception(RESTfulExceptionConstant.CHECK_REQUEST_PARAM_FAILED_MSG)
                            .debugMessage("public [" + name + "] param is null");
                }

                if(!(httpServletRequest.getAttribute(name) == null )){
                    try {
                        if ("java.lang.Long".equals(parameterType.getName())) {
                            return Long.parseLong(httpServletRequest.getAttribute(name).toString());
                        }
                        if ("java.lang.Integer".equals(parameterType.getName())) {
                            return Integer.parseInt(httpServletRequest.getAttribute(name).toString());
                        }
                    }catch (NumberFormatException e){
                        log.error("framework-dubbor-exception:FormatException :{}", e);
                        throw new C422Exception(RESTfulExceptionConstant.CHECK_REQUEST_PARAM_FAILED_MSG)
                                .debugMessage("公参[" + name + "]类型转换失败");
                    }
                }
                return httpServletRequest.getAttribute(name);
            }
        }
        return null;
    }
}
