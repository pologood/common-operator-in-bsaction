package com.gomeplus.bs.thumbnail.util;

import com.gomeplus.bs.thumbnail.config.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by daisyli on 16/5/4.
 */
public class ConfigUtils {

    private String configPath;

    public ConfigUtils(String configPath) {
        this.configPath = configPath;
    }

    public void init() {
        Properties prop = new Properties();
        try {
            FileInputStream in = new FileInputStream(configPath);
            prop.load(in);
            in.close();
        } catch (IOException e) {
            System.out.println("Read properties file failed");
        }
        Config.DB_URL = prop.getProperty("db_url", "");
        Config.DB_USERNAME = prop.getProperty("db_username", "");
        Config.DB_PASSWORD = prop.getProperty("db_password", "");
        Config.URL_BASE = prop.getProperty("url_base", "");
    }
}
