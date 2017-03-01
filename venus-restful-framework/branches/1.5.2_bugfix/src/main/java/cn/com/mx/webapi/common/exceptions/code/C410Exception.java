package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 410异常 ，资源已被逻辑删除
 * @author mojianli
 * @date 2016年5月12日 上午11:02:03
 */
public class C410Exception extends BaseCodeException {

    private static final long serialVersionUID = -6755210519054697499L;

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
