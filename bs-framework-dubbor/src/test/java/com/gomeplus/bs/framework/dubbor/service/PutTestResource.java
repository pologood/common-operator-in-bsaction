package com.gomeplus.bs.framework.dubbor.service;

import com.gomeplus.bs.framework.dubbor.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 2016/12/29
 * @author  zhaozhou
 * @since   1.0
 */
@RestController
@RequestMapping("/test/put")
@Slf4j
public class PutTestResource {

    @PutMapping
    public User doPut(@RequestBody User user){
        return user;
    }
}
