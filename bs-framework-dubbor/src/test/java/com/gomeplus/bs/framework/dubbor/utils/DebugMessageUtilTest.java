package com.gomeplus.bs.framework.dubbor.utils;

import com.gomeplus.bs.framework.dubbor.exceptions.code4xx.C422Exception;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/12
 */
public class DebugMessageUtilTest {

    @Test
    public void getDebugMessageTest1() {
        String msgLog = "null";
        try {
            String str = null;
            str.length();
        } catch (Exception e) {
            Assert.assertTrue(DebugMessageUtil.getDebugMessage(e).startsWith(msgLog));
        }
        msgLog = "参数校验失败 [id is empty],\n";
        try{
            throw new C422Exception("参数校验失败").debugMessage("id is empty");
        }catch (Exception e){
            Assert.assertTrue(DebugMessageUtil.getDebugMessage(e).startsWith(msgLog));
        }
    }
}