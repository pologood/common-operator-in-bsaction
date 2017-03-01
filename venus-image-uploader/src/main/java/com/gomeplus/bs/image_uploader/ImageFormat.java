package com.gomeplus.bs.image_uploader;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiayanwei
 * @since 2016-09-13
 */
public class ImageFormat {
    private final static Map<String, String> formats = new HashMap<>();
    static {
        formats.put("\u0089\u0050\u004E\u0047", ".png");
        formats.put("\u0047\u0049\u0046\u0038", ".gif");
    }

    /**
     * 根据图片内容返回扩展名
     * @param bytes 图片内容
     * @return 扩展名
     */
    public static String getImageSuffix(byte[] bytes) {
        String suffix = formats.get(new String(Arrays.copyOf(bytes, 4), StandardCharsets.ISO_8859_1));
        return null == suffix? ".jpg": suffix;
    }
}
