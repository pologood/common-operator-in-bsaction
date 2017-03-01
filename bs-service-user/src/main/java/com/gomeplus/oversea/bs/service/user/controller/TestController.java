package com.gomeplus.oversea.bs.service.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangchangye on 2017/2/16.
 *
 * @Author wangchangye
 */
@RestController
@RequestMapping("/user/test")
public class TestController {

    @RequestMapping(method = RequestMethod.GET)
    public Object doGet(@RequestParam String id) {

            Map map = new HashMap<String,String>();
            map.put("a","a");
            map.put("b","b");
            return map;
    }
}
