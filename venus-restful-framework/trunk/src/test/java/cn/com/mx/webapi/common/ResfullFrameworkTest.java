package cn.com.mx.webapi.common;

import cn.com.mx.webapi.common.service.CheckService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/7/19 12:14
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/venus-spring_test.xml"})
public class ResfullFrameworkTest {

    @Test
    public void test(){
        log.debug("-------------");
    }

}
