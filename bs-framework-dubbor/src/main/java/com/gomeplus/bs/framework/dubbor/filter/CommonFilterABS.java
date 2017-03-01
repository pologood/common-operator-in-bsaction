package com.gomeplus.bs.framework.dubbor.filter;

import com.gomeplus.bs.framework.dubbor.constants.PublicParamsConstant;
import com.gomeplus.bs.framework.dubbor.constants.RESTfulExceptionConstant;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C406Exception;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import com.gomeplus.bs.framework.dubbor.modle.PublicParams;
import com.gomeplus.bs.framework.dubbor.utils.InternetProtocol;
import com.google.common.base.Strings;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.UUID;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/29
 */
public class CommonFilterABS {
    protected void setpublicParams(HttpServletRequest request, PublicParams model) {
        String pathInfo = request.getPathInfo();
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_TOKEN, PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_TOKEN, "setLoginToken", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_USER_ID, PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_USER_ID, "setUserId", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_ACCESSTOKEN, PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_ACCESSTOKEN, "setAccessToken", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_DEVICE, PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_DEVICE, "setDevice", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_APP, PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_APP, "setApp", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_NET, PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_NET, "setNet", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.ACCEPT, PublicParamsConstant.QUERY_PARAM_ACCEPT, "setAccept", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_TRACE_ID, PublicParamsConstant.QUERY_PARAM_TRACEID, "setTraceId", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.JSONP, PublicParamsConstant.QUERY_PARAM_JSONP, "setJsonp", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.QUERY_PARAM_APPVERSION, PublicParamsConstant.QUERY_PARAM_APPVERSION, "setAppVersion", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_IP, PublicParamsConstant.QUERY_PARAM_IP, "setIp", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_SID, PublicParamsConstant.QUERY_PARAM_SID, "setSid", String.class, pathInfo, false);
        setValue(request, model, PublicParamsConstant.X_GOMEPLUS_ADMIN_USER_ID, PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_ADMIN_USER_ID, "setAdminUserId", String.class, pathInfo, false);
        model.setIp(InternetProtocol.getRemoteAddr(request));
    }
    protected void setValue(HttpServletRequest req, PublicParams model, String key, String queryParamKey, String methodName, Class<?> keyType, String pathInfo, boolean isNullAble) {
        try {
            String value;
            value = req.getParameter(queryParamKey) != null ? req.getParameter(queryParamKey) : req.getHeader(key);
            //sid放入MDC
            if (queryParamKey.equals(PublicParamsConstant.QUERY_PARAM_SID) && value != null) {
                MDC.put(PublicParamsConstant.QUERY_PARAM_SID, value);
            }
            if (isNullAble && value == null) {//是否非必填
                throw new C422Exception(RESTfulExceptionConstant.CHECK_REQUEST_PARAM_FAILED_MSG).debugMessage("公参[" + key + "]必传");
            }

            if (value != null) {
                value = URLDecoder.decode(value, "UTF-8");
                req.setAttribute(queryParamKey, value);
                setValueByInvokeMethod(model, keyType, value, methodName);
            }
        } catch (Exception ex) {
            throw new C422Exception(RESTfulExceptionConstant.CHECK_REQUEST_PARAM_FAILED_MSG).debugMessage("获取公参[" + key + "]失败");
        }
    }

    private void setValueByInvokeMethod(PublicParams model, Class<?> keyType, String value, String methodName)
            throws Exception {
        Method method = model.getClass().getMethod(methodName, keyType);
        if ("Integer".equals(keyType.getSimpleName())) {
            method.invoke(model, Integer.valueOf(value));
        } else if ("Long".equals(keyType.getSimpleName())) {
            method.invoke(model, Long.valueOf(value));
        } else if ("String".equals(keyType.getSimpleName())) {
            method.invoke(model, value);
        } else {
            throw new RuntimeException("unhandled exception !");
        }
    }

    protected void checkAccept(PublicParams model) {
        String accept = model.getAccept();
        //如果没有accept，默认application/json
        if (Strings.isNullOrEmpty(accept)) {
            accept = PublicParamsConstant.ACCEPT_TYPE_JSON;
        }
        if (!accept.contains(PublicParamsConstant.ACCEPT_TYPE_JAVASCRIPT)
                && !accept.contains(PublicParamsConstant.ACCEPT_TYPE_JSON)
                && !accept.contains(PublicParamsConstant.ACCEPT_TYPE_ANYTHING)
                && !accept.contains(PublicParamsConstant.ACCEPT_TYPE_APPLICATION_ANYTHING)) {
            throw new C406Exception(RESTfulExceptionConstant.NOT_ACCEPTABLE);
        }
    }

    protected void addTraceId(HttpServletRequest req, HttpServletResponse resp) {
        String traceId = req.getParameter(PublicParamsConstant.QUERY_PARAM_TRACEID) != null ? req.getParameter(PublicParamsConstant.QUERY_PARAM_TRACEID) : req.getHeader(PublicParamsConstant.X_GOMEPLUS_TRACE_ID);
        if (traceId == null || traceId.trim().length() == 0) {
            traceId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        }
        // 设置请求id
        MDC.put(PublicParamsConstant.LOG_SESSION_ID, traceId);

        // 将id放到相应头
        resp.setHeader(PublicParamsConstant.X_GOMEPLUS_TRACE_ID, MDC.get(PublicParamsConstant.LOG_SESSION_ID));
    }

    protected void removeTraceId() {
        MDC.remove(PublicParamsConstant.LOG_SESSION_ID);
    }
}
