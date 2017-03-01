package com.gomeplus.bs.framework.dubbor.service;

import com.gomeplus.bs.framework.dubbor.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2016/12/29
 * @author  zhaozhou
 * @since   1.0
 */
@RestController
@RequestMapping("/test/post")
@Slf4j
public class PostTestResource {

    @PostMapping
    public User doPost(@RequestBody User user){
        return user;
    }
}
