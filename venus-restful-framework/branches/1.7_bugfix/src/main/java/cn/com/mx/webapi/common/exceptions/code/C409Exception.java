package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 409异常，Conflict 通用冲突
 * @author lichengzhen
 * @date 2016年5月13日 下午2:21:54
 */
public class C409Exception extends BaseCodeException {

	private static final long serialVersionUID = -5864063498619932899L;

	/**
	 * @param message message 异常消息：对应json返回数据中的message字段,面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 */
	public C409Exception(String message,String debugMessage){
		super(message,debugMessage);
	}
	/**
	 * @param message 异常消息：对应json返回数据中的message字段，面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 * @param error  error属性：即返回数据中的error节点，慎用
	 */
	public C409Exception(String message,String debugMessage,Object error){
		super(message,debugMessage,error);
	}
	public C409Exception() {
		super();
	}
	public C409Exception(BaseExceptionMessage message) {
		super(message);
	}

	public C409Exception(BaseExceptionMessage message, Throwable cause) {
		super(message, cause);
	}

	public C409Exception(Throwable cause) {
		super(cause);
	}

}
