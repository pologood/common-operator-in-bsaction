package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 404异常 ，Not Found：请求失败，请求所希望得到的资源未被在服务器上发现
 * @author wanggang-ds6
 * @date 2016年1月21日 下午1:51:17
 */
public class C404Exception extends BaseCodeException {

	private static final long serialVersionUID = 2751205722411589544L;

	public C404Exception() {
		super();
	}

	/**
	 * @param message message 异常消息：对应json返回数据中的message字段,面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 */
	public C404Exception(String message,String debugMessage){
		super(message,debugMessage);
	}
	/**
	 * @param message 异常消息：对应json返回数据中的message字段，面向用户
	 * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
	 * @param error  error属性：即返回数据中的error节点，慎用
	 */
	public C404Exception(String message,String debugMessage,Object error){
		super(message,debugMessage,error);
	}
	public C404Exception(BaseExceptionMessage message) {
		super(message);
	}
	
	public C404Exception(BaseExceptionMessage message, Throwable cause) {
        super(message, cause);
    }
	
	public C404Exception(Throwable cause) {
        super(cause);
    }

}
