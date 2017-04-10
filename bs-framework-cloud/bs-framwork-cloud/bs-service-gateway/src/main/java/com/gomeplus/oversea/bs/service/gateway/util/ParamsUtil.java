package com.gomeplus.oversea.bs.service.gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by shangshengfang on 2017/2/28.
 */
@Slf4j
public class ParamsUtil {
    public static String getBodyFromRequest(HttpServletRequest request){
        String bodyParamString = "";
        if("POST".equals(request.getMethod())||"PUT".equals(request.getMethod())) {
            try {
                InputStream is = request.getInputStream();
                bodyParamString = IOUtils.toString(is, "UTF-8");
                return bodyParamString;
            } catch (Exception e) {
                log.info("get Request Body Error.url:{}",request.getRequestURI(),e);
                return null;
            }
        }else{
            return null;
        }
    }

    public static String getQueryFromRequest(HttpServletRequest request)throws Exception {
        JSONObject queryForJsonFromRequest = getQueryForJsonFromRequest(request);
        return queryForJsonFromRequest.toJSONString();
    }

    public static JSONObject getQueryForJsonFromRequest (HttpServletRequest request) throws Exception {
        Enumeration<String> parameterNames = request.getParameterNames();
        JSONObject queryParamJson=new JSONObject();
        while(parameterNames.hasMoreElements()){
            String name = (String) parameterNames.nextElement();
            JsonNode jsonNodeFromSwagger = getJsonNodeFromSwagger(request.getRequestURI(), request.getMethod(), name);
            if(jsonNodeFromSwagger!=null) {
                String type = jsonNodeFromSwagger.get("type").asText();
                if(jsonNodeFromSwagger.get("required")!=null && jsonNodeFromSwagger.get("required").asBoolean() && Strings.isNullOrEmpty(request.getParameter(name))){
                    throw new Exception("requiredParams");
                }
                setParamByType(type, name, request.getParameter(name), queryParamJson);
            }else{
                queryParamJson.put(name, request.getParameter(name));
            }
        }
        return queryParamJson;
    }
    public static String getHeaderFromRequest(HttpServletRequest request){
        Enumeration headerEnum = request.getHeaderNames();
        JSONObject headerParamJson=new JSONObject();
        while(headerEnum.hasMoreElements()){
            String name = (String) headerEnum.nextElement();
            JsonNode jsonNodeFromSwagger = getJsonNodeFromSwagger(request.getRequestURI(), request.getMethod(), name);

            String type = jsonNodeFromSwagger.get("type").asText();
            if(!"string".equals(type)){
                switch (type) {
                    case "integer": headerParamJson.put(name, Integer.valueOf(request.getHeader(name))); break;
                    case "long": headerParamJson.put(name, Long.valueOf(request.getHeader(name))); break;
                    case "float": headerParamJson.put(name, Float.valueOf(request.getHeader(name))); break;
                    case "double": headerParamJson.put(name, Double.valueOf(request.getHeader(name))); break;
                    case "boolean": headerParamJson.put(name, Boolean.valueOf(request.getHeader(name))); break;
                }

            }else {
                headerParamJson.put(name, request.getHeader(name));
            }
        }
        return headerParamJson.toJSONString();
    }
    public static JSONObject getHeaderForJsonFromRequest(HttpServletRequest request){
        Enumeration headerEnum = request.getHeaderNames();
        JSONObject headerParamJson=new JSONObject();
        while(headerEnum.hasMoreElements()){
            String name = (String) headerEnum.nextElement();
            headerParamJson.put(name,request.getHeader(name));
        }
        return headerParamJson;
    }

    public static JsonNode getJsonNodeFromSwagger(String requestURI,String requestMethod,String paramName){
        JsonNode swagger = SwaggerUtil.getSwagger();
        JsonNode paths = swagger.get("paths");
        JsonNode uriNode = paths.get(requestURI);
        String method =requestMethod.toLowerCase();
        JsonNode methodNode = uriNode.get(method);
        JsonNode paramsNode = methodNode.get("parameters");
        Iterator<JsonNode> paramsNodeIt = paramsNode.iterator();
        while(paramsNodeIt.hasNext()){
            JsonNode next = paramsNodeIt.next();
            if(next.get("name")!=null && paramName.equals(next.get("name").asText())){
                return next;
            }
        }
        return null;
    }
    static void setParamByType(String type,String parameterName,String parameterValue,JSONObject queryParamJson){
        if(!"string".equals(type)){
                switch (type) {
                    case "integer":
                        queryParamJson.put(parameterName, Strings.isNullOrEmpty(parameterValue)?null:Integer.valueOf(parameterValue));
                        break;
                    case "long":
                        queryParamJson.put(parameterName, Strings.isNullOrEmpty(parameterValue)?null:Long.valueOf(parameterValue));
                        break;
                    case "float":
                        queryParamJson.put(parameterName, Strings.isNullOrEmpty(parameterValue)?null:Float.valueOf(parameterValue));
                        break;
                    case "double":
                        queryParamJson.put(parameterName, Strings.isNullOrEmpty(parameterValue)?null:Double.valueOf(parameterValue));
                        break;
                    case "boolean":
                        queryParamJson.put(parameterName, Strings.isNullOrEmpty(parameterValue)?null:Boolean.valueOf(parameterValue));
                        break;
                    case "number":
                        queryParamJson.put(parameterName, Strings.isNullOrEmpty(parameterValue)?null:Long.valueOf(parameterValue));
                        break;
                }
        }else {
            queryParamJson.put(parameterName, parameterValue);
        }
    }


}
