package cn.com.mx.webapi.common.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.com.mx.webapi.common.constant.CommonConstant;
import cn.com.mx.webapi.common.constant.PublicParamsConstant;
import cn.com.mx.webapi.common.constant.UrlConstant;
import cn.com.mx.webapi.common.exceptions.code.C301Exception;
import cn.com.mx.webapi.common.exceptions.code.C302Exception;
import cn.com.mx.webapi.common.model.PublicParams;
import cn.com.mx.webapi.common.service.ResponseProcessor;
import cn.com.mx.webapi.common.utils.ParametersPrinter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 公共过滤器 
 * @author zhaozhou 修改：BaseConstants-->CommonConstant
 * @date 2016年5月3日 上午9:55:14
 */
@Slf4j
public class PublicParamFilter extends PublicParamFilterABS implements Filter {
	
	@Autowired
	private ResponseProcessor responseProcessor;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// 用于支持自动注入 
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
	    AutowireCapableBeanFactory factory = ctx.getAutowireCapableBeanFactory();
	    factory.autowireBean(this);
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		// 开始计时
		ParametersPrinter.setStartTime(System.currentTimeMillis());
		
		HttpServletRequest hsReq = (HttpServletRequest)req;	
		HttpServletResponse hsResp = (HttpServletResponse)resp;		
		
		// 记录请求地址
		ParametersPrinter.setRequestUrl(hsReq.getRequestURI());
		try {
			this.publicParam(req);
			//添加请求跟踪号
			addTraceId(hsReq, hsResp);

			//公参校验
			this.checkPublicParams(hsReq);
			chain.doFilter(req, resp);
			// 记录应答状态
			ParametersPrinter.setResponseCode(hsResp.getStatus());
		} catch (C301Exception | C302Exception c301e) {
			responseProcessor.modifyRedirectResponse(hsReq, hsResp, c301e);
		} catch (Exception e) {
			responseProcessor.modifyExceptionResponse(hsReq, hsResp, e);
		}
		// 结束计时
		ParametersPrinter.setEndTime(System.currentTimeMillis());
		// 输出参数信息
		String pwdUri = UrlConstant.getPwdUrl(((HttpServletRequest)req).getRequestURI());
		if(null != pwdUri){
			// 登录特殊处理，不打印password
			log.debug(ParametersPrinter.toStringWithoutPassword(true, UrlConstant.passwordUrlMap.get(pwdUri)));
		} else {
			log.debug(ParametersPrinter.toString(true));
		}		
		removeTraceId();
	}

	/**
	 * @Description 增加每次Traceid 
	 * @date 2016年1月22日 下午3:09:04
	 */
	private void addTraceId(HttpServletRequest req, HttpServletResponse resp) {
		
		PublicParams model = (PublicParams) req.getAttribute(PublicParamsConstant.PUBLIC_PARAMS_NAME);
		String traceId=model.getTraceId();
//		String traceId = model.getTraceId()!=null?model.getTraceId():req.getHeader(PublicParamsConstant.X_GOMEPLUS_TRACE_ID);
		if (traceId == null || traceId.trim().length() == 0) {
			// 创建traceId
			traceId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		}	
		// 设置请求id
		MDC.put(CommonConstant.LOG_SESSION_ID, traceId);
		
		// 将id放到相应头
		resp.setHeader(PublicParamsConstant.X_GOMEPLUS_TRACE_ID, MDC.get(CommonConstant.LOG_SESSION_ID));
	}

	/**
	 * @Description 删除每次Traceid 
	 * @author wanggang-ds6
	 * @date 2016年1月22日 下午3:09:20
	 */
	private void removeTraceId() {
		// 删除请求id
		MDC.remove(CommonConstant.LOG_SESSION_ID);
	}
	

}
