package com.gomeplus.comx.schema;

import com.gomeplus.comx.boot.ComxConfLoader;
import com.gomeplus.comx.utils.config.Config;
import com.gomeplus.comx.utils.config.ConfigException;
import com.gomeplus.comx.utils.config.Loader;

/**
 * Created by xue on 12/16/16.
 * TODO comx 当中文件操作应当 替换为操作系统无关的常量 IO 包中 slash
 *
 */
public class SchemaLoader {

    public static Schema load(String urlPath, String method) throws ConfigException{
        String pathfile = ComxConfLoader.getComxHome() + "/apis" + urlPath;
        Config conf = Loader.fromJsonFile(pathfile+ "/"+ method + ".json");
        Schema schema = new Schema(conf);
        return schema;
    }
}
