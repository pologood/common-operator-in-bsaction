package test.com.gomeplus.bs.image_uploader;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import com.gomeplus.bs.image_uploader.ImageFormat;
import static junit.framework.TestCase.assertEquals;

/**
 * ImageFormat Tester.
 *
 * @author jiayanwei
 * @version 1.0
 * @since 2016-09-13
 */
public class ImageFormatTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: getImageSuffix(byte[] bytes)
     */
    @Test
    public void testGetImageSuffix() throws Exception {
        assertEquals(".jpg", ImageFormat.getImageSuffix(new byte[]{}));
        assertEquals(".gif", ImageFormat.getImageSuffix(new byte[]{(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte)0}));
    }
} 
