package com.gomeplus.bs.image_uploader;

import com.gomeplus.bs.image_uploader.mongo.ImageAfterUploadHandlerAudit;
import com.gomeplus.bs.image_uploader.mongo.ImageInfoAudit;
import com.gomeplus.bs.tfs_uploader.TFSUploader;
import com.gomeplus.venus.common.imageutil.IllegalExpressionException;
import com.gomeplus.venus.common.imageutil.UrlConverter;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 将图片上传到 TFS
 * @author jiayanwei .
 * @since 2016-09-08
 */
@Slf4j
public class ImageUploader {

    private static UrlConverter urlConverter;
    private final static ExecutorService executorService;
    static {
    	executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2 +1);
        Properties prop = new Properties();
        try (InputStream appIn = TFSUploader.class.getClassLoader().getResourceAsStream("app.properties")) {
            prop.load(appIn);
            String urlPrefix = prop.getProperty("imageutil_urlPrefix") + "/v1/img";
            String candidateSubdomains = prop.getProperty("imageutil_candidateSubdomains");
            urlConverter = new UrlConverter(urlPrefix, candidateSubdomains);
        } catch (IOException e) {
            log.error("load app.properties failed:{}", e);
        }
    }


    public static final class ImageInfo {
        public String url;
        public int width;
        public int height;
    }

    /**
     * 图片上传到 TFS，无后缀
     * @param in 要上传的图片内容流
     * @return 图片信息
     * @throws IOException, IllegalExpressionException
     */
    public static ImageInfo uploadImage(InputStream in) throws IOException, IllegalExpressionException {
        byte[] bytes = IOUtils.toByteArray(in);
        return uploadImage(bytes, null);
    }

    /**
     * 图片上传到 TFS，识别后缀
     * @param in 要上传的图片内容流
     * @return 图片信息
     * @throws IOException, IllegalExpressionException
     */
    public static ImageInfo uploadImageWithSuffix(InputStream in) throws IOException, IllegalExpressionException {
        byte[] bytes = IOUtils.toByteArray(in);
        String suffix = ImageFormat.getImageSuffix(bytes);
        return uploadImage(bytes, suffix);
    }

    /**
     * 外站转内站：
     * 将 imgUrl 对应图片下下来，上传到 TFS，无后缀
     * @param imgUrl 要上传的源文件URL
     * @return 图片信息
     * @throws IOException, IllegalExpressionException
     */
    public static ImageInfo transfer(String imgUrl) throws IOException, IllegalExpressionException {
        return uploadImage(downloadImage(imgUrl));
    }

    /**
     * 外站转内站：
     * 将 imgUrl 对应图片下下来，上传到 TFS，识别后缀
     * @param imgUrl 要上传的源文件URL
     * @return 图片信息
     * @throws IOException, IllegalExpressionException
     */
    public static ImageInfo transferWithSuffix(String imgUrl) throws IOException, IllegalExpressionException {
        return uploadImageWithSuffix(downloadImage(imgUrl));
    }

    private static InputStream downloadImage(String imgUrl) throws IOException {
        HttpGet httpget = new HttpGet(imgUrl);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpget);
        return response.getEntity().getContent();
    }

    /**
     * 传图内部实现，图片上传到 TFS，用指定后缀
     */
    private static ImageInfo uploadImage(byte[] bytes, String suffix) throws IOException, IllegalExpressionException {
        ImageInfo info = new ImageInfo();
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
        info.url = urlConverter.getHashedFullLocation(TFSUploader.uploadFile(bytes, suffix));
        info.width = img.getWidth();
        info.height = img.getHeight();
        try {
        	handleAfterImageUpload(info);
		} catch (Exception e) {
			log.error("failed to save imgInfo for imgUrl:{}, detail :{}", info.url, e);
		}
        return info;
    }
    
    /**
     * 图片上传后处理，以异步方式将图片信息存redis
     * @param info 图片信息
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    private static void handleAfterImageUpload(ImageInfo info) throws InterruptedException, ExecutionException{
    	executorService.submit(new ImageAfterUploadHandler(info));
    }


    /**
     * 下列方法是为了兼容mongo逻辑
     * @param in
     * @param needAudit
     * @return
     * @throws IOException
     * @throws IllegalExpressionException
     */

    public static ImageInfoAudit uploadImageAudit(InputStream in, Boolean needAudit) throws IOException, IllegalExpressionException {
        byte[] bytes = IOUtils.toByteArray(in);
        return uploadImageAudit(bytes, null, needAudit);
    }
    public static ImageInfoAudit uploadImageWithSuffixAudit(InputStream in, Boolean needAudit) throws IOException, IllegalExpressionException {
        byte[] bytes = IOUtils.toByteArray(in);
        String suffix = ImageFormat.getImageSuffix(bytes);
        return uploadImageAudit(bytes, null, needAudit);

    }
    public static ImageInfoAudit transfer(String imgUrl,Boolean needAudit) throws IOException, IllegalExpressionException {
        return uploadImageAudit(downloadImage(imgUrl),needAudit);
    }
    public static ImageInfoAudit transferWithSuffix(String imgUrl, Boolean needAudit) throws IOException, IllegalExpressionException {
        return uploadImageWithSuffixAudit(downloadImage(imgUrl), needAudit);
    }

    private static ImageInfoAudit uploadImageAudit(byte[] bytes, String suffix, Boolean needAudit) throws IOException, IllegalExpressionException {
        ImageInfoAudit info = new ImageInfoAudit();
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
        info.setUrl(urlConverter.getHashedFullLocation(TFSUploader.uploadFile(bytes, suffix)));
        info.setWidth(img.getWidth());
        info.setHeight(img.getHeight());
        info.setNeedAuit(needAudit);
        //入Mongo
        try {
            handleAfterImageUploadAudit(info);
        } catch (Exception e) {
            log.error("failed to save imgInfo for imgUrl:{}, detail :{}", info.getUrl(), e);
        }
        return info;
    }
    private static void handleAfterImageUploadAudit(ImageInfoAudit info) throws InterruptedException, ExecutionException{
        executorService.submit(new ImageAfterUploadHandlerAudit(info));
    }









}//end of class

