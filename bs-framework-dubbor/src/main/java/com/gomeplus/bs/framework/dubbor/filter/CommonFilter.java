package com.gomeplus.bs.framework.dubbor.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gomeplus.bs.framework.dubbor.modle.CommonLogModel;
import com.gomeplus.bs.framework.dubbor.modle.PublicParams;
import com.gomeplus.bs.framework.dubbor.wrapper.HttpServletRequestCopier;
import com.gomeplus.bs.framework.dubbor.wrapper.HttpServletResponseCopier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * common filter {handle public params, traceId, printCommonLog}
 * 2016/12/29
 *
 * @author zhaozhou
 * @since 1.0
 */
@Slf4j
public class CommonFilter extends CommonFilterABS implements Filter {
    private static final String EMPTY_JESON = "{\"message\":\"\",\"data\":{}}";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        CommonLogModel logModel = new CommonLogModel();
        final PublicParams publicParams = new PublicParams();
        logModel.setStartTime(System.currentTimeMillis());

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getRequestURI().matches(".*.(html|js|css|jpg|ico|png)")) {
            chain.doFilter(request, response);
            return;
        }

        addTraceId(request, response);

        if (response.getCharacterEncoding() == null) {
            response.setCharacterEncoding("UTF-8");
        }
        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier(response);

        setpublicParams(request, publicParams);
        checkAccept(publicParams);
        String method = request.getMethod();
        String requestBody;
        if ("PUT".equals(method) || "POST".equals(method)) {
            HttpServletRequestCopier wrappedRequest = new HttpServletRequestCopier(request);
            try {
                requestBody = IOUtils.toString(wrappedRequest.getReader());
                logModel.setRequestBody(requestBody);
                wrappedRequest.resetInputStream();
            } catch (Exception e) {
                log.info("request body is null");
            }
            chain.doFilter(wrappedRequest, responseCopier);
        } else {
            chain.doFilter(request, responseCopier);
        }

        String responseBody = getResponseBody(response, responseCopier);

        logModel.setResponseBody(responseBody);

        handleReturnNull(response, responseBody);
        setResponseHeader(response);
        printCommonLog(request, response, publicParams, logModel);

    }

    /**
     * filter结束后删除traceId
     */
    @Override
    public void destroy() {
        removeTraceId();
    }

    /**
     * @param response       response
     * @param responseCopier responseCopier
     * @return responseBody响应实体
     * @throws IOException IOException
     */
    private String getResponseBody(HttpServletResponse response, HttpServletResponseCopier responseCopier) throws IOException {
        byte[] bytes = responseCopier.getByteArray();
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String responseBody = new String(bytes, response.getCharacterEncoding());
        response.getOutputStream().write(bytes);
        return responseBody;
    }

    /**
     * 设置返回Header
     *
     * @param response HttpServletResponse
     */
    private void setResponseHeader(HttpServletResponse response) {
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setHeader("Accept", "application/json, application/javascript");
    }

    /**
     * 处理return null时响应实体为""的情况
     *
     * @param response     HttpServletResponse
     * @param responseBody responseBody
     * @throws IOException IOException
     */
    private void handleReturnNull(HttpServletResponse response, String responseBody) throws IOException {
        if (responseBody == null || "".equals(responseBody)) {
            if (response.isCommitted()) {
                return;
            }
            PrintWriter writer = response.getWriter();
            writer.write(EMPTY_JESON);
            writer.flush();
        }
    }

    /**
     * 每一个请求的日志
     *
     * @param request      HttpServletRequest
     * @param response     HttpServletResponse
     * @param publicParams 公参
     * @param logModel     日志实体
     */
    private void printCommonLog(HttpServletRequest request, HttpServletResponse response, PublicParams publicParams, CommonLogModel logModel) {
        logModel.setPublicParams(publicParams);
        logModel.setRequestParams(request.getParameterMap());
        logModel.setRequestUrl(request.getRequestURI());
        logModel.setResponseCode(response.getStatus());
        logModel.setEndTime(System.currentTimeMillis());
        logModel.setCostTime(logModel.getEndTime() - logModel.getStartTime());
        log.debug(JSON.toJSONString(logModel, SerializerFeature.PrettyFormat));
    }


}
