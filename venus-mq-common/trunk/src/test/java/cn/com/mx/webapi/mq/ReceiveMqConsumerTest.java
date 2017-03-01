package cn.com.mx.webapi.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author daisyli
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations={"classpath*:/spring/venus-mq-servlet-persistence-context-test.xml"})
public class ReceiveMqConsumerTest {
	
	@Autowired
	private ReceiveMqConsumer consumer;
	
	@Test
	public void test() {
		consumer.receive("bsarchQueue");
	}

}
