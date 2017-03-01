package com.gomeplus.oversea.bs.service.user.util;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
/*
 * https请求工具类
 */
@Slf4j
public class HttpsClientUtil {
	/**
	 * https get请求
	 * @param uri
	 * @return
	 */
	public static String doHttpsGet(String uri,String charset){
		log.info("Https request with uri[{}]",uri);
		CloseableHttpClient httpclient = HttpClients.createDefault();
    	String result = "";
    	try {
			HttpGet httpget = new HttpGet(uri);
			httpget.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpget.addHeader("Connection", "Keep-Alive");
			log.info("executing request : {} ", httpget.getURI());
			CloseableHttpResponse response = httpclient.execute(httpget);
			log.info("Https request with response[{}]", JSONObject.toJSONString(response));
			try {
				if (response.getStatusLine().getStatusCode() == 200) {
					// 获取响应实体
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						// 打印响应内容长度
						log.info("Response content length:{} ", entity.getContentLength());
						result = EntityUtils.toString(entity,charset);
					} else {
						log.error("entity is null, uri : {}", uri);
					}
				} else {
					log.error("http status is not 200, uri : {}", uri);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			log.error("Call badwordsService error, uri : {}", uri, e);
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				log.error("Call badwordsService error, close connection error, uri : {}", uri, e);
			}
		}
    	return result;
	}
}