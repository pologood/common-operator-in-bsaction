package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 403异常 ，Not Acceptable：请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体。
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C403Exception extends BaseCodeException {

	private static final long serialVersionUID = -1674922965203577452L;


	/**
	 * @param message message 异常消息：对应json返回数据中的message字段,面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 */
	public C403Exception(String message,String debugMessage){
		super(message,debugMessage);
	}
	/**
	 * @param message 异常消息：对应json返回数据中的message字段，面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 * @param error  error属性：即返回数据中的error节点，慎用
	 */
	public C403Exception(String message,String debugMessage,Object error){
		super(message,debugMessage,error);
	}
	public C403Exception() {
		super();
	}
	
	public C403Exception(BaseExceptionMessage message) {
		super(message);
	}
	
	public C403Exception(BaseExceptionMessage message, Throwable cause) {
        super(message, cause);
    }
	
	public C403Exception(Throwable cause) {
        super(cause);
    }

}
