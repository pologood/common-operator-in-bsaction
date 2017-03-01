package com.gomeplus.bs.image_uploader.mongo;

import com.alibaba.fastjson.JSON;
import com.gomeplus.bs.image_uploader.ImageUploader.ImageInfo;
import com.gomeplus.bs.image_uploader.util.MongoUtil;
import com.gomeplus.bs.image_uploader.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.concurrent.Callable;

/**
 * 图片信息上传入mongo库
 */
@Slf4j
public class ImageAfterUploadHandlerAudit implements Callable<String> {


	public ImageInfoAudit imageinfo;

	public ImageAfterUploadHandlerAudit(ImageInfoAudit imageinfo) {
		this.imageinfo = imageinfo;
	}
	
	public String saveImageInfo(ImageInfoAudit imageinfo){
		String result = MongoUtil.insert(new Document("url", imageinfo.getUrl()).
				append("width", imageinfo.getWidth()).
				append("height", imageinfo.getHeight()).
				append("needAudit", imageinfo.getNeedAuit()));
		System.err.println(result);
		return result;
	}

	@Override
	public String call() throws Exception {
		return saveImageInfo(imageinfo);
	}
}
