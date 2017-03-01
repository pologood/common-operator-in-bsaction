package com.gomeplus.bs.thumbnail;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * ResponseAsyncCompletionHandler Tester.
 *
 * @author jiayanwei
 * @version 1.0
 * @since <pre>2016-09-22</pre>
 */
public class ResponseAsyncCompletionHandlerTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: onCompleted(Response response)
     */
    @Test
    public void testOnCompleted() throws Exception {
    }

    /**
     * Method: onThrowable(Throwable t)
     */
    @Test
    public void testOnThrowable() throws Exception {
    }


    /**
     * Method: doResponse(Response response)
     */
    @Test
    public void testDoResponse() throws Exception {
        /*
        try {
           Method method = ResponseAsyncCompletionHandler.getClass().getMethod("doResponse", Response.class);
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
    }

    /**
     * Method: buildResponse(byte[] buf, String mimeType)
     */
    @Test
    public void testBuildResponse() throws Exception {
        /*
        try {
           Method method = ResponseAsyncCompletionHandler.getClass().getMethod("buildResponse",
                byte[].class, String.class);
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
    }

    /**
     * Method: getImageRequests()
     */
    @Test
    public void testGetImageRequests() throws Exception {
        /*
        try {
           Method method = ResponseAsyncCompletionHandler.getClass().getMethod("getImageRequests");
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
    }

    /**
     * Method: doErrorResponse(String msg)
     */
    @Test
    public void testDoErrorResponse() throws Exception {
        /*
        try {
           Method method = ResponseAsyncCompletionHandler.getClass().getMethod("doErrorResponse", String.class);
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
    }

    /**
     * Method: buildErrorResponse(String msg)
     */
    @Test
    public void testBuildErrorResponse() throws Exception {
        /*
        try {
           Method method = ResponseAsyncCompletionHandler.getClass().getMethod("buildErrorResponse", String.class);
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
    }

    /**
     * Method: buildOKResponse(byte[] buf, String mimeType)
     */
    @Test
    public void testBuildOKResponse() throws Exception {
        /*
        try {
           Method method = ResponseAsyncCompletionHandler.getClass().getMethod("buildOKResponse",
                byte[].class, String.class);
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
    }

    /**
     * Method: writeResponse(ChannelHandlerContext ctx, FullHttpResponse response)
     */
    @Test
    public void testWriteResponse() throws Exception {
        /*
        try {
           Method method = ResponseAsyncCompletionHandler.getClass().getMethod("writeResponse",
                ChannelHandlerContext.class, FullHttpResponse.class);
           method.setAccessible(true);
           method.invoke(<Object>, <Parameters>);
        } catch(NoSuchMethodException e) {
        } catch(IllegalAccessException e) {
        } catch(InvocationTargetException e) {
        }
        */
    }

    /**
     * Method: getMIMEType(String suffix)
     */
    @Test
    public void testGetMIMEType() throws Exception {
        final Class<?> clazz = Class.forName("com.gomeplus.bs.thumbnail.ResponseAsyncCompletionHandler");
        Method method = clazz.getDeclaredMethod("getMIMEType", String.class);
        method.setAccessible(true);
        assertEquals("image/jpeg", method.invoke(null, "jpg"));
        assertEquals("image/png", method.invoke(null, "png"));
        assertEquals("image/bmp", method.invoke(null, "bmp"));
        assertEquals("image/gif", method.invoke(null, "gif"));
        assertEquals("application/json", method.invoke(null, "meta"));
    }
}
