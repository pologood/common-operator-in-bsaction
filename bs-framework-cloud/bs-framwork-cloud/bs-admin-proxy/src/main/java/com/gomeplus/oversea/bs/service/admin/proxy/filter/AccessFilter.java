package com.gomeplus.oversea.bs.service.admin.proxy.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.gomeplus.oversea.bs.service.admin.proxy.model.ResponseModel;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Iterator;


@Slf4j
public class AccessFilter extends ZuulFilter {
    static JsonNode swagger;

    static {
        try {

            ClassPathResource resource = new ClassPathResource("swagger.json");
            if (resource != null) {
                InputStream inputStream = resource.getInputStream();
                if (inputStream != null) {
                    String in = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
                    if (in != null) {
                        in = in.replaceAll("\r\n", "");
                        ObjectMapper errorMapper = new ObjectMapper();
                        errorMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                        JsonNode jsonNode = errorMapper.readTree(in);
                        AccessFilter.swagger = jsonNode;
                    }
                } else {
                    log.info("ClassPathResource(\"swagger.json\")  is  null");
                }
            }

            if (AccessFilter.swagger == null) {
                log.info("AccessFilter.swagger file is null ");
            }
        } catch (Exception e) {
            log.error("swaggerFileLoadError,{}", e);
        }
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        Enumeration headerEnum = request.getHeaderNames();

        //traceId 设置
        setTraceId(ctx);

        //获取头信息
        JSONObject headerParamJson = new JSONObject();
        while (headerEnum.hasMoreElements()) {
            String name = (String) headerEnum.nextElement();
            headerParamJson.put(name, request.getHeader(name));
        }
        //获取入参
        Enumeration<String> parameterNames = request.getParameterNames();
        JSONObject queryParamJson = new JSONObject();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            queryParamJson.put(name, request.getParameter(name));
        }
        //获取body
        String bodyParamString = "";
        if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) {
            try {
                InputStream is = request.getInputStream();
                bodyParamString = IOUtils.toString(is, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info(String.format("request method:%s, request url: %s,request headerParams:%s,request queryParams:%s,request bodyParams:%s",
                request.getMethod(),
                request.getRequestURL().toString(),
                headerParamJson.toJSONString(),
                queryParamJson.toJSONString(),
                bodyParamString
        ));
        long startTime = System.currentTimeMillis();
        if (!checkPublicParams(request, headerParamJson.toJSONString())) {
            responseValidateFail(ctx, "public parameters validated fail.");
            return null;
        }
        if (!checkQueryParams(request, queryParamJson.toJSONString())) {
            responseValidateFail(ctx, "parameters validated fail.");
            return null;
        }
        ;
        if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) {
            if (!checkBodyParams(request, bodyParamString)) {
                responseValidateFail(ctx, "parameters validated fail.");
                return null;
            }
            ;
        }
        long validateTime = System.currentTimeMillis() - startTime;
        log.info("validateTime:{}", validateTime);
        return null;
    }

    private void setTraceId(RequestContext ctx) {
        String traceId = ctx.getRequest().getHeader("X-Gomeplus-Trace-Id");
        if (traceId == null || "".equals(traceId)) {
            traceId = MDC.get("X-B3-TraceId");
        } else {
            String xB3TraceId = MDC.get("X-B3-TraceId");
            log.info("X-Gomeplus-Trace-Id binding X-B3-TraceId, X-Gomeplus-Trace-Id:{},X-B3-TraceId:{}", traceId, xB3TraceId);
            ctx.addZuulResponseHeader("X-B3-TraceId", xB3TraceId);
        }
        ctx.addZuulResponseHeader("X-Gomeplus-Trace-Id", traceId);
    }

    private void responseValidateFail(RequestContext ctx, String message) {
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(422);
        ResponseModel<Object> responseModel = new ResponseModel<>();
        responseModel.setMessage(message);
        responseModel.setData(new Object());
        ctx.getRequest().setAttribute("validationFail", responseModel);
    }

    private boolean checkPublicParams(HttpServletRequest request, String headerParamData) {
        JsonSchema schema = getSchema(request, "header");
        if (schema == null) {
            return true;
        }
        return validate(schema, headerParamData);
    }

    ;

    private boolean checkQueryParams(HttpServletRequest request, String queryParamData) {
        JsonSchema schema = getSchema(request, "query");
        if (schema == null) {
            return true;
        }
        return validate(schema, queryParamData);
    }

    private boolean checkBodyParams(HttpServletRequest request, String bodyParamData) {
        JsonSchema schema = getSchema(request, "body");
        if (schema == null) {
            return true;
        }
        return validate(schema, bodyParamData);
    }

    ;

    private boolean validate(JsonSchema schema, String data) {
        try {
            JsonNode jsonNode = JsonLoader.fromString(data);
            ProcessingReport validateResult = schema.validate(jsonNode);
            boolean success = validateResult.isSuccess();
            return success;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private JsonSchema getSchema(HttpServletRequest request, String type) {
        try {
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            JSONObject schema = new JSONObject();
            schema.put("$schema", "http://json-schema.org/draft-04/schema#");
            schema.put("type", "object");
            if (AccessFilter.swagger == null) {
                log.info("swagger file is null ,can't do schema validation...");
                return null;
            }
            JsonNode swagger = AccessFilter.swagger;
            JsonNode paths = swagger.get("paths");
            JsonNode uriNode = paths.get(request.getRequestURI());

            String method = request.getMethod().toLowerCase();
            JsonNode methodNode = uriNode.get(method);

            JsonNode paramsNode = methodNode.get("parameters");

            Iterator<JsonNode> paramsNodeIt = paramsNode.elements();
            if ("body".equals(type)) {
                while (paramsNodeIt.hasNext()) {
                    JsonNode paramNode = paramsNodeIt.next();
                    String paramType = paramNode.get("in").asText();
                    if (paramType.equals(type)) {
                        String path;
                        if (paramNode.get("schema") != null && paramNode.get("schema").get("$ref") != null) {
                            path = paramNode.get("schema").get("$ref").asText();
                        } else {
                            return null;
                        }
                        JsonNode bodyDefinition = getSwaggerElement(path);
                        schema.put("properties", bodyDefinition.get("properties"));
                        if (bodyDefinition.get("required") != null) {
                            ArrayNode requiredNode = (ArrayNode) bodyDefinition.get("required");
                            schema.put("required", requiredNode);
                        }
                    }
                }
            } else {
                JSONObject property = new JSONObject();
                JSONArray required = new JSONArray();
                while (paramsNodeIt.hasNext()) {
                    JsonNode paramNode = paramsNodeIt.next();
                    if (paramNode.get("$ref") != null) {
                        JsonNode ref = paramNode.get("$ref");
                        JsonNode param = getSwaggerElement(ref.asText());
                        String paramType = param.get("in").asText();
                        if (paramType.equals(type)) {
                            JSONObject typeNode = new JSONObject();
                            typeNode.put("type", param.get("type").asText());
                            property.put(param.get("name"), typeNode);
                            if (param.get("required") != null && param.get("required").asBoolean()) {
                                required.add(param.get("name"));
                            }
                        }
                    } else {
                        String paramType = paramNode.get("in").asText();
                        JSONObject typeNode = new JSONObject();
                        if (paramType.equals(type)) {
                            property.put(paramNode.get("name"), typeNode);
                            if (paramNode.get("required") != null && paramNode.get("required").asBoolean()) {
                                required.add(paramNode.get("name"));
                            }
                        }
                    }
                }
                schema.put("properties", property);
                if (required.size() > 0) {
                    schema.put("required", required);
                }
            }
            String schemaString = schema.toJSONString();
            JsonNode schemaJson = JsonLoader.fromString(schemaString);
            JsonSchema jsonSchema = factory.getJsonSchema(schemaJson);
            return jsonSchema;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonNode getSwaggerElement(String path) {
        String[] paths = {path};
        if (path.contains("/")) {
            paths = path.split("/");
        }
        int start = 0;
        if ("".equals(paths[0]) || "#".equals(paths[0])) {
            start = 1;
        }
        JsonNode temp = swagger.get(paths[start]);
        if (temp != null) {
            for (int i = start + 1; i < paths.length; i++) {
                if (temp != null) {
                    temp = temp.get(paths[i]);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
        return temp;
    }

}
