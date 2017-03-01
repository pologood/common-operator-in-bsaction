package com.gomeplus.bs.framework.dubbor;

import com.gomeplus.bs.framework.dubbor.filter.CommonFilter;
import org.hamcrest.core.StringContains;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 2016/12/30
 * @author  zhaozhou
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;


    RequestBuilder request = null;

    /**
     * 如果只是对某个controller测试，可以写下面方式，但是这将不走拦截器等mvc扩展
     * 且上面的注解写成：@SpringBootTest(classes = MockServletContext.class)
     */
//    @Before
//    public void setUp() throws Exception {
//        mvc = MockMvcBuilders.standaloneSetup(new BaseTestResource()).build();
//    }
    @Before
    public void setUp() throws Exception {
        CommonFilter commonFilter = new CommonFilter();
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(commonFilter, "/*")
                .build();

    }

    @Test
    public void forbiddenTest() throws Exception {
        // 测试UserController

        request = post("/test/test");
        mvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(content().string(equalTo("{\"message\":\"资源不可用\",\"data\":{}}")));


    }
    @Test
    public void unEncapsulateTest() throws Exception {
        request = get("/test/testUnEncapsulate");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"username\":\"henry\"}")));
    }

    @Test
    public void paramCheckTest() throws Exception {
        request = get("/test/test");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{\"username\":\"henry\"}}")));
    }
    @Test
    public void publicParams() throws Exception {
        request = get("/test/publicParams/long?userId=1");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{}}")));

        request = get("/test/publicParams/int?userId=1");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{}}")));

        request = get("/test/publicParams/int?userId=zhadf");
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(StringContains.containsString("{\"message\":\"参数校验失败\",\"data\":{},\"debug\":\"参数校验失败")));

        request = get("/test/publicParams/string?userId=asdfaf");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{}}")));

        request = get("/test/publicParams/require");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{}}")));
    }

    @Test
    public void actuatorTest() throws Exception {
        request = get("/health");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.status", notNullValue()));
    }

    @Test
    public void returnNullTest() throws Exception {
        request = get("/test/testReturnNull");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{}}")));
    }

    @Test
    public void postTest() throws Exception {
        //language=JSON
        String user = "{\"username\":\"zhaozhou\",\"password\":\"123\"}";

        request = post("/test/post").content(user)
                .contentType("application/json;charset=UTF-8");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{\"username\":\"zhaozhou\",\"password\":\"123\"}}")));

    }
    @Test
    public void putTest() throws Exception {
        //language=JSON
        String user = "{\"username\":\"zhaozhou\",\"password\":\"123\"}";

        request = put("/test/put?id=1").content(user)
                .contentType("application/json;charset=UTF-8");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{\"username\":\"zhaozhou\",\"password\":\"123\"}}")));

    }

    @Test
    public void exceptionTest() throws Exception {

        request = put("/test/testException?id=1");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{}}")));

        request = get("/test/testException?userId=1");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"message\":\"\",\"data\":{\"userId\":1}}")));

        request = get("/test/testException");
        mvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(StringContains.containsString("\"message\":\"参数校验失败\",\"data\":{},\"debug\":\"参数校验失败 [public [userId] param is null]")));

    }
    // TODO 404 错误页面开发
    @Test
    public void missingPathTest() throws Exception {

        request = get("/miss/miss?id=122");
        mvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string(StringContains.containsString("")));

    }
    @Test
    public void staticFileTest() throws Exception {

        request = get("/index.html");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<h1>Bubbor Test</h1>")));

        request = get("/cmd/welcome.html");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("<h1>Bubbor Welcome</h1>")));

    }
}
