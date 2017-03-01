package com.gomeplus.bs.framework.dubbor.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/12/12
 */
public class InternetProtocolTest {

    private MockMvc mockMvc;

    @Test
    public void getRemoteAddrTest(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        String remoteAddr = InternetProtocol.getRemoteAddr(request);
        Assert.assertEquals("127.0.0.1",remoteAddr);

        request = new MockHttpServletRequest();
        Assert.assertEquals(request.getRemoteAddr(),remoteAddr);

        request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For","127.0.0.1");
        remoteAddr = InternetProtocol.getRemoteAddr(request);
        Assert.assertEquals("127.0.0.1",remoteAddr);

        request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For","127.0.0.1,127.0.0.2");
        remoteAddr = InternetProtocol.getRemoteAddr(request);
        Assert.assertEquals("127.0.0.1",remoteAddr);

        request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For","127.0.0.1,127.0.0.2");
        remoteAddr = InternetProtocol.getRemoteAddr(request);
        Assert.assertEquals("127.0.0.1",remoteAddr);

        request = new MockHttpServletRequest();
        request.addHeader("X-Real-IP","10.125.31.218");
        remoteAddr = InternetProtocol.getRemoteAddr(request);
        Assert.assertEquals("10.125.31.218",remoteAddr);

        request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For","unknown");
        remoteAddr = InternetProtocol.getRemoteAddr(request);
        Assert.assertEquals(request.getRemoteAddr(),remoteAddr);
    }

}