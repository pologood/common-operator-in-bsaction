package com.gomeplus.bs.image_web.exceptions;

import com.google.common.base.Strings;



/**
 * @Description 异常基类 
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:49:20
 */
public abstract class BaseCodeException extends RuntimeException{

	private static final long serialVersionUID = 9205819405627627565L;
	private Object error;
	private String debugMessage;
	
	public BaseCodeException() {
		super();
	}

	/**
	 *
	 * @param message message 异常消息：对应json返回数据中的message字段
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
     */
	public BaseCodeException(String message,String debugMessage){
		super(message);
		this.debugMessage = debugMessage;
	}
	/**
	 *
	 * @param message 异常消息：对应json返回数据中的message字段
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 * @param error  error属性：即返回数据中的error节点，慎用
     */
	public BaseCodeException(String message,String debugMessage,Object error){
		super(message);
		this.debugMessage = debugMessage;
		this.error = error;
	}

	public BaseCodeException(BaseExceptionMessage message) {
		super(message.toString());
	}
	
	public BaseCodeException(BaseExceptionMessage message, Throwable cause) {
        super(message.toString(), cause);
    }
	
	public BaseCodeException(Throwable cause) {
        super(cause);
    }
	
	/**
	 * @Description 获取状态码 
	 * @date 2016年1月21日 下午1:51:53
	 * @return 状态码 
	 */
	public int getCode() {
		return Integer.parseInt(this.getClass().getSimpleName().substring(1, 4));
	}

	public String getMessage(){
        if(Strings.isNullOrEmpty(super.getMessage())){
            return "";
        }
        return super.getMessage();
    }
	public void setError(Object error){
		this.error = error;
	}
	public Object getError(){
		return this.error;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}
}
