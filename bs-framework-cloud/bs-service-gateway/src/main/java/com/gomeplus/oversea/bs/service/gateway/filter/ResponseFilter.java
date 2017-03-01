package com.gomeplus.oversea.bs.service.gateway.filter;

import com.gomeplus.oversea.bs.service.gateway.model.ResponseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 2017/1/22
 *
 * @author erdaoya
 * @since 1.1
 */
@Slf4j
public class ResponseFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try (InputStream responseDataStream = ctx.getResponseDataStream()) {
            ResponseModel<Object> responseModel = new ResponseModel<>();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            if (responseDataStream == null) {
                //是否是入参校验错误
                if(ctx.getRequest().getAttribute("validationFail")!=null){
                    ResponseModel paramError = (ResponseModel)ctx.getRequest().getAttribute("validationFail");
                    ctx.setResponseBody(mapper.writeValueAsString(paramError));
                }else {
                    responseModel.setData(new Object());
                    ctx.setResponseBody(mapper.writeValueAsString(responseModel));
                }
                return null;
            }

            final String responseData = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));
            if (StringUtils.isEmpty(responseData.trim())) {
                responseModel.setData(new Object());
                ctx.setResponseBody(mapper.writeValueAsString(responseModel));
                return null;
            }
            int statusCode = ctx.getResponseStatusCode();
            JsonNode jsonNode = mapper.readTree(responseData);

            if (statusCode >= 400 && statusCode < 500) {
                ObjectMapper errorMapper = new ObjectMapper();
                errorMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                errorMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

                JsonNode message = jsonNode.get("message");
                JsonNode errorJsonNode = null;
                try {
                    errorJsonNode = errorMapper.readTree(message.asText());
                } catch (Exception e) {

                }
                if (errorJsonNode != null && errorJsonNode.get("error") != null) {
                    JsonNode innerMessage = errorJsonNode.get("message");
                    if (innerMessage == null) {
                        responseModel.setMessage("");
                    } else {
                        responseModel.setMessage(innerMessage.textValue());
                    }
                    JsonNode error = errorJsonNode.get("error");
                    responseModel.setError(error);
                } else {
                    if (message == null) {
                        responseModel.setMessage("");
                    } else {
                        responseModel.setMessage(message.textValue());
                    }
                }
                responseModel.setData(new Object());
            } else if (statusCode >= 500) {
                responseModel.setMessage("System busy，try later please.");
                responseModel.setData(new Object());
            } else {
                responseModel.setData(jsonNode);
            }
            ctx.setResponseBody(mapper.writeValueAsString(responseModel));


        } catch (IOException e) {
            log.warn("Error read body", e);
        }
        return null;
    }
}

