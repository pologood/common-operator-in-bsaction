package com.gomeplus.bs.tfs_uploader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import static com.alibaba.fastjson.JSON.parseObject;
import org.apache.commons.io.IOUtils;

/**
 * 将文件上传到 TFS
 * @author jiayanwei .
 * @since 2016-09-08
 */
public class TFSUploader {

    private static String TFS_POST_URL;
    private static String TFS_DELETE_URL;
    private static String CDN_URL;
    private static String CDN_USERNAME;
    private static String CDN_PASSWORD;

    static {
        Properties prop = new Properties();
        try (InputStream appIn = TFSUploader.class.getClassLoader().getResourceAsStream("app.properties")) {
            prop.load(appIn);
            TFS_POST_URL = prop.getProperty("comm_tfs_post_url");
            TFS_DELETE_URL = prop.getProperty("comm_tfs_delete_url");
            CDN_URL = prop.getProperty("cdn_url");
            CDN_USERNAME = prop.getProperty("cdn_username");
            CDN_PASSWORD = prop.getProperty("cdn_password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文件 POST 到 TFS 的 post 接口，无后缀
     * @param in 要上传的流
     * @return 文件名
     */
    public static String uploadFile(InputStream in) throws IOException {
        return uploadFile(in, null);
    }

    /**
     * 将文件 POST 到 TFS 的 post 接口
     * @param in 要上传的流
     * @param suffix0 后缀，如果用做扩展名需要包含开头的点
     * @return 文件名
     */
    public static String uploadFile(InputStream in, String suffix0) throws IOException {
        byte[] bytes = IOUtils.toByteArray(in);
        return uploadFile(bytes, suffix0);
    }

    /**
     * 将文件 POST 到 TFS 的 post 接口
     * @param bytes 要上传的文件内容
     * @param suffix0 后缀，如果用做扩展名需要包含开头的点
     * @return 文件名
     */
    public static String uploadFile(byte[] bytes, String suffix0) throws IOException {
        InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(bytes), bytes.length,
                ContentType.APPLICATION_OCTET_STREAM);

        String suffix = null == suffix0? "": suffix0;
        String query = suffix.isEmpty()? "": "?suffix=" + suffix;


        HttpPost httppost = new HttpPost(TFS_POST_URL + query);
        httppost.setEntity(reqEntity);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        String responseText = EntityUtils.toString(entity);
        return parseObject(responseText, new TypeReference<Map<String, String>>(){}).get("TFS_FILE_NAME") + suffix;
    }

    /**
     * 外站转内站：
     * 将 url 对应文件下下来，POST 到 TFS 的 post 接口，无后缀
     * @param url 要上传的源文件URL
     * @return 文件名
     */
    public static String transfer(String url) throws IOException {
        HttpGet httpget = new HttpGet(url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpget);
        return uploadFile(response.getEntity().getContent(), null);
    }

    /**
     *  删除tfs
     *  @param imageId
     *  @param suffix0  指定了.png 则严格按照 id+ .png寻渣佛图片，没有返回404，删除成功返回200
     */
    public static int delete(String imageId, String suffix0) throws IOException{

        String suffix = null == suffix0? "": suffix0;
        String query = suffix.isEmpty()? "": suffix;

        HttpDelete httpdelete = new HttpDelete(TFS_DELETE_URL + imageId + query);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpdelete);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        System.out.println(statusCode);
        return statusCode;
    }

    /**
     *
     * @param url  https://i-pre.meixincdn.com/v1/img/T1atYTB4DT1RXrhCrK.jpg
     * @return
     * @throws IOException
     */
    public static String refreshCDN(String url) throws IOException{
        //刷新CDN只能是生产环境，开发预生产配置为127.0.0.1，直接返回
        if (CDN_URL.equals("127.0.0.1")){
            return "OK, pre no need refresh!";
        }
        StringBuffer url0 = new StringBuffer();
        url0.append(CDN_URL)
                .append("username=" + CDN_USERNAME)
                .append("&password=" + CDN_PASSWORD)
                .append("&type_num=1")
                .append("&msg=" + url)
                .toString();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity, "UTF-8");
        return s;
    }
}
