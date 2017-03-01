package com.gomeplus.bs.framework.dubbor.service;

import com.gomeplus.bs.framework.dubbor.annotations.PublicParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/30
 */
@Slf4j
@RestController
@RequestMapping("/test/publicParams")
public class PublicParamsResources {

    @RequestMapping("/long")
    @GetMapping
    public void doGet1(@PublicParam(name = "userId") Long userId) {
        log.debug("userId {} , type {}", userId, userId.getClass());
    }

    @RequestMapping("/int")
    @GetMapping
    public void doGet2(@PublicParam(name = "userId") Integer userId) {
        log.debug("userId {} , type {}", userId, userId.getClass());
    }

    @RequestMapping("/string")
    @GetMapping
    public void doGet3(@PublicParam(name = "userId") String userId) {
        log.debug("userId {} , type {}", userId, userId.getClass());
    }

    @RequestMapping("/require")
    @GetMapping
    public void doGet4(@PublicParam(name = "userId", required = false) String userId) {
        log.debug("userId {} , type {}", userId);
    }

}
