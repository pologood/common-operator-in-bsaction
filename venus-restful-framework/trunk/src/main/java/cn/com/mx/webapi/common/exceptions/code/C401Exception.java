package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 401异常 ，Unauthorized：当前请求需要用户验证
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
@SuppressWarnings("SerializableHasSerializationMethods")
public class C401Exception extends BaseCodeException {

	private static final long serialVersionUID = 7249808434799610256L;

	public C401Exception() {
		super();
	}

	/**
	 * @param message message 异常消息：对应json返回数据中的message字段,面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 */
	public C401Exception(String message,String debugMessage){
		super(message,debugMessage);
	}
	/**
	 * @param message 异常消息：对应json返回数据中的message字段，面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 * @param error  error属性：即返回数据中的error节点，慎用
	 */
	public C401Exception(String message,String debugMessage,Object error){
		super(message,debugMessage,error);
	}
	public C401Exception(BaseExceptionMessage message) {
		super(message);
	}
	
	public C401Exception(BaseExceptionMessage message, Throwable cause) {
        super(message, cause);
    }
	
	public C401Exception(Throwable cause) {
        super(cause);
    }

}
