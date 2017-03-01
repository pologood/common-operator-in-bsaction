package cn.com.mx.webapi.common.exceptions.code;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;

/**
 * @Description 409异常，Conflict 通用冲突
 * @author lichengzhen
 * @date 2016年5月13日 下午2:21:54
 */
public class C409Exception extends BaseCodeException {

	private static final long serialVersionUID = -5864063498619932899L;

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
