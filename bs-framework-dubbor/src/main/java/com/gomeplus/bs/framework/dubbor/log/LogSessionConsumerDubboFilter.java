package com.gomeplus.bs.framework.dubbor.log;


import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.gomeplus.bs.framework.dubbor.constants.PublicParamsConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;


/**
 * @author zhaozhou
 * @Description 对所有dubbo的consumer都适用
 * @date 2016/8/12 10:54
 */
@Slf4j
@Activate(
        group = {"consumer"},
        order = -1000
)
public class LogSessionConsumerDubboFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String logSessionId = MDC.get(PublicParamsConstant.LOG_SESSION_ID);
        if(logSessionId != null && logSessionId.length()>0){
            RpcContext.getContext().setAttachment(PublicParamsConstant.LOG_SESSION_ID,logSessionId);
        }
        return invoker.invoke(invocation);
    }
}
