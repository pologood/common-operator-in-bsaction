package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 415异常 ，Unsupported Media Type：对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式，因此请求被拒绝。
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C415Exception extends BaseCodeException {

	private static final long serialVersionUID = 7179579352685718658L;

	/**
	 * @param message message 异常消息：对应json返回数据中的message字段,面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 */
	public C415Exception(String message,String debugMessage){
		super(message,debugMessage);
	}
	/**
	 * @param message 异常消息：对应json返回数据中的message字段，面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 * @param error  error属性：即返回数据中的error节点，慎用
	 */
	public C415Exception(String message,String debugMessage,Object error){
		super(message,debugMessage,error);
	}
	public C415Exception() {
		super();
	}
	
	public C415Exception(BaseExceptionMessage message) {
		super(message);
	}
	
	public C415Exception(BaseExceptionMessage message, Throwable cause) {
        super(message, cause);
    }
	
	public C415Exception(Throwable cause) {
        super(cause);
    }

}
