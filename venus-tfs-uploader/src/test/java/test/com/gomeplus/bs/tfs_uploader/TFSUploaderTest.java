package test.com.gomeplus.bs.tfs_uploader;

import com.gomeplus.bs.tfs_uploader.TFSUploader;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * TFSUploader Tester.
 *
 * @author Jiayanwei
 * @version 1.0
 * @since 2016-09-08
 */
public class TFSUploaderTest extends TestCase {
    public TFSUploaderTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Method: uploadFile(InputStream in)
     */
    public void testUploadFile() throws Exception {
        //TODO: Test goes here...
        InputStream in1 = TFSUploaderTest.class.getClassLoader().getResourceAsStream("Java_logo.png");
        String tfsFileName1 = TFSUploader.uploadFile(in1);
        System.out.println("upload without suffix: " + tfsFileName1);
        assertTrue(Pattern.compile("T1[0-9a-zA-Z\\._]{16}").matcher(tfsFileName1).matches());
        in1.close();

        InputStream in2 = TFSUploaderTest.class.getClassLoader().getResourceAsStream("Java_logo.png");
        String tfsFileName2 = TFSUploader.uploadFile(in2, ".png");
        System.out.println("upload with suffix: " + tfsFileName2);
        assertTrue(Pattern.compile("T1[0-9a-zA-Z\\._]{16}.png").matcher(tfsFileName2).matches());
        in2.close();
    }

    public void testTranslate() throws Exception {
        String url = "http://wx.qlogo.cn/mmopen/ajNVdqHZLLBQS7ibTREdDqLk5hNcsaxEspNcodwCWrNYFR9PfR9qS7gTq98zFcuxwTBia" +
                "FUyiajia6HfUCIJdT3Kicw/0";
        String tfsFileName = TFSUploader.transfer(url);
        System.out.println("transfer: " + tfsFileName);
        assertTrue(Pattern.compile("T1[0-9a-zA-Z\\._]{16}").matcher(tfsFileName).matches());
    }


    public static Test suite() {
        return new TestSuite(TFSUploaderTest.class);
    }
} 
