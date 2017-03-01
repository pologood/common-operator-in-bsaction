package cn.com.mx.webapi.common.servlet;

import cn.com.mx.webapi.common.utils.ResourceParameters;
import org.springframework.stereotype.Controller;

import javax.servlet.annotation.WebServlet;

/**
 * @Description
 * @author zhaozhou
 * @date 2016/8/4 11:00
 */
@WebServlet("/demo/test")
@Controller
public class DemoResources  extends BaseResource{
    public Object get(ResourceParameters resourceParameters){
        return "";
    }
}
