package cn.com.mx.webapi.common.utils;

import cn.com.mx.webapi.common.utils.ValidateJsonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/8/1 11:32
 */
@Slf4j
public class ValidateJsonUtilTest {
    @Test
    public void getJsonSchemaTest() throws Exception {
        String jsonStr = "{\"name\":\"zhou\",\"age\":26}";
        ValidateJsonUtil validateJsonUtil = new ValidateJsonUtil();
        validateJsonUtil.validateJsonString("test", "test", "get", jsonStr);
        try {
            validateJsonUtil.validateJsonString("test", "demo", "get", jsonStr);
        }catch (Exception e){
            Assert.assertEquals("resource /validate/json/test/demo_get.json not found",e.getMessage());
        }
    }
    
    
    
    
}
