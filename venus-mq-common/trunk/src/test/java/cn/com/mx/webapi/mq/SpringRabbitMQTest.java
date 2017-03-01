package cn.com.mx.webapi.mq;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

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
public class SpringRabbitMQTest {
	
	@Autowired
	private SpringRabbitMqSender sender;
	
	@Autowired
	private SpringRabbitMqReceiver receiver;
	
	private final String userJson = "{'name':'Stefanie', 'id':'9527', 'type':'user.userInfo', 'action':'UPDATE'}";
	
	@Test
	public void testSend() {
		String routingKey = "user.userInfo";
		sender.send(routingKey, userJson);
//		routingKey = "vshop.vshopInfo";
//		sender.send(routingKey, "{'name':'Stella', 'id':'9528'}");
//		sender.send(routingKey, "{'name':'Stella', 'id':'9529'}");
//		sender.send(routingKey, "{'name':'Stella', 'id':'9530'}");
//		sender.send(routingKey, "{'name':'Stella', 'id':'9531'}");
//		sender.send(routingKey, "{'name':'Stella', 'id':'9532'}");
//		sender.send(routingKey, "{'name':'Stella', 'id':'9533'}");
	}
	
	@Test
	public void testReceive() throws InterruptedException {
//		String userStringSearch = receiver.receive("bsarchQueue");
//		System.out.println(userStringSearch);
//		assertEquals(userJson, userStringSearch);
//		
//		String userStringTrade = receiver.receive("searchQueue");
//		System.out.println(userStringTrade);
//		assertEquals(userJson, userStringTrade);
//		
//		String userStringTrade2 = receiver.receive("vshopQueue");
//		System.out.println(userStringTrade2);
//		assertEquals(userJson, userStringTrade2);
		
		String userStringSearch = "";
		String bsarchString = "";
		int i=0;
		while (i < 10) {
			userStringSearch = receiver.receive("bs.social");
			System.out.println("bs.social：" + userStringSearch);
			
			bsarchString = receiver.receive("bs.arch");
			System.out.println("bs.arch:" + bsarchString);
			System.out.println("bs.item:" + receiver.receive("bs.item"));
			i++;
			Thread.sleep(1000);
		}
		
	}
	
}
