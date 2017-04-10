package com.gomeplus.comx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.comx.utils.config.Config;
import com.gomeplus.comx.utils.config.ConfigException;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;

/**
 * Created by xue on 3/23/17.
 */
public class Test {
    @org.junit.Test
    public void testStr() throws Exception {
        Object testobj = "a";
        assertEquals("not equal", "a", "a");
    }
}
