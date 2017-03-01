package cn.com.mx.webapi.common.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import cn.com.mx.webapi.common.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 匹配所有未发现的资源
 *              <p>
 *              url-pattern匹配规则和顺序：
 *              <p>
 *              1.精确路径匹配
 *              <p>
 *              2.最长路径匹配
 *              <p>
 *              3.扩展匹配
 *              <p>
 *              4.缺省匹配
 * @author wanggang-ds6
 * @date 2016年1月25日 上午10:25:20
 */
@WebServlet("/")
@Slf4j
public class NotFoundResource extends HttpServlet {

	private static final long serialVersionUID = -7992726432075272497L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	// public Object get(ResourceParameters resourceParameters) throws Exception
	// {
	// throw new C403Exception(BaseExceptionMessage.RESOURCE_FORBIDDEN);
	// }
	//
	// public Object post(ResourceParameters resourceParameters, JSONObject
	// body) throws Exception {
	// throw new C403Exception(BaseExceptionMessage.RESOURCE_FORBIDDEN);
	// }
	//
	// public Object put(ResourceParameters resourceParameters, JSONObject body)
	// throws Exception {
	// throw new C403Exception(BaseExceptionMessage.RESOURCE_FORBIDDEN);
	// }
	//
	// public Object delete(ResourceParameters resourceParameters) throws
	// Exception {
	// throw new C403Exception(BaseExceptionMessage.RESOURCE_FORBIDDEN);
	// }

	/**
	 * @Description 处理所有未发现的请求 ，返回403
	 * @author wanggang-ds6
	 * @date 2016年1月25日 上午10:31:08
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String url = req.getRequestURI();
		ResponseModel<Object> responseModel = new ResponseModel<Object>();
		responseModel.setMessage("资源不可用， 错误代码[403]");
		responseModel.setData(new Object());
		resp.setContentType("application/json;charset=utf-8");
		resp.setCharacterEncoding("UTF-8");
		log.info("Forbidden.错误代码[403],请求资源{}不存在.", url);
		resp.getWriter().write(JSONObject.toJSONString(responseModel));
		resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}
}
