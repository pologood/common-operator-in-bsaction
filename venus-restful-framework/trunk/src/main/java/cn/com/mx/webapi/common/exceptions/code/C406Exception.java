package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 406异常 ，Forbidden：服务器已经理解请求，但是拒绝执行它
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C406Exception extends BaseCodeException {

	private static final long serialVersionUID = -5893058147963724662L;

	/**
	 * @param message message 异常消息：对应json返回数据中的message字段,面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 */
	public C406Exception(String message,String debugMessage){
		super(message,debugMessage);
	}
	/**
	 * @param message 异常消息：对应json返回数据中的message字段，面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 * @param error  error属性：即返回数据中的error节点，慎用
	 */
	public C406Exception(String message,String debugMessage,Object error){
		super(message,debugMessage,error);
	}
	public C406Exception() {
		super();
	}
	
	public C406Exception(BaseExceptionMessage message) {
		super(message);
	}
	
	public C406Exception(BaseExceptionMessage message, Throwable cause) {
        super(message, cause);
    }
	
	public C406Exception(Throwable cause) {
        super(cause);
    }

}
