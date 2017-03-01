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
@RequestMapping("/test/test")
public class BaseTestResource {

    @GetMapping
    public Object doGet(){
        Map map = new HashMap<>();
        map.put("username", "henry");
        return map;
    }
}
