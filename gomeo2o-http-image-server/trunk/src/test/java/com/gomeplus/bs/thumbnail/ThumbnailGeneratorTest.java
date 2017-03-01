package com.gomeplus.bs.thumbnail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;

/**
 * ThumbnailGenerator Tester.
 *
 * @author JiaYanwei
 * @since <pre>2016-01-28</pre>
 * @version 1.0
 */
public class ThumbnailGeneratorTest {



    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * 
     * Method: doThumbnail(String params)
     * 
     */
    @Test
    public void testDoThumbnail0() throws Exception {
        // jpeg; 640x480
        InputStream imageInputStream = getClass().getResourceAsStream("h7.jpg");
        ThumbnailGenerator generator = new ThumbnailGenerator(ImageIO.read(imageInputStream));
        imageInputStream.close();

        // thumbnails of the jpeg
        imageInputStream = getClass().getResourceAsStream("h7_160x120q90.jpg");
        byte[] img160x120q90 = IOUtils.toByteArray(imageInputStream);
        imageInputStream.close();
        imageInputStream = getClass().getResourceAsStream("h7_160x120q75.jpg");
        byte[] img160x120q75 = IOUtils.toByteArray(imageInputStream);
        imageInputStream.close();

        TargetImageGenerator thumbnail;

        Method method = generator.getClass().getDeclaredMethod("doThumbnail", String.class);
        method.setAccessible(true);

        thumbnail = (TargetImageGenerator) method.invoke(generator, "160x160");
        assertTrue(Arrays.equals(thumbnail.generate("jpg"), img160x120q90));

        thumbnail = (TargetImageGenerator) method.invoke(generator, "160x160q75");
        assertTrue(Arrays.equals(thumbnail.generate("jpg"), img160x120q75));

        // thumbnail: 800x600cRzq75.jpg
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        imageInputStream = getClass().getResourceAsStream("h7.jpg");
        Thumbnails.of(imageInputStream)
                .size(800, 600)
                .crop(Positions.CENTER_RIGHT)
                .outputQuality(0.75)
                .outputFormat("jpg")
                .toOutputStream(os);
        byte[] img800x600cRzq75 = os.toByteArray();
        imageInputStream.close();

        // 800x600cRzq75 => zoom & crop @center-right to 800x600 with quality 75%
        thumbnail = (TargetImageGenerator) method.invoke(generator, "800x600cRzq75");
        assertTrue(Arrays.equals(thumbnail.generate("jpg"), img800x600cRzq75));

        // - => 源图改格式
        imageInputStream = getClass().getResourceAsStream("h7_.png");
        byte[] img_ = IOUtils.toByteArray(imageInputStream);
        imageInputStream.close();
        thumbnail = (TargetImageGenerator) method.invoke(generator, "-");
        assertTrue(Arrays.equals(thumbnail.generate("png"), img_));

        imageInputStream = getClass().getResourceAsStream("h7_.jpg");
        img_ = IOUtils.toByteArray(imageInputStream);
        imageInputStream.close();
        thumbnail = (TargetImageGenerator) method.invoke(generator, "-");
        assertTrue(Arrays.equals(thumbnail.generate("jpg"), img_));

        // .-meta => 原图取 meta
        thumbnail = (TargetImageGenerator) method.invoke(generator, "-");
        Map<String, Integer> meta = JSON.parseObject(thumbnail.generate("meta"), HashMap.class);
        assertEquals(640, (int)meta.get("width"));
        assertEquals(480, (int)meta.get("height"));

        // .jpg-meta => 缩略图取 meta
        thumbnail = (TargetImageGenerator) method.invoke(generator, "160x160q75");
        meta = JSON.parseObject(thumbnail.generate("meta"), HashMap.class);
        assertEquals(160, (int)meta.get("width"));
        assertEquals(120, (int)meta.get("height"));
    }

    /**
     * 
     * Method: doThumbnail(ThumbnailParams tbParams)
     * 
     */
    @Test
    public void testDoThumbnail() throws Exception {
        // png; 195x195
        InputStream imageInputStream = getClass().getResourceAsStream("T1ztCTB5CT1R4cSCrK.png");
        ThumbnailGenerator generator = new ThumbnailGenerator(ImageIO.read(imageInputStream));
        imageInputStream.close();

        ThumbnailParams params;
        BufferedImage thumbnail;

        Method method = generator.getClass().getDeclaredMethod("doThumbnail", ThumbnailParams.class);
        method.setAccessible(true);

        // 160x120 => 120x120
        params = new ThumbnailParams.Builder().width(160).height(120).build();
        thumbnail = (BufferedImage) method.invoke(generator, params);
        assertEquals(120, thumbnail.getWidth());
        assertEquals(120, thumbnail.getHeight());

        // 600x120 => 120x120
        params = new ThumbnailParams.Builder().width(600).height(120).build();
        thumbnail = (BufferedImage)method.invoke(generator, params);
        assertEquals(120, thumbnail.getWidth());
        assertEquals(120, thumbnail.getHeight());

        // 160x800 => 160x160
        params = new ThumbnailParams.Builder().width(160).height(800).build();
        thumbnail = (BufferedImage)method.invoke(generator, params);
        assertEquals(160, thumbnail.getWidth());
        assertEquals(160, thumbnail.getHeight());

        // 600x800; not zoom bigger => 195x195
        params = new ThumbnailParams.Builder().width(600).height(800).zoomBigger(false).build();
        thumbnail = (BufferedImage)method.invoke(generator, params);
        assertEquals(195, thumbnail.getWidth());
        assertEquals(195, thumbnail.getHeight());

        // 600x800; zoom bigger => 600x600
        params = new ThumbnailParams.Builder().width(600).height(800).zoomBigger(true).build();
        thumbnail = (BufferedImage)method.invoke(generator, params);
        assertEquals(600, thumbnail.getWidth());
        assertEquals(600, thumbnail.getHeight());

        // 160 => 160x160
        params = new ThumbnailParams.Builder().width(160).build();
        thumbnail = (BufferedImage) method.invoke(generator, params);
        assertEquals(160, thumbnail.getWidth());
        assertEquals(160, thumbnail.getHeight());

        // x120 => 120x120
        params = new ThumbnailParams.Builder().height(120).build();
        thumbnail = (BufferedImage) method.invoke(generator, params);
        assertEquals(120, thumbnail.getWidth());
        assertEquals(120, thumbnail.getHeight());

        // 600 => 600x600
        params = new ThumbnailParams.Builder().width(600).zoomBigger(true).build();
        thumbnail = (BufferedImage) method.invoke(generator, params);
        assertEquals(600, thumbnail.getWidth());
        assertEquals(600, thumbnail.getHeight());

        // x800 => 800x800
        params = new ThumbnailParams.Builder().height(800).zoomBigger(true).build();
        thumbnail = (BufferedImage) method.invoke(generator, params);
        assertEquals(800, thumbnail.getWidth());
        assertEquals(800, thumbnail.getHeight());

        // 600 => 195x195
        params = new ThumbnailParams.Builder().width(600).zoomBigger(false).build();
        thumbnail = (BufferedImage) method.invoke(generator, params);
        assertEquals(195, thumbnail.getWidth());
        assertEquals(195, thumbnail.getHeight());

        // x800 => 195x195
        params = new ThumbnailParams.Builder().height(800).zoomBigger(false).build();
        thumbnail = (BufferedImage) method.invoke(generator, params);
        assertEquals(195, thumbnail.getWidth());
        assertEquals(195, thumbnail.getHeight());

        // 160x120; crop => 160x120
        params = new ThumbnailParams.Builder().width(160).height(120).crop(Positions.TOP_CENTER).build();
        thumbnail = (BufferedImage) method.invoke(generator, params);
        assertEquals(160, thumbnail.getWidth());
        assertEquals(120, thumbnail.getHeight());

        // 600x800; crop & not zoom bigger => 195x195
        params = new ThumbnailParams.Builder()
                .width(600).height(800).zoomBigger(false).crop(Positions.BOTTOM_RIGHT).build();
        thumbnail = (BufferedImage)method.invoke(generator, params);
        assertEquals(195, thumbnail.getWidth());
        assertEquals(195, thumbnail.getHeight());
    }

    /**
     * 连续调用两次 testDoThumbnail
     */
    @Test
    public void testDoThumbnail_calltwice() throws Exception {
        // png; 195x195
        ThumbnailGenerator generator = new ThumbnailGenerator(ImageIO.read(getClass().getResourceAsStream("T1ztCTB5CT1R4cSCrK.png")));

        Method method = generator.getClass().getDeclaredMethod("doThumbnail", ThumbnailParams.class);
        method.setAccessible(true);
        ThumbnailParams params = new ThumbnailParams.Builder().width(160).height(120).build();
        BufferedImage thumbnail = (BufferedImage)method.invoke(generator, params);
        method.invoke(generator, params);
        assertEquals(120, thumbnail.getWidth());
        assertEquals(120, thumbnail.getHeight());
    }
    

    /**
     * 
     * Method: parseThumbnailParams(String params)
     * 
     */
    @Test
    public void testParseThumbnailParams() throws Exception {
        ThumbnailGenerator generator = new ThumbnailGenerator(null);

        Method method = generator.getClass().getDeclaredMethod("parseThumbnailParams", String.class);
        method.setAccessible(true);

        final ThumbnailParams PARAMS = new ThumbnailParams.Builder().build();

        // simple 160x120
        ThumbnailParams params = (ThumbnailParams) method.invoke(generator, "160x120");
        assertEquals(160, params.width());
        assertEquals(120, params.height());
        assertEquals(PARAMS.crop(), params.crop());
        assertEquals(PARAMS.zoomBigger(), params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());

        // 160 => only width 160
        params = (ThumbnailParams) method.invoke(generator, "160");
        assertEquals(160, params.width());
        assertEquals(PARAMS.height(), params.height());
        assertEquals(PARAMS.crop(), params.crop());
        assertEquals(PARAMS.zoomBigger(), params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());

        // x120 => only height 120
        params = (ThumbnailParams) method.invoke(generator, "x120");
        assertEquals(PARAMS.width(), params.width());
        assertEquals(120, params.height());
        assertEquals(PARAMS.crop(), params.crop());
        assertEquals(PARAMS.zoomBigger(), params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());
        
        // 800x600z => zoom to 800x600
        params = (ThumbnailParams) method.invoke(generator, "800x600z");
        assertEquals(800, params.width());
        assertEquals(600, params.height());
        assertEquals(PARAMS.crop(), params.crop());
        assertEquals(true, params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());

        // 800z => zoom to 800x...
        params = (ThumbnailParams) method.invoke(generator, "800z");
        assertEquals(800, params.width());
        assertEquals(PARAMS.width(), params.height());
        assertEquals(PARAMS.crop(), params.crop());
        assertEquals(true, params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());

        // x600z => zoom to ...x600
        params = (ThumbnailParams) method.invoke(generator, "x600z");
        assertEquals(PARAMS.width(), params.width());
        assertEquals(600, params.height());
        assertEquals(PARAMS.crop(), params.crop());
        assertEquals(true, params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());
        
        // 800x600q60 => quality 60%
        params = (ThumbnailParams) method.invoke(generator, "800x600q60");
        assertEquals(800, params.width());
        assertEquals(600, params.height());
        assertEquals(PARAMS.crop(), params.crop());
        assertEquals(PARAMS.zoomBigger(), params.zoomBigger());
        assertEquals(60, params.quality());

        // 160x120c => resize & crop @center to 160x120
        params = (ThumbnailParams) method.invoke(generator, "160x120c");
        assertEquals(160, params.width());
        assertEquals(120, params.height());
        assertEquals(Positions.CENTER, params.crop());
        assertEquals(PARAMS.zoomBigger(), params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());

        // 160x120cT => resize & crop @top-center to 160x120
        params = (ThumbnailParams) method.invoke(generator, "160x120cT");
        assertEquals(160, params.width());
        assertEquals(120, params.height());
        assertEquals(Positions.TOP_CENTER, params.crop());
        assertEquals(PARAMS.zoomBigger(), params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());

        // 160x120cBL => resize & crop @bottom-left to 160x120
        params = (ThumbnailParams) method.invoke(generator, "160x120cBL");
        assertEquals(160, params.width());
        assertEquals(120, params.height());
        assertEquals(Positions.BOTTOM_LEFT, params.crop());
        assertEquals(PARAMS.zoomBigger(), params.zoomBigger());
        assertEquals(PARAMS.quality(), params.quality());

        // 800x600cRzq75 => zoom & crop @center-right to 800x600 with quality 75%
        params = (ThumbnailParams) method.invoke(generator, "800x600cRzq75");
        assertEquals(800, params.width());
        assertEquals(600, params.height());
        assertEquals(Positions.CENTER_RIGHT, params.crop());
        assertEquals(true, params.zoomBigger());
        assertEquals(75, params.quality());
    }
}
