package cn.com.mx.webapi.common.utils;

import cn.com.mx.webapi.common.utils.ValidateProLoadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/8/1 11:18
 */
@Slf4j
public class ValidateProLoadServiceTest {

    @Test
    public void validateTest(){
        try {
            InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("validate/regexp/test.properties");
            fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("/app.properties");
            URL url = Thread.currentThread().getContextClassLoader().getResource("app.properties");
            ValidateProLoadService proLoadService = new ValidateProLoadService();
            Assert.assertEquals("id",proLoadService.getValue("test", "test.GET", ""));
            Assert.assertEquals("",proLoadService.getValue("test","test.POST",""));
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
