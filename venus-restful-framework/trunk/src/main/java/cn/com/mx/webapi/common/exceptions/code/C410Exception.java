package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 410异常 ，资源已被逻辑删除
 * @author mojianli
 * @date 2016年5月12日 上午11:02:03
 */
public class C410Exception extends BaseCodeException {

    private static final long serialVersionUID = -6755210519054697499L;

    /**
     * @param message message 异常消息：对应json返回数据中的message字段,面向用户
     * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
     */
    public C410Exception(String message,String debugMessage){
        super(message,debugMessage);
    }
    /**
     * @param message 异常消息：对应json返回数据中的message字段，面向用户
     * @param debugMessage debugmsg，可以传null，此时debug信息默认取message
     * @param error  error属性：即返回数据中的error节点，慎用
     */
    public C410Exception(String message,String debugMessage,Object error){
        super(message,debugMessage,error);
    }
    public C410Exception() {
        super();
    }

    public C410Exception(BaseExceptionMessage message) {
        super(message);
    }

    public C410Exception(BaseExceptionMessage message, Throwable cause) {
        super(message, cause);
    }

    public C410Exception(Throwable cause) {
        super(cause);
    }
}
