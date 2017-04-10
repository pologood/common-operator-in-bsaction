package com.gomeplus.oversea.bs.service.gateway.filter;

import com.gomeplus.oversea.bs.service.gateway.event.EventDispatcher;
import com.gomeplus.oversea.bs.service.gateway.event.TokenSuccessMQEvent;
import com.gomeplus.oversea.bs.service.gateway.model.MQModel;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class TokenSuccessMQFilter extends ZuulFilter {


    @Autowired
    EventDispatcher eventDispatcher;

    @Value("${online_notify_mq_switch}")
    String onlineNotifyMqSwitch;
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 11;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getRequest().getAttribute("validationFail")==null;
    }

    @Override
    public Object run() {
        long startTime=System.currentTimeMillis();
        if("true".equals(onlineNotifyMqSwitch)) {
           RequestContext ctx = RequestContext.getCurrentContext();
           HttpServletRequest request = ctx.getRequest();
           boolean tokenValidateSuccess = request.getAttribute("tokenValidateSuccess") == null ? false : true;
           if (tokenValidateSuccess) {
               TokenSuccessMQEvent mqEvent = new TokenSuccessMQEvent();
               MQModel mqModel = new MQModel();
               mqModel.setAction("userOnlineNotify");

               Map m = new HashMap();
               m.put("region", request.getHeader("X-Gomeplus-Region"));
               mqModel.setPubParam(m);

               String userId = request.getHeader("X-Gomeplus-User-Id");
               Map data = new HashMap();
               data.put("userId", userId);
               mqModel.setData(data);
               mqModel.setExt(new HashMap());
               mqEvent.setMqModel(mqModel);
               eventDispatcher.publish(mqEvent);
           }
        }else{
            log.info("onlineNotifyMqSwitch not true,no mq to onlineNotify.");
        }
        long validateTime = System.currentTimeMillis() - startTime;

        log.info("TokenSuccessFilerSpendTime:{}", validateTime);
        return null;

    }


}
