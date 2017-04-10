package com.gomeplus.oversea.bs.service.gateway.filter;

import com.gomeplus.oversea.bs.service.gateway.util.FilterFailUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;


@Slf4j
public class ServiceDegradationFilter extends ZuulFilter {


    @Value("${serviceDegradation}")
    private String serviceDegradation;

    @Value("${serviceDegradationDesc}")
    String serviceDegradationDesc;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 3;
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
        String[] degradations = serviceDegradation.split(";");
        String appVersion= request.getHeader("X-Gomeplus-App-Version");
        if (appVersion !=null) {
            for (String str : degradations) {
                String[] degradation = str.split(":");
                String uri = degradation[0];
                String version = degradation[1];
                if (request.getRequestURI().equals(uri) && request.getHeader("X-Gomeplus-App-Version").compareTo(version) < 0) {
                    FilterFailUtil.responseServiceDegradation(ctx, serviceDegradationDesc);
                    break;
                }
            }
        }
        long spendTime=System.currentTimeMillis()-startTime;
        log.info("ServiceDegradationFilerSpendTime:{}",spendTime);
        return null;
    }


}
