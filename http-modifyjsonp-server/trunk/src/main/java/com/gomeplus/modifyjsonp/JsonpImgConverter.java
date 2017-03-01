package com.gomeplus.modifyjsonp;

import com.gomeplus.modifyjsonp.config.Config;
import com.gomeplus.modifyjsonp.config.ConfigUtil;
import com.gomeplus.venus.common.imageutil.IllegalExpressionException;
import com.gomeplus.venus.common.imageutil.UrlConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gomeplus.modifyjsonp.ResponseAsyncCompletionHandler.*;

/**
 * Created by xue on 11/25/16.
 */
@Slf4j
public class JsonpImgConverter {
    static String convert(String line) {
        StringBuffer sb = new StringBuffer();
        UrlConverter urlConverter;
        if(ConfigUtil.getProperty("MX_URLPREFIX").contains(CONS_PRE)){
            urlConverter = new UrlConverter(PRE_PREFIX, new String[]{CONS_I_PRE});
        }else{
            urlConverter = new UrlConverter(PRO_PREFIX);
        }
        //获取img的原域名 pre: atguat.net.cn  pro： gomein.net.cn
        String originGomeImgHost = ConfigUtil.getProperty("originGomeImgHost");

        Pattern p  = Pattern.compile("gome-src=['|\"](.*?)['|\"]");
        Pattern p1 = Pattern.compile("http://img[0-9]{1,2}\\."+ originGomeImgHost);
        Pattern p2 = Pattern.compile("http://gfs[0-9]{1,2}\\."+ originGomeImgHost);
        Matcher m  = p.matcher(line);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            String originImgUrl = line.substring(mr.start(1), mr.end(1));
            // 0, 判断是否http开头  //开头代表协议无关
            if (originImgUrl.charAt(0) == '/' && originImgUrl.charAt(1) == '/') continue;

            Matcher m1 = p1.matcher(originImgUrl);
            Matcher m2 = p2.matcher(originImgUrl);
            // 若未匹配成功则原URL不变
            String newImgUrl = originImgUrl;
            try {
                if (m1.find()) {
                    newImgUrl = urlConverter.getHashedGomeOnlineImgLocation(originImgUrl);
                } else if (m2.find()) {
                    newImgUrl = urlConverter.getHashedGomeOnlineLocation(originImgUrl);
                }
            } catch(IllegalExpressionException ex) {
                log.info("exception " + Long.toString(Thread.currentThread().getId()));
                ex.printStackTrace();
            }

            m.appendReplacement(sb, "gome-src='"+ newImgUrl + "'");
        }
        m.appendTail(sb);
        // 3, grey.gif 全替换
        String newHtml = sb.toString().replaceAll("http://app.gome.com.cn/images/grey.gif", "https://i3.meixincdn.com/T16aCTB_Kv1R4n9VrK");
        return newHtml;
    }
}
