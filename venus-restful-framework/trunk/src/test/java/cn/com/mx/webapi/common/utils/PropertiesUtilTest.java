package cn.com.mx.webapi.common.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/8/1 18:11
 */
public class PropertiesUtilTest {
    @Test
    public void test()
    {
        String value1 = PropertiesUtil.getProProperties("logRoot","");
        String value2 = PropertiesUtil.getProProperties("logRootHHH","default");
        String value3 = PropertiesUtil.getProProperties("app","logRootHHH","WWW");

        Assert.assertEquals("/gomeo2o/logs/venus",value1);
        Assert.assertEquals("default",value2);
        Assert.assertEquals("WWW",value3);

    }
}
