package com.gomeplus.bs.image_web;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.gomeplus.venus.common.imageutil.IllegalExpressionException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 上传图片 V2 接口
 * - 目前只支持已知外站图的 URL 转换
 * @author jiayanwei
 */
@WebServlet({"/v2/image/image"})
public class ImageV2Servlet extends AbstractImageServlet {

    private static final long serialVersionUID = 5933696482336349565L;

    private static final Logger logger = LoggerFactory.getLogger(ImageV1Servlet.class);

    private static String IMG_URL_PREFIX;

    static {
        Properties prop = new Properties();
        try (InputStream appIn = ImageV2Servlet.class.getClassLoader().getResourceAsStream("app.properties")) {
            prop.load(appIn);
            IMG_URL_PREFIX = prop.getProperty("imageutil_urlPrefix");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long time = System.currentTimeMillis();
        setIoEncodingUTF8(request, response);
        JSONObject json = new JSONObject();
        response.setContentType("application/json");

        try {
            String requestText =  IOUtils.toString(request.getInputStream());
            String srcUrl = JSONObject.parseObject(requestText, new TypeReference<Map<String, String>>(){})
                    .get("srcUrl");

            if(!srcUrl.startsWith("https://cdn.laigome.com/")) {
                throw new IllegalExpressionException(srcUrl);
            }

            json.put("message", "");
            Map<String, String> result = new HashMap<>();
            result.put("url",  IMG_URL_PREFIX + "/l/" + srcUrl.substring(24));
            json.put("data", result);
        } catch (IllegalExpressionException e) {
            logger.error("incorrect uri, reason: ", e);
            json.put("message", "参数校验失败");
            response.setStatus(422);
        } catch (Exception e) {
            logger.error("upload failed, reason: ", e);
            json.put("message", "上传图片失败");
            response.setStatus(500);
        }

        time = System.currentTimeMillis() - time;
        logger.info("elapsed time: {}ms", time);
        doJsonResponse(response, json);
    }
}
