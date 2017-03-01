package com.gomeplus.oversea.bs.service.gateway.config;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanInjector;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhaozhou
 * @Description
 * @date 2017/2/9
 */
public class CustomHttpServletResponseSpanInjector implements SpanInjector<HttpServletResponse> {

    @Override
    public void inject(Span span, HttpServletResponse response) {
        response.addHeader("correlationId", Span.idToHex(span.getTraceId()));
        response.addHeader("mySpanId", Span.idToHex(span.getSpanId()));
    }
}