package cn.com.mx.webapi.common.utils;

import com.alibaba.fastjson.support.odps.udf.CodecCheck;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.Object;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/8/1 17:45
 */
@Slf4j
public class ParametersPrinterTest {
    @Test
    public void toStringTest()
    {
        ParametersPrinter parametersPrinter = new ParametersPrinter();
        long startTime = 1470045512016L;
        long endtTime = 1470045513030L;
        parametersPrinter.setStartTime(startTime);
        parametersPrinter.setRequestUrl("/v2/user/user");
        parametersPrinter.setRequestBody(null);
        parametersPrinter.setEndTime(endtTime);
        Assert.assertEquals("{\"startTime\":1470045512016,\"endTime\":1470045513030,\"requestUrl\":\"/v2/user/user\",\"costTime\":1014}",
                parametersPrinter.toString(true));
        Assert.assertEquals("null",parametersPrinter.toString(true));
    }
}
