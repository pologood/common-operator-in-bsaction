package com.gomeplus.bs.framework.dubbor.log;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.gomeplus.bs.framework.dubbor.constants.PublicParamsConstant;
import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/22
 */
@Slf4j
@Activate(
        group = {"provider"},
        order = -10000
)
public class LogSessionProviderDubboFilter implements Filter{
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        String logSessionId = RpcContext.getContext().getAttachment(PublicParamsConstant.LOG_SESSION_ID);
        if(logSessionId != null && logSessionId.trim().length() > 0) {
            MDC.put(PublicParamsConstant.LOG_SESSION_ID, logSessionId);
        }
        Result result = invoker.invoke(invocation);
        if (result.hasException()){
            Throwable exception = result.getException();
            if (exception instanceof RESTfull4xxBaseException){
                log.warn("framework-dubbor-exception:Dubbo 4xx exception ", exception);
            }else {
                log.error("framework-dubbor-exception:Dubbo exception ", exception);
            }
        }
        MDC.remove(PublicParamsConstant.LOG_SESSION_ID);
        return result;
    }
}