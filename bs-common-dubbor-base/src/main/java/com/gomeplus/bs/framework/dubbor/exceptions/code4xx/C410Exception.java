package com.gomeplus.bs.framework.dubbor.exceptions.code4xx;


import com.gomeplus.bs.framework.dubbor.exceptions.RESTfull4xxBaseException;

import java.io.Serializable;

/**
 * @Description 410异常 ，资源已被逻辑删除
 * @author mojianli
 * @date 2016年5月12日 上午11:02:03
 */
public class C410Exception extends RESTfull4xxBaseException implements Serializable {


    private static final long serialVersionUID = 6479372425706561796L;

    public C410Exception(String message) {
        super(message);
    }
}
