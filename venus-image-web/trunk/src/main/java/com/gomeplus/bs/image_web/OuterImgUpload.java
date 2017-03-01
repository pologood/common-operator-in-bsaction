package com.gomeplus.bs.image_web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONObject;
import com.gomeplus.bs.image_uploader.ImageUploader;
import com.gomeplus.bs.image_web.common.ImageUploadMessage;
import com.gomeplus.bs.image_web.common.ThirdAccount;
import com.gomeplus.bs.image_web.util.CollectionUtil;
import com.gomeplus.bs.image_web.util.SpringMongoUtil;
import com.gomeplus.venus.common.imageutil.IllegalExpressionException;

@MultipartConfig()
@WebServlet("/ul/upload.json")
public class OuterImgUpload extends HttpServlet {

	private static final long serialVersionUID = 2293258441319738300L;

	private static final Logger logger = LoggerFactory.getLogger(OuterImgUpload.class);

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long time = System.currentTimeMillis();
		JSONObject json = new JSONObject();
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		// 1.校验请求的合法性
		String secretKey = request.getParameter("secretKey");
		String currentTimeStr = request.getParameter("currentTime");
		if (StringUtils.isBlank(secretKey) || StringUtils.isBlank(currentTimeStr)) {
			json.put("code", 422);
			json.put("success", false);
			json.put("message", "参数错误");
			json.put("data", null);
		} else {
			Long currentTime = Long.valueOf(currentTimeStr);
			List<ThirdAccount> findAllObjects = SpringMongoUtil.getMongoOperation().find(
					new Query(Criteria.where("state").is(0)), ThirdAccount.class, "thirdAccount");
			boolean flag = false;
			Long lastRequestTime = 0l;
			boolean timeFlag = false;
			if (CollectionUtil.isNotEmpty(findAllObjects)) {
				for (ThirdAccount openTopicAccount : findAllObjects) {
					String key = openTopicAccount.getAccount() + currentTime;
					if(DigestUtils.md5DigestAsHex(key.getBytes()).equals(secretKey)){
						flag = true;
						lastRequestTime = openTopicAccount.getLastRequestTime();
						if((currentTime-lastRequestTime)>=2000){
							//跟新成最新的请求时间
							 SpringMongoUtil.getMongoOperation().updateFirst(new Query(Criteria.where("id").is(openTopicAccount.getId())), new Update().set("lastRequestTime", currentTime), ThirdAccount.class);
							 timeFlag = true;
						}
					}
				}
			}
			// 开始上传
			List<String> imageNameList = new ArrayList<String>();
			if (!flag) {
				json.put("code", 403);
				json.put("success", false);
				json.put("message", ImageUploadMessage.USER_FORBIDDEN_MSG);
				json.put("data", null);
			} else {
				if(!timeFlag){
					json.put("code", 403);
					json.put("success", true);
					json.put("message", ImageUploadMessage.REPEAT_REQUEST);
					json.put("data", null);
				}else{
					// 获取上传的文件集合
					Collection<Part> parts = request.getParts();
					for (Part part : parts) {
						// 循环处理上传的文件
						if ("imageArray".equals(part.getName()) && part.getSize() > 0) {
							// 上传文件
							try {
								imageNameList.add(upLoadImage(part.getInputStream()));
								json.put("code", 200);
								json.put("success", true);
								json.put("message", "成功");
								json.put("data", imageNameList);
							} catch (IllegalExpressionException e) {
								json.put("code", 500);
								json.put("success", false);
								json.put("message", "上传图片失败");
								json.put("data", imageNameList);
								logger.error("upload picture exception", e);
							}
						}
					}
				}
			}
		}
		time = System.currentTimeMillis() - time;
		logger.info("upload third picture time: {}ms", time);
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		out.flush();
		out.close();
	}

	/**
	 * 上传单张图片
	 *
	 * @param inputStream
	 *            图片流
	 * @return 图片地址
	 * @throws IOException
	 *             流处理错误
	 * @throws IllegalExpressionException
	 *             url处理错误
	 */
	private String upLoadImage(InputStream inputStream) throws IOException, IllegalExpressionException {
		final ImageUploader.ImageInfo imageInfo = ImageUploader.uploadImageWithSuffix(inputStream);
		return imageInfo.url;
	}
}
