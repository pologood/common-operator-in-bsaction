package test.com.gomeplus.bs.image_uploader;

import com.alibaba.fastjson.JSON;
import com.gomeplus.bs.image_uploader.ImageUploader;
import com.gomeplus.bs.image_uploader.mongo.ImageInfoAudit;
import com.gomeplus.bs.image_uploader.util.MongoUtil;
import org.bson.Document;
import org.junit.Test;

import java.io.InputStream;
import java.util.Date;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by wangchangye on 2017/2/13.
 *
 * @Author wangchangye
 */
public class MongoTest {

    @Test
    public void testInsert(){
        MongoUtil.insert(new Document("id","123").
                append("account","259").
                append("accountId","259").
                append("code","1").append("state","0").append("desc","ceshi").append("createTime",new Date()).append("updateTime",new Date()));
    }

    @Test
    public void testUploadImage() throws Exception {
        InputStream in = MongoTest.class.getClassLoader().getResourceAsStream("Java_logo.png");
        ImageInfoAudit infoAudit = ImageUploader.uploadImageAudit(in, Boolean.TRUE);
        System.out.println("upload: " + JSON.toJSONString(infoAudit));
        final Pattern pattern = Pattern.compile("https?://[^.]+\\.meixincdn\\.com/v1/img/T1[0-9a-zA-Z._]{16}");
        assertTrue(pattern.matcher(infoAudit.getUrl()).matches());
        assertEquals(259, infoAudit.getWidth());
        assertEquals(259, infoAudit.getHeight());
        in.close();
    }


}
