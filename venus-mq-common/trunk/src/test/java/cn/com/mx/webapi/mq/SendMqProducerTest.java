package cn.com.mx.webapi.mq;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.com.mx.webapi.mq.common.SubmitAction;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations={"classpath*:/spring/venus-mq-servlet-persistence-context-test.xml"})
public class SendMqProducerTest {

	@Autowired
	private SendMqProducer pro;
	
	@Autowired
	private SpringRabbitMqReceiver receiver;
	
	@BeforeClass
	public static void before() throws Exception {
	}

	@AfterClass
	public static void after() throws Exception {
	}
//	@Test//json
//	public void testSend() {
//		String type = "item";
//		String data = "{\"id\":\"0375\",\"city\":\"北京\"}";
//		String serverAddr = "127.0.0.1", topic = "item", groupName = "item";
//		List<String> destList = new ArrayList<String>();
//		destList.add("social");
//		destList.add("trade");
//		pro.sendJson2Mq(type, SubmitAction.UPDATE, data, destList);;
//		System.out.println("finish");
//	}
	
	@Test//vo
	public void testSend2Mq() {
		String id = "1001";
		String type = "user.userInfo";
		User user =new User();
		user.setId("123");
		user.setName("呵呵");
		pro.sendVo2Mq(id, type, SubmitAction.UPDATE, user);
		System.out.println("bs.arch:" + receiver.receive("bs.arch"));
		
	}
//	@Test//list
//	public void testSend3() {
//		//String json = "{\"id\":\"0375\",\"city\":\"北京\"}";
//		String type = "item";
//		User user1 =new User();
//		user1.setId("123");
//		user1.setName("呵呵");
//		User user2 = new User();
//		user2.setId("456");
//		user2.setName("哈哈");
//		List<User> list = new ArrayList<>();
//		list.add(user1);
//		list.add(user2);
//		String serverAddr = "127.0.0.1", topic = "item", groupName = "item";
//		List<String> destList = new ArrayList<String>();
//		destList.add("social");
//		destList.add("trade");
//		pro.sendVo2Mq(type, SubmitAction.UPDATE, list, destList);
//	}
//	@Test//map
//	public void testSend4() {
//		//String json = "{\"id\":\"0375\",\"city\":\"北京\"}";
//		String type = "item";
//		User user1 =new User();
//		user1.setId("123");
//		user1.setName("呵呵");
//		User user2 = new User();
//		user2.setId("456");
//		user2.setName("哈哈");
//		Map<String,User> data = new HashMap<>();
//		data.put("第一个", user1);
//		data.put("第二个", user2);
//		String serverAddr = "127.0.0.1", topic = "item", groupName = "item";
//		List<String> destList = new ArrayList<String>();
//		destList.add("social");
//		destList.add("trade");
//		pro.sendMap2Mq(type, SubmitAction.UPDATE, data, destList);
//	}
	
	
}
