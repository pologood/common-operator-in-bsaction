package com.gomeplus.oversea.bs.common.exception.code4xx;



import com.gomeplus.oversea.bs.common.exception.RESTfull4xxBaseException;
import com.gomeplus.oversea.bs.common.exception.entity.Error;
import com.gomeplus.oversea.bs.common.exception.util.ErrorUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class C406Exception extends RESTfull4xxBaseException implements Serializable {

    private static final long serialVersionUID = -6140144363882807393L;

    public C406Exception(String message) {
        super(message);
    }
    public C406Exception(String message,Error error){
        super(ErrorUtil.appendError(message,error));
    }
}