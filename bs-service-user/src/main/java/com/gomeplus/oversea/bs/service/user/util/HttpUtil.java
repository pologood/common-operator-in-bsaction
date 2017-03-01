package com.gomeplus.oversea.bs.service.user.util;

import com.gomeplus.oversea.bs.common.exception.code5xx.C500Exception;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by liuliyun-ds on 2017/2/14.
 * http工具类
 */
public class HttpUtil {

    protected static CloseableHttpClient client = null;
    static {
        PoolingHttpClientConnectionManager pccm = new PoolingHttpClientConnectionManager();
        pccm.setMaxTotal(2000);          //客户端总并行链接最大数
        pccm.setDefaultMaxPerRoute(2000); //每个主机的最大并行链接数

        int REQUEST_TIMEOUT = 3*1000;  //设置请求超时3秒钟
        int SO_TIMEOUT = 3*1000;       //设置等待数据超时时间3秒钟

//        LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

        RequestConfig requestConfig =RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setSocketTimeout(SO_TIMEOUT).build();
        client = HttpClients.custom().setConnectionManager(pccm)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    /**
     * 发送post请求
     */
    public static String doPost(String url,String body, Map<String,String> headers){
        String content = null;
        try {
            HttpPost post = new HttpPost(url);
            // 设置请求头信息
            if(headers != null){
                for (String key : headers.keySet()) {
                    post.setHeader(key, headers.get(key));
                }
            }
            post.setHeader("Connection", "close");
            if (StringUtils.isNotEmpty(body)) {
                post.setEntity(new StringEntity(body, "utf-8"));
            }
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() > 299) {
                throw new C500Exception("服务器异常");
            }
            content = getContent(response.getEntity());

        } catch (Exception e) {
            throw new C500Exception("服务器异常");
        }

        return content;
    }

    /**
     * 发送get请求
     * @return
     */
    public static String doGet(String url,Map<String,String> headers){
        String content = null;
        try {
            HttpGet get = new HttpGet(url);
            // 设置请求头信息
            if(headers != null){
                for (String key : headers.keySet()) {
                    get.setHeader(key, headers.get(key));
                }
            }
            get.setHeader("Connection", "close");
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() > 299) {
                throw new C500Exception("服务器异常");
            }
            content = getContent(response.getEntity());
        } catch (Exception e) {
            throw new C500Exception("服务器异常");
        }

        return content;
    }

    public static String getContent(HttpEntity entity)
            throws IOException
    {
        String content = null;
        int length =(int) entity.getContentLength();
        InputStream input = entity.getContent();
        if (length != -1)
        {
            int count = 0, read = 0;
            byte[] bytes = new byte[length];
            while(true)
            {
                read = input.read(bytes, count, length - count);
                count += read;
                if (read == -1 || count >= bytes.length)
                    break;
            }
            content = new String(bytes, "utf-8");

        } else
        {
            byte[] bytes = getContentBytes(entity);
            content = new String(bytes, "utf-8");
        }
        input.close();
//        close(entity);
        return content;
    }

    private static byte[] getContentBytes(HttpEntity entity)
            throws IOException
    {
        byte[] bytes = new byte[256];
        int count = 0;
        InputStream input = entity.getContent();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while(true)
        {
            count = input.read(bytes);
            if (count == -1)
                break;
            output.write(bytes, 0, count);
        }
        input.close();
//        close(entity);
        return output.toByteArray();
    }
}
