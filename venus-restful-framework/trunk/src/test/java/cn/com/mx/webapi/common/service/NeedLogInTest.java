package cn.com.mx.webapi.common.service;

import cn.com.mx.webapi.common.annotation.LoggedIn;
import cn.com.mx.webapi.common.annotation.LoggedInProcessor;
import cn.com.mx.webapi.common.filter.PublicParamFilter;
import cn.com.mx.webapi.common.model.NeedLogInEnum;
import cn.com.mx.webapi.common.servlet.BaseResource;
import cn.com.mx.webapi.common.utils.ResourceParameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/8/1 18:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:config/venus-spring_test.xml"})
public class NeedLogInTest {

    @Autowired
    private CheckService checkService;


    @Autowired
    private RequestProcessor requestProcessor;
    MockHttpServletRequest mockRequest;
    MockHttpServletResponse mockResponse;
    PublicParamFilter filter ;

    @Before
    public void init(){
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        filter = new PublicParamFilter();
    }

    @Test
    public void needLogIn() throws NoSuchMethodException {
        NeedLogInEnum needLogInEnum= LoggedInProcessor.needLogIn(new NeedLogInTest(),"test1",String.class);
        Assert.assertEquals(needLogInEnum,NeedLogInEnum.YES);
        needLogInEnum= LoggedInProcessor.needLogIn(new NeedLogInTest(),"test2",String.class);
        Assert.assertEquals(needLogInEnum,NeedLogInEnum.NO);
        needLogInEnum= LoggedInProcessor.needLogIn(new NeedLogInTest(),"test3",String.class);
        Assert.assertEquals(needLogInEnum,NeedLogInEnum.OPTIONAL);
    }
    @LoggedIn
    public void test1(String str){
        System.out.println("");
    }
    public void test2(String str){
        System.out.println("");
    }
    @LoggedIn(optional = true)
    public void test3(String str){
        System.out.println("");
    }


    @Test
    public void checkLogTest()
    {
        BaseResource baseResource = new BaseResource() {
            @Override
            public void init(ServletConfig config) throws ServletException {
                super.init(config);
            }
            @LoggedIn
            public Object get(){
                return "";
            }
        };
        mockRequest.setParameter("id","117");
        mockRequest.setParameter("accept","application/json");
        mockRequest.setContentType("application/json;charset=utf-8");
        mockRequest.setContent("".getBytes());
        mockRequest.setParameter("userId","117");
        mockRequest.setParameter("loginToken","fadfasdfasr3we");
        ResourceParameters resourceParameters = requestProcessor.getParameter(mockRequest);
        resourceParameters.put("publicParams",resourceParameters.get("requestEntity"));
//        publicParams
//        checkService.checkLogin(resourceParameters,baseResource,"get");
    }
}
