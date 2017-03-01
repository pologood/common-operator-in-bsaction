package cn.com.mx.webapi.common.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.com.mx.webapi.common.constant.PublicParamsConstant;
import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;
import cn.com.mx.webapi.common.exceptions.code.C405Exception;
import cn.com.mx.webapi.common.exceptions.code.C422Exception;
import cn.com.mx.webapi.common.exceptions.code.C500Exception;
import cn.com.mx.webapi.common.model.ResponseModel;
import cn.com.mx.webapi.common.service.CheckService;
import cn.com.mx.webapi.common.service.RequestProcessor;
import cn.com.mx.webapi.common.service.ResponseProcessor;
import cn.com.mx.webapi.common.utils.ParametersPrinter;
import cn.com.mx.webapi.common.utils.ResourceParameters;
import cn.com.mx.webapi.common.utils.ValidateJsonUtil;
import cn.com.mx.webapi.common.utils.ValidateProLoadService;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wanggang-ds6
 * @Description 控制层基类
 * @date 2016年1月22日 上午11:06:38
 */
@Slf4j
public abstract class BaseResource extends HttpServlet {

	@Autowired
	private CheckService checkService;

	@Autowired
	ValidateProLoadService validateProLoadService;

	@Autowired
	private RequestProcessor requestProcessor;

	@Autowired
	private ResponseProcessor responseProcessor;

	// wyp 2016-03-31 validate param
	// @Autowired
	// protected ConfigCenter configCenter;
	// protected static final String KEY_PATH = "validpath.";
	// protected static final String KEY_REGULAR = "validreg.";
	protected static final String OPTIONAL = "^\\[\\w+\\]$";
	protected static final String MIDBRACKETS = "\\[|\\]";

	/**
	 * @author wanggang-ds6
	 * @Description 方法枚举
	 * @date 2016年1月22日 上午11:06:54
	 */
	public enum MethodEnum {
		get, post, put, delete;
	}

	private static final long serialVersionUID = -4555021483800227968L;

	/**
	 * @param config
	 * @throws ServletException
	 * @Description 初始化
	 * @author wanggang-ds6
	 * @date 2016年1月22日 上午11:06:08
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// 用于支持自动注入
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		AutowireCapableBeanFactory factory = ctx.getAutowireCapableBeanFactory();
		factory.autowireBean(this);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp, MethodEnum.get);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp, MethodEnum.delete);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp, MethodEnum.post);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp, MethodEnum.put);
	}

	/**
	 * @param req
	 *            请求
	 * @param resp
	 *            响应
	 * @param methodEnum
	 *            方法
	 * @Description 处理
	 * @author wanggang-ds6
	 * @throws ServletException
	 * @throws IOException
	 * @throws Exception
	 * @date 2016年1月22日 上午11:03:59
	 */
	private void process(HttpServletRequest req, HttpServletResponse resp, MethodEnum methodEnum)
			throws ServletException, IOException {
		// 校验数据
		requestProcessor.beforeProcess(req, methodEnum);
		
		
		// 解析请求
		ResourceParameters servletParam = requestProcessor.getParameter(req);
		String jsonStr = servletParam.getString(PublicParamsConstant.REQUEST_BODY);
		JSONObject jsonBody = JSONObject.parseObject(jsonStr);// 获得requestBody的json对象。

		// String path = req.getServletPath(); //venus-account-servlet

		// 解析URL
		String uri = req.getRequestURI();
		String[] uris = parseUrl(uri);
		String module = uris[0];
		String resource = uris[1];
		String method = req.getMethod();// get post put delete
		try {
			// 添加入参校验，待完善。
			validateServletParam(servletParam, module, resource, method);
			validateJsonStr(jsonStr, module, resource, method);

			// 记录参数
			ParametersPrinter.setRequestBody(jsonBody);
			ParametersPrinter.setRequestParam(servletParam);

			Object rtnMap = null;
			if (methodEnum == MethodEnum.get) {
				checkService.checkLogin(servletParam, this, "get", ResourceParameters.class);
				rtnMap = get(servletParam);
			} else if (methodEnum == MethodEnum.post) {
				checkService.checkLogin(servletParam, this, "post", ResourceParameters.class, JSONObject.class);
				rtnMap = post(servletParam, jsonBody);
			} else if (methodEnum == MethodEnum.put) {
				checkService.checkLogin(servletParam, this, "put", ResourceParameters.class, JSONObject.class);
				rtnMap = put(servletParam, jsonBody);
			} else if (methodEnum == MethodEnum.delete) {
				checkService.checkLogin(servletParam, this, "delete", ResourceParameters.class);
				rtnMap = delete(servletParam);
			}
			ResponseModel<Object> responseModel = new ResponseModel<Object>(rtnMap);
			// 处理应答
			responseProcessor.process(req, resp, responseModel);
		} catch (Exception e) {
			// 异常处理。由于这儿只能跑出httpservletException，所以需要在此处处理异常。无法给filter处理
			responseProcessor.modifyExceptionResponse(req, resp, e);
		} finally {
			// 处理解析参数
			// 处理应答
			requestProcessor.afterProcess(jsonBody, servletParam);
		}
	}

	protected void validateJsonStr(String jsonStr, String module, String resource, String method) throws Exception {
		log.debug("validateJsonStr param is {}, {}, {}, {}", module, module, resource, method, jsonStr);
		if("GET".equals(method)||"DELETE".equals(method)){//GET，DELETE没有请求体，所有不需要校验
			return;
		}
		ValidateJsonUtil.validateJsonString(module, resource, method, jsonStr);
	}

	private void validateServletParam(ResourceParameters servletParam, String module, String resource, String method)
			throws Exception {
		log.debug("validateServletParam param is {}, {}, {}, {}", servletParam, module, module, resource, method);
		String resKey = resource + "." + method;
		String keytmp = null;
		try {
			keytmp = validateProLoadService.getValue(module, resKey, "");
		} catch (IllegalStateException e) {
			log.error("{} path is not valid!", resKey, e);
			return;
		}
		String[] keys = StringUtils.isEmpty(keytmp) ? null : keytmp.split(",");
		if (null == keys)
			return;
		String value = null;
		String regular = null;
		boolean isOption = false;// 是否可选
		for (String key : keys) {
			isOption = key.matches(OPTIONAL);
			if (isOption) {
				key = key.replaceAll(MIDBRACKETS, "");
			}

			regular = validateProLoadService.getValue(module, resource + "." + key, "");
			if (StringUtils.isEmpty(regular)) {
				throw new Exception("没有配置" + key + "的校验规则.");// 顶层会处理为500错误。
			}
			value = servletParam.getString(key);
			if (StringUtils.isEmpty(value)) {
				throw new C422Exception(BaseExceptionMessage.CHECK_PARAMDATA_FAILED.setArgs(key + "值为空"));
			}
			if (!value.matches(regular)) {
				throw new C422Exception(
						BaseExceptionMessage.CHECK_PARAMDATA_FAILED.setArgs(key + "值为" + value + ",而校验规则为" + regular));
			}
		}
	}

	/**
	 * 解析URI获取模块名、资源名
	 * 
	 * @param requestURI
	 * @return [0]moduleName, [1]resourceName
	 */
	private String[] parseUrl(String requestURI) {
		String[] tmp = requestURI.split("/");
		log.debug("requestURI is {}", tmp);
		if (null == tmp || tmp.length < 3) {
			throw new C422Exception(new BaseExceptionMessage("request uri error [{}]!", requestURI));
		} else {
			return new String[] { tmp[tmp.length - 2], tmp[tmp.length - 1] };
		}
	}
	/*
	 * protected void validateJsonBody(JSONObject jsonBody, String path) {
	 * log.debug("validateJsonBody param is {}, {}", jsonBody, path);
	 * 
	 * }
	 * 
	 * 
	 * private void validateJsonStr(String jsonStr) { //JsonNode jsonSchema =
	 * JsonLoader.fromResource("/json/custom-fmt.json"); }
	 * 
	 * protected void validateServletParam(ResourceParameters servletParam,
	 * String path) { log.debug("validateServletParam param is {}, {}",
	 * servletParam, path); String pathtmp = path.substring(1); String keytmp =
	 * null; try { keytmp = configCenter.get(KEY_PATH + pathtmp); } catch
	 * (IllegalStateException e) { log.error("{} path is not valid!",pathtmp,
	 * e); return; } String[] keys = StringUtils.isEmpty(keytmp) ? new
	 * String[]{""} : keytmp.split(","); String value = null; String regular =
	 * null; boolean isOption = false;//是否可选 for (String key : keys) { isOption
	 * = key.matches(OPTIONAL); regular = configCenter.get(KEY_REGULAR + key);
	 * if (isOption) { value = servletParam.getString(key); if
	 * (StringUtils.isEmpty(value)) { continue; } else { if
	 * (!value.matches(regular)) { throw new C422Exception(new
	 * BaseExceptionMessage("%s value is %s!", key, value)); } }
	 * 
	 * } else { value = servletParam.getString(key); if
	 * (StringUtils.isEmpty(value) || !value.matches(regular)) { throw new
	 * C422Exception(new BaseExceptionMessage("%s value is [%s]!", key, value));
	 * } } } }
	 */

	public Object get(ResourceParameters resourceParameters) throws Exception {
		throw new C405Exception();
	}

	public Object post(ResourceParameters resourceParameters, JSONObject body) throws Exception {
		throw new C405Exception();
	}

	public Object put(ResourceParameters resourceParameters, JSONObject body) throws Exception {
		throw new C405Exception();
	}

	public Object delete(ResourceParameters resourceParameters) throws Exception {
		throw new C405Exception();
	}

}
