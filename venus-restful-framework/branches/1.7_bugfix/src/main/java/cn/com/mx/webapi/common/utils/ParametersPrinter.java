package cn.com.mx.webapi.common.utils;

import java.util.HashMap;
import java.util.Map;

import cn.com.mx.webapi.common.model.ResponseModel;
import com.alibaba.fastjson.JSONObject;

/**
 * 打印入参和出参
 * @author wanggang-ds6
 * @since 2016年1月25日 下午5:10:53
 */
public class ParametersPrinter {

    private static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();
    
    /**
     * 设置开始时间
     * @param timeMillis 开始时间
     */
    public static void setStartTime(long timeMillis) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startTime", timeMillis);
        threadLocal.set(paramMap);
    }
    
    /**
     * 设置结束时间
     * @param timeMillis 结束时间
     */
    public static void setEndTime(long timeMillis) {
        long costTime = timeMillis - (long) threadLocal.get().get("startTime");
        threadLocal.get().put("costTime", costTime);
        threadLocal.get().put("endTime", timeMillis);
    }
    
    /**
     * 设置请求参数
     * @param servletParam 请求参数
     */
    public static void setRequestParam(ResourceParameters servletParam) {
        threadLocal.get().put("requestParam", servletParam);
    }
    
    /**
     * 设置请求地址
     * @param path 请求地址
     */
    public static void setRequestUrl(String path) {
        threadLocal.get().put("requestUrl", path);
    }
    
    /**
     * 设置请求实体
     * @param body 请求实体
     */
    public static void setRequestBody(JSONObject body) {
        threadLocal.get().put("requestBody", body);
    }
    
    /**
     * 设置应答实体
     * @param responseModel 应答实体
     */
    public static void setResponseModel(ResponseModel responseModel) {
        threadLocal.get().put("responseModel", responseModel);
    }
    
    /**
     * 设置重定向地址
     * @param path 重定向地址
     */
    public static void setResponseRedirect(String path) {
        threadLocal.get().put("responseRedirect", path);
    }
    
    /**
     * 设置应答码
     * @param responseCode 应答码
     */
    public static void setResponseCode(int responseCode) {
        threadLocal.get().put("responseCode", responseCode);
    }
    
    /**
     * 转换字符串输出
     * @param isDelete 是否删除，如果之后不再使用，请删除
     * @return 字符串结果
     */
    public static String toString(boolean isDelete) {
        // 避免日志中requestBody打印2遍
        ResourceParameters reqParam = (ResourceParameters) threadLocal.get().get("requestParam");
        if(null != reqParam){
            reqParam.remove("requestBody");
        }
        String rtnValue =  JSONObject.toJSONString(threadLocal.get());
        if (isDelete) {
            threadLocal.remove();
        }
        return rtnValue;        
    }
    
    /**
     * 转换字符串输出 (敏感信息相关URI使用,过滤请求参数中的敏感信息)
     * @param isDelete 是否删除
     * @param paramNames 敏感信息字段列表
     * @return 字符串结果
     */
    public static String toStringWithoutPassword(boolean isDelete, String[] paramNames) {
        // 1 requestBody
        // 由于有些uri不同的httpmethod提交时,对应不同的参数,只在有敏感信息变量时进行过滤
        final JSONObject requestBody = (JSONObject) threadLocal.get().get("requestBody");
        if (null != requestBody) {
            for (final String paramName: paramNames) {
                if (null != requestBody.get(paramName)) {
                    requestBody.put(paramName, "*");
                }
            }
        }

        // 2 requestParam.requestBody
        ResourceParameters reqParam = (ResourceParameters) threadLocal.get().get("requestParam");
        if (null != reqParam){
            reqParam.remove("requestBody");
        }

        String rtnValue =  JSONObject.toJSONString(threadLocal.get());
        if (isDelete) {
            threadLocal.remove();
        }
        return rtnValue;
    }
}
