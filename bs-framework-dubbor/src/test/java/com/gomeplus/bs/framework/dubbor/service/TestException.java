package com.gomeplus.bs.framework.dubbor.service;

import com.gomeplus.bs.framework.dubbor.annotations.PublicParam;
import com.gomeplus.bs.framework.dubbor.constants.RESTfulExceptionConstant;
import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/13
 */
@RestController
@RequestMapping("/test/testException")
public class TestException {

    @GetMapping
    public Object doGet(@PublicParam(name = "userId") Long userId){
        Map map = new HashMap<>();
        map.put("userId", userId);
        return map;
    }

    @PutMapping
    public void doPut(Long id){
        if(id == null){
            throw new C422Exception(RESTfulExceptionConstant.CHECK_REQUEST_PARAM_FAILED_MSG);
        }
    }
}
