package com.gomeplus.bs.framework.dubbor.service;

import com.gomeplus.bs.framework.dubbor.entity.User;
import com.gomeplus.bs.framework.dubbor.interfaces.UserResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhaozhou on 16/12/29.
 */

@RestController("userResource")
@RequestMapping("/test/user")
public class UserResourceImpl implements UserResource {

    @GetMapping
    @Override
    public User doGet(Long id) {
        User user = new User();
        user.setUsername("zhaozhou");
        user.setPassword("123");
        return user;
    }
}
