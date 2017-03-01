# 图片上传公共模块

## 依赖项目
* venus-tfs-uploader

## 环境相关属性
* venus-tfs-uploader  
   app.properties 中属性：
   `comm_tfs_post_url`
* venus-common-imageutil  
   app.properties 中属性： 
   `imageutil_urlPrefix`
   `imageutil_candidateSubdomains`
   
## pom.xml 添加

```xml
    <dependency>
        <groupId>com.gomeplus</groupId>
        <artifactId>venus-image-uploader</artifactId>
        <version>1.3</version>
    </dependency>
```

## 提供静态方法

```java
import com.gomeplus.bs.image_uploader.ImageUploader;

class ImageUploader {

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
    public static ImageInfo uploadImage(InputStream in) throws IOException, IllegalExpressionException;

    /**
     * 图片上传到 TFS，识别后缀
     * @param in 要上传的图片内容流
     * @return 图片信息
     * @throws IOException, IllegalExpressionException
     */
    public static ImageInfo uploadImageWithSuffix(InputStream in) throws IOException, IllegalExpressionException;

    /**
     * 外站转内站：
     * 将 imgUrl 对应图片下下来，上传到 TFS，无后缀
     * @param imgUrl 要上传的源文件URL
     * @return 图片信息
     * @throws IOException, IllegalExpressionException
     */
    public static ImageInfo transfer(String imgUrl) throws IOException, IllegalExpressionException;

    /**
     * 外站转内站：
     * 将 imgUrl 对应图片下下来，上传到 TFS，识别后缀
     * @param imgUrl 要上传的源文件URL
     * @return 图片信息
     * @throws IOException, IllegalExpressionException
     */
    public static ImageInfo transferWithSuffix(String imgUrl) throws IOException, IllegalExpressionException;
```
