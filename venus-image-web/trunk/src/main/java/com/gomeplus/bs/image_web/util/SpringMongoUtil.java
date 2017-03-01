package com.gomeplus.bs.image_web.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@SuppressWarnings("deprecation")
public class SpringMongoUtil {
	private static MongoOperations mongoOperation;  
	private static String MONGO_DATABASE;
	private static String DB_NAME;

	private SpringMongoUtil(){}
	public static MongoOperations getMongoOperation(){
		Properties prop = new Properties();
        InputStream fis = null;
        try {
            fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties");
            prop.load(fis);
            MONGO_DATABASE = prop.getProperty("mongodbReplica").trim();
            DB_NAME = prop.getProperty("social_mongodbName").trim();
        } catch (Exception e) {
            //log.error("load app.properties failed:{}", e);
            throw new RuntimeException("load app.properties failed");
        }
		List<ServerAddress> seeds = new ArrayList<ServerAddress>();

        String[] addressArray = MONGO_DATABASE.split(",");
        Pattern pattern = Pattern.compile("^.+[:]\\d{1,6}\\s*$");
        for (String address : addressArray) {
            boolean isIpPort = pattern.matcher(address).matches();
            if (!isIpPort) {
                throw new IllegalArgumentException("ip或 port不合法！");
            }
            String[] ipAndPort = address.split(":");
            ServerAddress hap = new ServerAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
            seeds.add(hap);
        }
        MongoClient client = new MongoClient(seeds);
        return mongoOperation = new MongoTemplate(client,DB_NAME);
	}
	
	
	
}
