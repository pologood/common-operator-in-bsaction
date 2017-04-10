package com.gomeplus.oversea.bs.service.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.gomeplus.oversea.bs.common.exception.entity.Error;
import com.gomeplus.oversea.bs.service.gateway.model.ResponseModel;
import com.gomeplus.oversea.bs.service.gateway.model.UserTokenModel;
import com.gomeplus.oversea.bs.service.gateway.service.BsServiceUser;
import com.gomeplus.oversea.bs.service.gateway.util.ParamsUtil;
import com.gomeplus.oversea.bs.service.gateway.util.SwaggerUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;


@Slf4j
public class TokenValidateFilter extends ZuulFilter {

    @Autowired
    BsServiceUser bsServiceUser;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
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
        JSONObject headerParamJson=ParamsUtil.getHeaderForJsonFromRequest(request);
        //token校验
        if(isValidateToken(request)) {
           tokenValidation(headerParamJson,ctx);
        }
        long validateTime=System.currentTimeMillis()-startTime;
        log.info("tonkenValidateFilterSpendTime:{}",validateTime);
        return null;
    }
    private void tokenValidation(JSONObject headerParamJson,RequestContext ctx){
        Object userIdObject= headerParamJson.get("x-gomeplus-user-id");
        Object tokenObject=headerParamJson.get("x-gomeplus-login-token");
        Object device=headerParamJson.get("x-gomeplus-device");
        if(userIdObject!=null && tokenObject!=null && device!=null){
          UserTokenModel validateResult = bsServiceUser.tokenValidate(userIdObject.toString(),tokenObject.toString(),device.toString());
          if (validateResult.getStatusCode()==200){
              //在设置token校验成功标志，tokenSuccessMQFilter会根据此标志发送MQ给日活统计
              ctx.getRequest().setAttribute("tokenValidateSuccess",true);
          }else{
              responseValidateFail(ctx,validateResult);
          }
        }
        //else{
            //如果公参中缺少验证token的任一项，认为不需要进行token校验
        //}
    }
    private void responseValidateFail(RequestContext ctx,UserTokenModel validateResult){
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(validateResult.getStatusCode());

        ResponseModel<Object> responseModel = new ResponseModel<>();
            responseModel.setMessage("please login in.");
            responseModel.setData(new Object());
                Error error=new Error();
                error.setMessage(validateResult.getErrorMessage());
                error.setCode(validateResult.getErrorCode());
            responseModel.setError(error);

        ctx.getRequest().setAttribute("validationFail",responseModel);
    }

    private boolean isValidateToken(HttpServletRequest request){
        try {
            JsonNode swagger = SwaggerUtil.getSwagger();
            JsonNode paths = swagger.get("paths");
            JsonNode uriNode = paths.get(request.getRequestURI());

            String method = request.getMethod().toLowerCase();
            JsonNode methodNode = uriNode.get(method);
            JsonNode paramsNode = methodNode.get("parameters");
            Iterator<JsonNode> paramsNodeIt = paramsNode.elements();
            boolean hasToken = false;
            boolean hasUserId = false;
            boolean hasDevice = false;
            while (paramsNodeIt.hasNext()) {
                JsonNode paramNode = paramsNodeIt.next();
                if (paramNode.get("$ref") != null) {
                    JsonNode ref = paramNode.get("$ref");
                    JsonNode param = SwaggerUtil.getPubParameters(ref.asText());
                    String paramType = param.get("in")==null?"":param.get("in").asText();
                    if ("header".equals(paramType)) {
                        JSONObject typeNode = new JSONObject();
                        String paramName = param.get("name").asText();
                        if ("X-Gomeplus-Login-Token".equals(paramName)) {
                            hasToken = true;
                        }
                        if ("X-Gomeplus-User-Id".equals(paramName)) {
                            hasUserId = true;
                        }
                        if ("X-Gomeplus-Device".equals(paramName)) {
                            hasDevice = true;
                        }
                    }
                }
            }
            return hasToken && hasUserId && hasDevice;

        }catch(Exception e){
            log.info("get isNeedValidate fail.",e);
            return false;
        }

    }

}
