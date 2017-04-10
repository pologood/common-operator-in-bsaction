package com.gomeplus.comx.schema.onError;

import com.alibaba.fastjson.JSONObject;
import com.gomeplus.comx.utils.config.Config;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by xue on 2/22/17.
 */
public class StrategyTest {
    @Test
    public void test1() throws Exception{
        JSONObject configJson = new JSONObject();
        configJson.put("type", "fail");
        Config config = new Config(configJson);

        assertEquals(FailStrategy.getInstance(), Strategy.fromConf(config));
        configJson.put("type", "ignore");
        assertEquals(IgnoreStrategy.getInstance(), Strategy.fromConf(config));
        configJson.put("type", "byDefault");
        assertEquals(ByDefaultStrategy.class, Strategy.fromConf(config).getClass());

    }
}
