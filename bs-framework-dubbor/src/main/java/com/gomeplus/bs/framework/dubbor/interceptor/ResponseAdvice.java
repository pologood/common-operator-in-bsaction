package com.gomeplus.bs.framework.dubbor.interceptor;


import com.gomeplus.bs.framework.dubbor.annotations.UnEncapsulate;
import com.gomeplus.bs.framework.dubbor.constants.RESTfulExceptionConstant;
import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;
import com.gomeplus.bs.framework.dubbor.exceptions.RESTfullBaseException;
import com.gomeplus.bs.framework.dubbor.modle.ResponseModel;
import com.gomeplus.bs.framework.dubbor.utils.DebugMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>全局控制器</p>
 * @author zhaozhou
 * @version 1.o
 * @since 1.0
 */
@RestControllerAdvice
@Slf4j
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @Value("${WEBAPI_DEBUG_STATUS:N}")
    private String WEBAPI_DEBUG_STATUS;

    private final static Map EmptyMap =  new HashMap();

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        UnEncapsulate unEncapsulate = methodParameter.getMethodAnnotation(UnEncapsulate.class);
        if(unEncapsulate != null){
            return false;
        }
        String name = methodParameter.getMethod().toString();
        if(name.matches(".+org.springframework.boot.actuate.endpoint.mvc.+")){
            return false;
        }
        return true;
    }

    /**
     *
     * @param obj controller返回对象
     * @param methodParameter 方法参数
     * @param mediaType 媒体类型
     * @param aClass convert类
     * @param serverHttpRequest http请求
     * @param serverHttpResponse http相应
     * @return 标准返回模型
     */
    @Override
    public ResponseModel beforeBodyWrite(Object obj, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
    	ResponseModel<Object> responseModel = new ResponseModel<Object>(EmptyMap);
        if(obj instanceof ResponseModel){
            return (ResponseModel) obj;
        }
        if(null != obj){
            responseModel.setData(obj);
        }
        return responseModel;
    }

    /**
     * <p>异常处理</p>
     * @param response 标准返回模型
     * @return 标准返回模型
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseModel<Object> handleUnexpectedServerError(Exception exception, HttpServletResponse response) {
        ResponseModel<Object> responseModel = new ResponseModel<Object>(EmptyMap);

        if (WEBAPI_DEBUG_STATUS != null && "Y".equals(WEBAPI_DEBUG_STATUS.trim())){
            responseModel.setDebug(DebugMessageUtil.getDebugMessage(exception));
        }
        if( exception instanceof RESTfullBaseException){
            RESTfullBaseException bce = (RESTfullBaseException) exception;
            responseModel.setMessage(bce.getMessage());

            responseModel.setError(bce.getError());
            response.setStatus(getCode(exception));

            if(exception instanceof RESTfull4xxBaseException){
                log.warn("framework-dubbor-exception:4xx异常警",bce);
            }else
            {
                log.error("framework-dubbor-exception:RESTfullBaseException",exception);
            }

        }else{
            log.error("framework-dubbor-exception:500Exception ",exception);
            responseModel.setMessage(RESTfulExceptionConstant.SYSTEM_BUSY);
            response.setStatus(500);
        }

        return responseModel;
    }

    /**
     * <p>处理没有对应方法的异常</p>
     * @param response  http相应
     * @return 标准返回模型
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseModel<Map> httpRequestMethodNotSupportedExceptionHandler(HttpServletResponse response){
        ResponseModel<Map> responseModel = new ResponseModel<>(EmptyMap);
        responseModel.setMessage("资源不可用");
        return responseModel;
    }

    /**
     * <p>参数校验失败422异常</p>
     * @param exception  MissingServletRequestParameterException
     * @return 标准返回模型
     */
    @ExceptionHandler(value = {
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseModel<Map> missingServletRequestParameterException(Exception exception){
        ResponseModel<Map> responseModel = new ResponseModel<>(EmptyMap);
        responseModel.setMessage("参数校验失败");
        if (WEBAPI_DEBUG_STATUS != null && "Y".equals(WEBAPI_DEBUG_STATUS.trim())){
            responseModel.setDebug(DebugMessageUtil.getDebugMessage(exception));
        }
        log.warn("framework-dubbor-exception:参数校验失败",exception);

        return responseModel;
    }

    private int getCode(Object obj) {
        return Integer.parseInt(obj.getClass().getSimpleName().substring(1, 4));
    }
}
