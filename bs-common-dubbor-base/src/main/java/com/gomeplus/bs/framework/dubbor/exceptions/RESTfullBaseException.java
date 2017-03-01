package com.gomeplus.bs.framework.dubbor.exceptions;

import java.io.Serializable;

/**
 * <p> </p>
 * @author zhaozhou
 * Created on 2016/10/19.
 */
public class RESTfullBaseException extends RuntimeException implements Serializable{
    private static final long serialVersionUID = 90291629755833147L;
    private Object error;
    private String debugMessage;
    public RESTfullBaseException(String message){
        super(message);
    }
    public RESTfullBaseException debugMessage(String debugMessage){
        this.debugMessage = debugMessage;
        return this;
    }
    public RESTfullBaseException error(Object error){
        this.error = error;
        return this;
    }
    public Object getError() {
        return error;
    }
    public String getDebugMessage() {
        return debugMessage;
    }

}
