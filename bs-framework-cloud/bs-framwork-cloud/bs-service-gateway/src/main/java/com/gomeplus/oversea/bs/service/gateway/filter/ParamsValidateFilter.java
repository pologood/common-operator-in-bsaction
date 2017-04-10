package com.gomeplus.oversea.bs.service.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.gomeplus.oversea.bs.service.gateway.common.MessageContant;
import com.gomeplus.oversea.bs.service.gateway.util.FilterFailUtil;
import com.gomeplus.oversea.bs.service.gateway.util.ParamsUtil;
import com.gomeplus.oversea.bs.service.gateway.util.SchemaFactory;
import com.gomeplus.oversea.bs.service.gateway.util.SwaggerUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;


@Slf4j
public class ParamsValidateFilter extends ZuulFilter {



    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 7;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getRequest().getAttribute("validationFail")==null;
    }

    @Override
    public Object run() {
        long startTime=System.currentTimeMillis();
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //获取头信息
        //获取头信息
        JSONObject headerParamJson=ParamsUtil.getHeaderForJsonFromRequest(request);
        //获取入参
        String queryParamJson="";
        try {
            //获取入参
            queryParamJson= ParamsUtil.getQueryFromRequest(request);
        }catch(Exception e){
            FilterFailUtil.responseValidateFail(ctx, MessageContant.paramInvalide);
            return null;
        }
        //获取body
        String bodyParamString = ParamsUtil.getBodyFromRequest(request);

        if(!checkPublicParams(request,headerParamJson.toJSONString())){
            FilterFailUtil.responseValidateFail(ctx, MessageContant.publicParamInvalide);
            return null;
        }
        if(!checkQueryParams(request,queryParamJson)){
            FilterFailUtil.responseValidateFail(ctx,MessageContant.paramInvalide);
            return null;
        };
        if("POST".equals(request.getMethod())||"PUT".equals(request.getMethod())){
            if(!checkBodyParams(request,bodyParamString)){
                FilterFailUtil.responseValidateFail(ctx,MessageContant.paramInvalide);
                return null;
            };
        }
        long validateTime=System.currentTimeMillis()-startTime;
        log.info("paramsValidateFilterSpendTime:{}",validateTime);
        return null;
    }


    private boolean checkPublicParams(HttpServletRequest request,String headerParamData){
        JsonSchema schema= SchemaFactory.getHeaderSchema(request);
        if(schema==null){
            return true;
        }
        return validate(schema,headerParamData);
    };

    private boolean checkQueryParams(HttpServletRequest request,String queryParamData){
        JsonSchema schema= SchemaFactory.getQuerySchema(request);
        if(schema==null){
            return true;
        }
        return validate(schema,queryParamData);
    }
    private boolean checkBodyParams(HttpServletRequest request,String bodyParamData){
        JsonSchema schema= SchemaFactory.getBodySchema(request);
        if(schema==null){
            return true;
        }
        return validate(schema,bodyParamData);
    };
    private boolean validate(JsonSchema schema,String data){
        try {
            JsonNode jsonNode = JsonLoader.fromString(data);
            ProcessingReport validateResult = schema.validate(jsonNode);
            boolean success = validateResult.isSuccess();
            if(!success){
                log.error("swagger JsonSchema validate error. schema:[{}],data:[{}]",schema.toString(),data);
            }
            return success;
        }catch(Exception ex){
            log.error("swagger JsonSchema validate exception. schema:[{}],data:[{}]",schema.toString(),data);
        }
        return true;
    }

}
