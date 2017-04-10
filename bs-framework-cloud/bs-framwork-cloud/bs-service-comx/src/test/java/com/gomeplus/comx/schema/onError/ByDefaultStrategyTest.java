package com.gomeplus.comx.schema.onError;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gomeplus.comx.context.Context;
import com.gomeplus.comx.utils.config.Config;
import com.gomeplus.comx.utils.log.ComxLogger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by xue on 2/22/17.
 */
public class ByDefaultStrategyTest {


    @Test
    public void test1() throws Exception{
        JSONObject configJson = new JSONObject();
        configJson.put("type", "byDefault");
        configJson.put("defaultValue", new JSONArray());
        Config config = new Config(configJson);

        Context context = new Context();
        context.setLogger(new ComxLogger());
        Exception ex = new Exception("msg");
        assertEquals(new JSONArray(), Strategy.fromConf(config).handleSourceException(ex, context));


        configJson.put("defaultValue", "aaa");
        Config config2 = new Config(configJson);
        assertEquals("aaa", Strategy.fromConf(config2).handleSourceException(ex, context));
    }
}
