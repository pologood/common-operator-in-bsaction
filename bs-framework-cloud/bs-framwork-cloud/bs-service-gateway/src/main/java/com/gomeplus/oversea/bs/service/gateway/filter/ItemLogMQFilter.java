package com.gomeplus.oversea.bs.service.gateway.filter;

import com.gomeplus.oversea.bs.service.gateway.common.MessageContant;
import com.gomeplus.oversea.bs.service.gateway.event.EventDispatcher;
import com.gomeplus.oversea.bs.service.gateway.event.ItemLogMQEvent;
import com.gomeplus.oversea.bs.service.gateway.model.MQModel;
import com.gomeplus.oversea.bs.service.gateway.model.RequestAllParams;
import com.gomeplus.oversea.bs.service.gateway.util.FilterFailUtil;
import com.gomeplus.oversea.bs.service.gateway.util.ParamsUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;


@Slf4j
public class ItemLogMQFilter extends ZuulFilter {


    @Autowired
    EventDispatcher eventDispatcher;

    @Value("${item_log_mq_switch}")
    String itemLogMqSwitch;

    @Value("${itemLogMqUrls}")
    String itemLogMqUrls;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 9;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getRequest().getAttribute("validationFail")==null;
    }

    @Override
    public Object run() {
        long startTime=System.currentTimeMillis();
        if("true".equals(itemLogMqSwitch)) {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            //判断当前url是否需要发送mq
            boolean isNeedSendMq =false;
            String currentUrl=request.getRequestURI();
            String[] urls=itemLogMqUrls.split(";");
            for(String url:urls){
                if(currentUrl.equals(url)){
                    isNeedSendMq=true;
                    break;
                };
            }
            if(isNeedSendMq) {
                //获取头信息
                JSONObject headerParamJson = ParamsUtil.getHeaderForJsonFromRequest(request);

                String queryParamJson="";
                try {
                    //获取入参
                    queryParamJson = ParamsUtil.getQueryFromRequest(request);
                }catch(Exception e){
                    FilterFailUtil.responseValidateFail(ctx, MessageContant.paramInvalide);
                    return null;
                }
                //获取body
                String bodyParamString = ParamsUtil.getBodyFromRequest(request);
                //发送全部参数到rabbit
                RequestAllParams allParams = new RequestAllParams();
                allParams.setMethod(request.getMethod());
                allParams.setUrl(request.getRequestURI());
                allParams.setHeader(headerParamJson.toJSONString());
                allParams.setQuery(queryParamJson);
                allParams.setBody(bodyParamString);

                ItemLogMQEvent itemLogMQEvent = new ItemLogMQEvent();

                MQModel mqModel = new MQModel();
                mqModel.setSendSource("gateway");
                mqModel.setAction("itemLog");
                mqModel.setData(allParams);

                itemLogMQEvent.setMqModel(mqModel);
                eventDispatcher.publish(itemLogMQEvent);
            }
        }else{
            log.info("itemLogMqSwitch not true,no mq to itemLog.");
        }
        long validateTime=System.currentTimeMillis()-startTime;
        log.info("ItemLogMQFilerSpendTime:{}",validateTime);
        return null;
    }


}
