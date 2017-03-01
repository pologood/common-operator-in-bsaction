package cn.com.mx.webapi.common.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;
import cn.com.mx.webapi.common.exceptions.code.C422Exception;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by neowyp on 2016/4/5.
 */
//@Component已经在spring中声明bean，不需要再用注解
@Slf4j
public class ValidateProLoadService {
    private final static Map<String, Properties> proMap = new HashMap<String, Properties>();
    private final static String PATH_PREFIX = "validate/regexp/";
    private final static String PATH_SUFFIX = ".properties";

    /**
     * 载入配置文件
     *
     * @param module
     * @throws Exception 
     */
    private void loadProperties(String module) throws Exception {
        if (proMap.containsKey(module)) {
            return;
        } else {
            InputStream fis = null;
            try {
                Properties prop = new Properties();// 属性集合对象
//                String path = "/" + PATH_PREFIX + module + PATH_SUFFIX;
                String path = PATH_PREFIX + module + PATH_SUFFIX;
                log.debug("resource stream path is :{}!", path);
                fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);// 属性文件输入流，默认service
                prop.load(fis);// 将属性文件流装载到Properties对象中
                proMap.put(module, prop);
            } catch (Exception e) {
            	//TODO 是否要打印error异常？
                log.error("load properties [{}] error!", module, e);
                throw new Exception("校验文件"+module + PATH_SUFFIX+"加载失败!");
            } finally {
                try {
                    fis.close();// 关闭流
                } catch (Exception e) {
                    log.error("finally close file!", e);
                }

            }

        }
    }

    /**
     * 获取key value
     *
     * @param module
     * @param key
     * @param defultValue
     * @return
     * @throws Exception 
     */
    public String getValue(String module, String key, String defultValue) throws Exception {
        log.debug("get key value [{}, {}, {}]", module, key, defultValue);
        loadProperties(module);
        Properties prop = proMap.get(module);
        if (null == prop) {
            log.debug("not has this module [{}].", module);
            return defultValue;
        } else {
            log.debug("module [{}] key [{}] value [{}].", module, key, prop.getProperty(key, defultValue));
            return prop.getProperty(key, defultValue);
        }
    }

    public static void main(String[] args) {
        try {
            InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("validate/regexp/social.properties");
            fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("/app.properties");
            URL url = Thread.currentThread().getContextClassLoader().getResource("app.properties");
            log.debug("");
            ValidateProLoadService proLoadService = new ValidateProLoadService();
            proLoadService.getValue("social", "group.GET", "");
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
