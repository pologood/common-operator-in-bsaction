package com.gomeplus.bs.image_uploader;

import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSON;
import com.gomeplus.bs.image_uploader.ImageUploader.ImageInfo;
import com.gomeplus.bs.image_uploader.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 图片上传后处理
 * @author liucheng
 * @since 2016年9月19日
 */
@Slf4j
public class ImageAfterUploadHandler implements Callable<String> {

	private static String IMG_META_KEY_TEMPLATE = "arch.imageMeta_";
	
	private ImageInfo imageinfo;

	ImageAfterUploadHandler(ImageInfo imageinfo) {
		this.imageinfo = imageinfo;
	}
	
	String saveImgMeta(String url){
		// 从url截取图片id
		int beginIdx = url.lastIndexOf("/");
		String imgId = url.substring(beginIdx + 1, beginIdx + 19);
		String imgMetaKey = IMG_META_KEY_TEMPLATE + imgId;
		String jsonStr = JSON.toJSONString(imageinfo);
		String returnCode = RedisUtil.set(imgMetaKey, jsonStr);
		if(!"OK".equals(returnCode)){
			log.info("failed to save imgInfo, imgUrl:{}", url);
		}
		return returnCode;
	}

	@Override
	public String call() throws Exception {
		return saveImgMeta(imageinfo.url);
	}
}
