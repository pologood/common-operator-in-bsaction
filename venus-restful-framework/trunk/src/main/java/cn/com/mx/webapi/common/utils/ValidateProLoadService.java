package cn.com.mx.webapi.common.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateProLoadService {
    private final static Map<String, Properties> proMap = new HashMap<String, Properties>();
    private final static String PATH_PREFIX = "validate/regexp/";
    private final static String PATH_SUFFIX = ".properties";

    /**
     * 载入配置文件
     *
     * @param module 模块
     * @throws Exception 
     */
    private void loadProperties(String module) throws Exception {
        if (!proMap.containsKey(module)) {
            Properties prop = new Properties();// 属性集合对象
            String path = PATH_PREFIX + module + PATH_SUFFIX;
            try (InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
                prop.load(fis);// 将属性文件流装载到Properties对象中
                proMap.put(module, prop);
            } catch (Exception e) {
                throw new Exception("校验文件" + module + PATH_SUFFIX + "加载失败!");
            }
        }
    }

    /**
     * 获取key value
     * @param module 模块名
     * @param key 键
     * @param defultValue 默认值
     * @return 值
     * @throws Exception 
     */
    public String getValue(String module, String key, String defultValue) throws Exception {
        loadProperties(module);
        Properties prop = proMap.get(module);
        if (null == prop) {
            return defultValue;
        } else {
            return prop.getProperty(key, defultValue);
        }
    }
}
