package com.gomeplus.bs.image_uploader.util;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import redis.clients.jedis.HostAndPort;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by wangchangye on 2017/2/13.
 *
 * @Author wangchangye
 */
//@Slf4j
public class MongoUtil {
    static MongoCollection<Document> collection = null;
    static {
        Properties prop = new Properties();
        InputStream fis = null;
        try {
            fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties");
            prop.load(fis);
            String MONGO_FEED = prop.getProperty("mongodbReplica").trim();
            String DB_NAME = prop.getProperty("social_mongodbName").trim();

            List<ServerAddress> seeds = new ArrayList<ServerAddress>();


            String[] addressArray = MONGO_FEED.split(",");
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
            MongoClient mongoClient = new MongoClient(seeds);
            collection = mongoClient.getDatabase(DB_NAME).getCollection("imageAudit");
            fis.close();
        } catch (Exception e) {
            //log.error("load app.properties failed:{}", e);
            throw new RuntimeException("load app.properties failed");
        }
    }

    public static String insert(Document doc){
        collection.insertOne(doc);
        System.out.println("save to imageAudit:"+ doc.toString());
        return doc.toString();
    }

    public static void delete(Bson var){
        collection.deleteOne(var);
    }



}//endofclass
