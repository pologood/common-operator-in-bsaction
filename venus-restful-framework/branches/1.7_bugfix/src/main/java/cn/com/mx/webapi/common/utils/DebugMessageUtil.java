package cn.com.mx.webapi.common.utils;

import cn.com.mx.webapi.common.exceptions.code.BaseCodeException;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaozhou
 * @Description 异常信息处理工具类
 * @date 2016/8/8 13:19
 */
public class DebugMessageUtil {

    private static String StackTraceElementFormate(StackTraceElement[] elements){
        StringBuffer sb = new StringBuffer();
        if (elements == null || elements.length==0){
            return null;
        }
        for(StackTraceElement element :elements ){
            sb.append(element);
            sb.append(",\n ");

        }
        return sb.toString();
    }

    public static String getDebugMessage(Exception e){
        //默认为message
        StringBuffer msg = new StringBuffer();
        String debugMsg = e.getMessage();
        if (e instanceof BaseCodeException) {
            BaseCodeException baseCodeException = (BaseCodeException) e;
            if(!Strings.isNullOrEmpty(baseCodeException.getDebugMessage())){
                debugMsg = debugMsg+baseCodeException.getDebugMessage();
            }
        }
        String stack = StackTraceElementFormate(e.getStackTrace());
        msg.append(debugMsg).append(",\n ");
        msg.append(" [").append(e.getClass().getName()).append(",\n ");
        if(null != e.getCause()){
            msg.append(e.getCause().toString()).append(",\n ");
        }
        msg.append(stack).append("]");
        return msg.toString();
    }
}
