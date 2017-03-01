package cn.com.mx.webapi.common.filter;

import cn.com.mx.webapi.common.model.PublicParams;
import cn.com.mx.webapi.common.service.ResponseProcessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/8/4 9:57
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/venus-spring_test.xml"})
public class PublicParamFilterTest {
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private PublicParamFilter filter ;
    @Autowired
    private ResponseProcessor responseProcessor;
    @Before
    public void init(){
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        filter = new PublicParamFilter();
    }
    @Test
    public void addTraceIdTest() throws NoSuchMethodException {

        PublicParams modle = new PublicParams();
        modle.setApp("001/12");
        mockRequest.setAttribute("publicParams", modle);

        Class clazz = filter.getClass();
        Method addTraceId = clazz.getDeclaredMethod("addTraceId", HttpServletRequest.class, HttpServletResponse.class);
        addTraceId.setAccessible(true);
        try {
            addTraceId.invoke(filter,mockRequest,mockResponse);
            Assert.assertNotNull(mockResponse.getHeader("x-gomeplus-trace-id"));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void useTraceIdTest() throws NoSuchMethodException {

        PublicParams modle = new PublicParams();
        modle.setApp("001/12");
        modle.setTraceId("4343CE66BD5C4A3F8519DC8E15B65FB5");
        mockRequest.setAttribute("publicParams", modle);
        PublicParamFilter filter = new PublicParamFilter();
        Class clazz = filter.getClass();
        Method addTraceId = clazz.getDeclaredMethod("addTraceId", HttpServletRequest.class, HttpServletResponse.class);
        addTraceId.setAccessible(true);
        try {
            addTraceId.invoke(filter,mockRequest,mockResponse);
            Assert.assertEquals("4343CE66BD5C4A3F8519DC8E15B65FB5",mockResponse.getHeader("x-gomeplus-trace-id"));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void doFilterTest(){
        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                System.out.println("filter chain");
            }
        };
        mockRequest.setParameter("accept","application/json");
        try {

            filter.doFilter(mockRequest,mockResponse,chain);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
