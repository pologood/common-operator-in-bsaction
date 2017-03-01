package com.gomeplus.oversea.bs.service.userid.generator.web;

import com.gomeplus.oversea.bs.service.userid.generator.dao.UserIdDao;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Data
@AllArgsConstructor
class UserId {
    private long id;
    private long timestamp;
}

@RestController
@Slf4j
@RequestMapping("/userId/next")
public class UserIdController {

    @Autowired
    private UserIdDao userDao;

    @ApiOperation(value = "get a new user id", notes = "")
    @RequestMapping(method = RequestMethod.GET)
    public UserId doGet() {
        return new UserId(userDao.nextId(), System.currentTimeMillis());
    }
}
