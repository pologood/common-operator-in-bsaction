package cn.com.mx.webapi.common.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/8/1 17:18
 */
public class SplitPublicParamTest {
    @Test
    public void SplitPublicParamTest(){
        String devive="Android/1.0.5/Samsung Galaxy/fadfadfa232323_2323ioasdf";
        String rst1 = SplitPublicParam.getParameterPart(devive,1);
        String rst2 = SplitPublicParam.getParameterPart(devive,5);
        Assert.assertEquals("Android",rst1);
        Assert.assertNull(rst2);
    }
}
