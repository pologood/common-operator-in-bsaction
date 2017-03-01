package com.gomeplus.bs.framework.dubbor.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/13
 */
@RestController
@RequestMapping("/test/testUnEncapsulate")
public class UnEncapsulate {

    @GetMapping
    @com.gomeplus.bs.framework.dubbor.annotations.UnEncapsulate
    public Object doGet(){
        Map map = new HashMap<>();
        map.put("username", "henry");
        return map;
    }
}
