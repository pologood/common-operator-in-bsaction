package test.com.gomeplus.bs.tfs_uploader;

import com.gomeplus.bs.tfs_uploader.TFSUploader;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by wangchangye on 2017/2/13.
 *
 * @Author wangchangye
 */


public class TFSdeleteTest {

    @Test
    public void delete() throws IOException {

        TFSUploader.delete("T1JaWTB_Cv1RCvBVdK","");
    }
    @Test
    public void refresh() throws IOException{
        TFSUploader.refreshCDN("https://i-pre.meixincdn.com/v1/img/T1atYTB4DT1RXrhCrK.jpg");
    }
}
