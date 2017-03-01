package test.com.gomeplus.bs.image_uploader;

import com.alibaba.fastjson.JSON;
import com.gomeplus.bs.image_uploader.ImageUploader;
import com.gomeplus.bs.image_uploader.mongo.ImageInfoAudit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * ImageUploader Tester.
 *
 * @author jiayanwei
 * @version 1.0
 * @since 2016-09-12
 */
public class ImageUploaderTest {

    JedisCluster jc =null;
    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    @Before
    public void before() throws Exception {
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        //Jedis Cluster will attempt to discover cluster nodes automatically
        config.setMaxIdle(2);
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(true);
        jedisClusterNodes.add(new HostAndPort("bj05-ops-rdsc01.dev.gomeplus.com", 8001));
        jedisClusterNodes.add(new HostAndPort("bj05-ops-rdsc02.dev.gomeplus.com", 8001));
        jedisClusterNodes.add(new HostAndPort("bj05-ops-rdsc03.dev.gomeplus.com", 8001));
        jc = new JedisCluster(jedisClusterNodes,2000,5,config);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: uploadImage(InputStream in)
     */
    @Test
    public void testUploadImage() throws Exception {
        InputStream in = ImageUploaderTest.class.getClassLoader().getResourceAsStream("Java_logo.png");
        ImageUploader.ImageInfo info = ImageUploader.uploadImage(in);
        System.out.println("upload: " + JSON.toJSONString(info));
        final Pattern pattern = Pattern.compile("https?://[^.]+\\.meixincdn\\.com/v1/img/T1[0-9a-zA-Z._]{16}");
        assertTrue(pattern.matcher(info.url).matches());
        assertEquals(259, info.width);
        assertEquals(259, info.height);
        in.close();
    }

    /**
     * Method: transfer(String imgUrl)
     */
    @Test
    public void testTransfer() throws Exception {
        String imgUrl = "http://wx.qlogo.cn/mmopen/ajNVdqHZLLBQS7ibTREdDqLk5hNcsaxEspNcodwCWrNYFR9PfR9qS7gTq98zFcuxw" +
                "TBiaFUyiajia6HfUCIJdT3Kicw/0";
        ImageUploader.ImageInfo info = ImageUploader.transfer(imgUrl);
        System.out.println("transfer: " + JSON.toJSONString(info));
        final Pattern pattern = Pattern.compile("https?://[^.]+\\.meixincdn\\.com/v1/img/T1[0-9a-zA-Z._]{16}");
        assertTrue(pattern.matcher(info.url).matches());
        assertEquals(638, info.width);
        assertEquals(638, info.height);
    }

    /**
     * Method: transferWithSuffix(String imgUrl)
     */
    @Test
    public void testTransferWithSuffix() throws Exception {
        String imgUrl = "http://wx.qlogo.cn/mmopen/ajNVdqHZLLBQS7ibTREdDqLk5hNcsaxEspNcodwCWrNYFR9PfR9qS7gTq98zFcuxw" +
                "TBiaFUyiajia6HfUCIJdT3Kicw/0";
        ImageUploader.ImageInfo info = ImageUploader.transferWithSuffix(imgUrl);
        System.out.println("transfer with suffix: " + JSON.toJSONString(info));
        final Pattern pattern = Pattern.compile(
                "https?://[^.]+\\.meixincdn\\.com/v1/img/T1[0-9a-zA-Z._]{16}\\.[a-zA-Z]{3}");
        assertTrue(pattern.matcher(info.url).matches());
        assertEquals(638, info.width);
        assertEquals(638, info.height);
    }

    /**
     * Method: uploadImage(InputStream in)，transferWithSuffix(String imgUrl) get VALUE from redis according KEY
     */
    @Test
    public void testRedis() throws Exception{
        //Method: uploadImage(InputStream in)
        InputStream in = ImageUploaderTest.class.getClassLoader().getResourceAsStream("Java_logo.png");
        ImageUploader.ImageInfo info = ImageUploader.uploadImage(in);
        System.out.println("info: " + JSON.toJSONString(info));
        String uploadKey = info.url.substring(info.url.lastIndexOf("/") + 1, info.url.lastIndexOf("/") + 19);
        System.out.println("imageId : " + uploadKey);

        Thread.sleep(2000);
        assertNotNull("get value from redis: ", jc.get("arch.imageMeta_" + uploadKey));
        ImageUploader.ImageInfo redisInfo = JSON.parseObject(jc.get("arch.imageMeta_" + uploadKey), ImageUploader.ImageInfo.class);
        assertEquals(info.url, redisInfo.url);
        assertEquals(info.height, redisInfo.height);
        assertEquals(info.width, redisInfo.width);

        //Method: transferWithSuffix(String imgUrl)
        String imgUrl = "http://wx.qlogo.cn/mmopen/ajNVdqHZLLBQS7ibTREdDqLk5hNcsaxEspNcodwCWrNYFR9PfR9qS7gTq98zFcuxw" +
                "TBiaFUyiajia6HfUCIJdT3Kicw/0";
        ImageUploader.ImageInfo info2 = ImageUploader.transferWithSuffix(imgUrl);
        System.out.println("info2:" + JSON.toJSONString(info2));
        String imageId = info2.url.substring(info2.url.lastIndexOf("/") + 1, info2.url.lastIndexOf("/") + 19);
        System.out.println("imageId is: " + imageId);

        Thread.sleep(2000);
        assertNotNull("get value from redis: ", jc.get("arch.imageMeta_" + imageId));
        ImageUploader.ImageInfo redisInfo2 = JSON.parseObject(jc.get("arch.imageMeta_" + imageId), ImageUploader.ImageInfo.class);
        assertEquals(info2.url, redisInfo2.url);
        assertEquals(info2.height, redisInfo2.height);
        assertEquals(info2.width, redisInfo2.width);
    }


    /**
     * Mongo
     * @throws Exception
     */
    @Test
    public void testUploadImageAudit() throws Exception {
        InputStream in = ImageUploaderTest.class.getClassLoader().getResourceAsStream("Java_logo.png");
        ImageInfoAudit infoAudit = ImageUploader.uploadImageAudit(in, Boolean.TRUE);
        System.out.println("upload: " + JSON.toJSONString(infoAudit));
        Thread.sleep(2000);
        final Pattern pattern = Pattern.compile("https?://[^.]+\\.meixincdn\\.com/v1/img/T1[0-9a-zA-Z._]{16}");
        assertTrue(pattern.matcher(infoAudit.getUrl()).matches());
        assertEquals(259, infoAudit.getWidth());
        assertEquals(259, infoAudit.getHeight());
        in.close();
    }
}
