package com.gomeplus.bs.framework.dubbor.exceptions;

import java.io.Serializable;

/**
 * <p>自定义异常</p>
 * Created by zhaozhou on 16/10/15.
 * @author zhaozhou
 */
public class BizExcepition extends RuntimeException implements Serializable{
    private static final long serialVersionUID = 9205819403452627565L;
    private Object error;
    private String debugMessage;
    private int httpStatus;

    /**
     * @param httpStatus http态码
     * @param message http 信息
     */
    public BizExcepition(int httpStatus,String message){
        super(message);
        this.httpStatus = httpStatus;
    }
    public BizExcepition debugMessage(String debugMessage){
        this.debugMessage = debugMessage;
        return this;
    }

    /**
     * error节点：谨慎使用
     * @param error object
     * @return BizExcepition
     */
    public BizExcepition error(Object error){
        this.error = error;
        return this;
    }

    public Object getError() {
        return error;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
