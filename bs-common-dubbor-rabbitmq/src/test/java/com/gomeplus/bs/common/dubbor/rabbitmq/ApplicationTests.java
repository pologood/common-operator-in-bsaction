package com.gomeplus.bs.common.dubbor.rabbitmq;

import com.gomeplus.bs.common.dubbor.rabbitmq.common.SubmitAction;
import com.gomeplus.bs.common.dubbor.rabbitmq.util.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = com.gomeplus.bs.common.dubbor.rabbitmq.Application.class)
@WebAppConfiguration
public class ApplicationTests {

    @Autowired
    private SendMqProducer sendMqProducer;

    @Autowired
    private SpringRabbitMqReceiver receiver;




    @Test
    public void sendVo2MqTest() throws Exception {
        String id = "1001";
        String type = "user.userInfo";
        User user =new User();
        user.setId("123");
        user.setName("呵呵");
        sendMqProducer.sendVo2Mq(id, type, SubmitAction.UPDATE, user);
        System.out.println("bs.arch:" + receiver.receive("bs.arch"));
    }

}
