package com.gomeplus.bs.image_web;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import com.gomeplus.bs.image_uploader.ImageUploader;
import com.gomeplus.venus.common.imageutil.IllegalExpressionException;

/**
 * 上传图片 V1 接口
 * @author wanggang-ds6
 * @author jiayanwei
 */
@MultipartConfig()
@WebServlet("/ul/upload_img.json")
public class ImageV1Servlet extends AbstractImageServlet {

    private static final long serialVersionUID = 245617554182036624L;

    private static final Logger logger = LoggerFactory.getLogger(ImageV1Servlet.class);

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long time = System.currentTimeMillis();
        setIoEncodingUTF8(request, response);
        JSONObject json = new JSONObject();
        try {
            List<String> imageNameList = new ArrayList<>();
            // 获取上传的文件集合
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                // 循环处理上传的文件
                if ("imageArray".equals(part.getName()) && part.getSize() > 0) {
                    // 上传文件
                    imageNameList.add(upLoadImage(part.getInputStream()));
                }
            }

            json.put("code", 0);
            json.put("success", true);
            json.put("message", "成功");
            json.put("data", imageNameList);

        } catch (Exception e) {
            logger.error("upload failed, reason: ", e);
            json.put("code", 881014);
            json.put("success", false);
            json.put("message", "上传图片失败");
            json.put("data", null);
        }
        time = System.currentTimeMillis() - time;
        logger.info("elapsed time: {}ms", time);
        doJsonResponse(response, json);
    }

    /**
     * 上传单张图片
     *
     * @param inputStream 图片流
     * @return 图片地址
     * @throws IOException                流处理错误
     * @throws IllegalExpressionException url处理错误
     */
    private String upLoadImage(InputStream inputStream) throws IOException, IllegalExpressionException {
        return ImageUploader.uploadImageWithSuffix(inputStream).url;
    }
}
