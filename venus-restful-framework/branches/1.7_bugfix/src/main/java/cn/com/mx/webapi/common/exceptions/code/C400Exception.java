package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 400异常 :Bad Request 请求格式错误.如Content-Type错误
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C400Exception extends BaseCodeException {

	private static final long serialVersionUID = 6763439507616251988L;

	public C400Exception() {
		super();
	}

	/**
	 * @param message message 异常消息：对应json返回数据中的message字段,面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 */
	public C400Exception(String message,String debugMessage){
		super(message,debugMessage);
	}
	/**
	 * @param message 异常消息：对应json返回数据中的message字段，面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 * @param error  error属性：即返回数据中的error节点，慎用
	 */
	public C400Exception(String message,String debugMessage,Object error){
		super(message,debugMessage,error);
	}
	
	public C400Exception(BaseExceptionMessage message) {
		super(message);
	}
	
	public C400Exception(BaseExceptionMessage message, Throwable cause) {
        super(message, cause);
    }
	
	public C400Exception(Throwable cause) {
        super(cause);
    }

}
