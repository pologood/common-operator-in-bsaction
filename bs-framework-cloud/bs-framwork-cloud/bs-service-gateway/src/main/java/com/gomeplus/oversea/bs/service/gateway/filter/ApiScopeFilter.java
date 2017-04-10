package com.gomeplus.oversea.bs.service.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.gomeplus.oversea.bs.service.gateway.common.MessageContant;
import com.gomeplus.oversea.bs.service.gateway.util.FilterFailUtil;
import com.gomeplus.oversea.bs.service.gateway.util.SwaggerUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;


@Slf4j
public class ApiScopeFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
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
        if(SwaggerUtil.getSwagger()==null){
            log.info("swagger file is null ,can't do schema validation...");
            FilterFailUtil.responseValidateFail(ctx, MessageContant.invalidRequest);
        }else {
            JsonNode swagger = SwaggerUtil.getSwagger();
            JsonNode paths = swagger.get("paths");
            if (paths == null) {
                log.info("swagger file paths is null ,can't do schema validation...");
                FilterFailUtil.responseValidateFail(ctx, MessageContant.invalidRequest);
            }else {
                JsonNode uriNode = paths.get(request.getRequestURI());
                if (uriNode == null) {
                    log.info("swagger file paths url:{} is null ,can't do schema validation...", request.getRequestURI());
                    FilterFailUtil.responseValidateFail(ctx, MessageContant.invalidRequest);
                }else {
                    String method = request.getMethod().toLowerCase();
                    JsonNode methodNode = uriNode.get(method);
                    if (uriNode == null) {
                        log.info("swagger file paths url:{}, method:{} is null ,can't do schema validation...", request.getRequestURI(), method);
                        FilterFailUtil.responseValidateFail(ctx,  MessageContant.invalidRequest);
                    }else {
                        JsonNode apiScope = methodNode.get("x-api-scope");
                        if (apiScope == null || !"outer".equals(apiScope.asText())) {
                            log.info("swagger file paths url:{}, method:{},x-api-scope:{} is inner api ,can't visited", request.getRequestURI(), method);
                            FilterFailUtil.responseInnerApiVisit(ctx, MessageContant.invalidRequest);
                        }
                    }
                }
            }
        }
        long validateTime=System.currentTimeMillis()-startTime;
        log.info("paramsValidateFilterSpendTime:{}",validateTime);
        return null;
    }
}
