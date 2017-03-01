package com.gomeplus.bs.image_web;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author jiayanwei
 * Created on 2017/1/11.
 */
public class AbstractImageServlet extends HttpServlet {
    protected static void doJsonResponse(HttpServletResponse response, JSONObject json) throws IOException {
        PrintWriter out = response.getWriter();
        out.println(json.toString());
        out.flush();
        out.close();
    }

    protected static void setIoEncodingUTF8(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
    }
}
