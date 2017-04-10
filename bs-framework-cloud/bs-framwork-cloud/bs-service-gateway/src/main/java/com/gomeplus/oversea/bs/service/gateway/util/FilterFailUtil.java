package com.gomeplus.oversea.bs.service.gateway.util;

import com.gomeplus.oversea.bs.common.exception.entity.Error;
import com.gomeplus.oversea.bs.service.gateway.model.ResponseModel;
import com.netflix.zuul.context.RequestContext;

/**
 * Created by shangshengfang on 2017/2/28.
 */
public class FilterFailUtil {
    public static void responseValidateFail(RequestContext ctx, String message){
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(422);
        ResponseModel<Object> responseModel = new ResponseModel<>();
        responseModel.setMessage(message);
        responseModel.setData(new Object());
        ctx.getRequest().setAttribute("validationFail",responseModel);
    }
    public static void responseInnerApiVisit(RequestContext ctx, String message){
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(403);
        ResponseModel<Object> responseModel = new ResponseModel<>();
        responseModel.setMessage(message);
        responseModel.setData(new Object());
        Error err=new Error();
        err.setMessage("api to accessed is inner");
        err.setCode("SD-10200");
        responseModel.setError(err);
        ctx.getRequest().setAttribute("validationFail",responseModel);
    }
    public static void responseServiceDegradation(RequestContext ctx,String message){
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(410);
        ResponseModel<Object> responseModel = new ResponseModel<>();
        Error error=new Error();
        error.setCode("SD-10100");
        error.setMessage(message);
        responseModel.setError(error);
        responseModel.setMessage(message);
        responseModel.setData(new Object());
        ctx.getRequest().setAttribute("validationFail",responseModel);

    }
}
