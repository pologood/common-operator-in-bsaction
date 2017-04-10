package com.gomeplus.oversea.bs.service.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.gomeplus.oversea.bs.service.gateway.common.MessageContant;
import com.gomeplus.oversea.bs.service.gateway.util.FilterFailUtil;
import com.gomeplus.oversea.bs.service.gateway.util.LogUtil;
import com.gomeplus.oversea.bs.service.gateway.util.ParamsUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;


@Slf4j
public class AccessFilter extends ZuulFilter {


    @Autowired
    RabbitTemplate rabbitTemplate;


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
        return true;
    }

    @Override
    public Object run() {
        long startTime=System.currentTimeMillis();
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        //traceId 设置,
        setTraceId(ctx);

        //获取头信息
        JSONObject headerParamJson=ParamsUtil.getHeaderForJsonFromRequest(request);
        //获取入参
        String queryParamJson ="";
        try {
            queryParamJson= ParamsUtil.getQueryFromRequest(request);
        }catch(Exception e){
            FilterFailUtil.responseValidateFail(ctx, MessageContant.paramInvalide);
            return null;
        }
        //获取body
        String bodyParamString = ParamsUtil.getBodyFromRequest(request);

        //过滤日志中的password
        bodyParamString=coverPasswardInLogs(request,bodyParamString);
        log.info(String.format("request method:%s, request url: %s,request headerParams:%s,request queryParams:%s,request bodyParams:%s",
                request.getMethod(),
                request.getRequestURL().toString(),
                headerParamJson.toJSONString(),
                queryParamJson,
                bodyParamString
        ));
        long validateTime=System.currentTimeMillis()-startTime;
        log.info("accessFilerSpendTime:{}",validateTime);
        return null;
    }

    private void setTraceId(RequestContext ctx){
        String traceId=ctx.getRequest().getHeader("X-Gomeplus-Trace-Id");
        if(traceId==null || "".equals(traceId)){
            traceId = MDC.get("X-B3-TraceId");
        }else{
            String xB3TraceId=MDC.get("X-B3-TraceId");
            log.info("X-Gomeplus-Trace-Id binding X-B3-TraceId, X-Gomeplus-Trace-Id:{},X-B3-TraceId:{}",traceId,xB3TraceId);
            ctx.addZuulResponseHeader("X-B3-TraceId",xB3TraceId);
        }
        ctx.addZuulResponseHeader("X-Gomeplus-Trace-Id",traceId);
    }
    private String  coverPasswardInLogs(HttpServletRequest request ,String bodyParamString){
        String url = request.getRequestURI();
        String method = request.getMethod().toLowerCase();
        for(String filterUrl: LogUtil.sensitiveUrls){
            String[] split = filterUrl.split("#");
            if(method.equals(split[0]) && url.equals(split[1])){
                String[] words = split[2].split(",");
                try {
                JsonNode jsonNode = JsonLoader.fromString(bodyParamString);
                    for(String word:words){
                        String password=jsonNode.get(word)==null?null:jsonNode.get(word).asText();
                        if(password!=null&&!"".equals(password)){
                            bodyParamString=bodyParamString.replaceAll(password,"********");
                        }
                    }
                }catch(Exception e){
                    log.info("coverPasswordInLogs failed.");
                }

            }
        }
        return bodyParamString;
    }
}
