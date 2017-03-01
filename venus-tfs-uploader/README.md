# TFS 上传公共模块

## 环境相关属性
依赖 app.properties 中 `comm_tfs_post_url` 属性

## pom.xml 添加

```xml
    <dependency>
        <groupId>com.gomeplus</groupId>
        <artifactId>venus-tfs-uploader</artifactId>
        <version>1.4</version>
    </dependency>
```

## 提供静态方法如下

```java
import com.gomeplus.bs.tfs_uploader.TFSUploader;

class TFSUploader {
    /**
     * 将文件 POST 到 TFS 的 post 接口，无后缀
     * @param in 要上传的流
     * @return 文件名
     */
    public static String uploadFile(InputStream in) throws IOException;

    /**
     * 将文件 POST 到 TFS 的 post 接口
     * @param in 要上传的流
     * @param suffix0 后缀，如果用做扩展名需要包含开头的点
     * @return 文件名
     */
    public static String uploadFile(InputStream in, String suffix0) throws IOException;

    /**
     * 外站转内站：
     * 将 url 对应文件下下来，POST 到 TFS 的 post 接口，无后缀
     * @param url 要上传的源文件URL
     * @return 文件名
     */
    public static String transfer(String url) throws IOException;
}
```
