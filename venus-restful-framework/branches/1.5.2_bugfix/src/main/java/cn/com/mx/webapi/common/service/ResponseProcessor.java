package cn.com.mx.webapi.common.service;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import cn.com.mx.webapi.common.constant.PublicParamsConstant;
import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;
import cn.com.mx.webapi.common.exceptions.code.BaseCodeException;
import cn.com.mx.webapi.common.exceptions.code.C301Exception;
import cn.com.mx.webapi.common.exceptions.code.C302Exception;
import cn.com.mx.webapi.common.model.PublicParams;
import cn.com.mx.webapi.common.model.ResponseModel;
import cn.com.mx.webapi.common.utils.ParametersPrinter;
import io.terminus.ecp.config.center.ConfigCenter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 应答处理 
 * @author wanggang-ds6
 * @date 2016年1月27日 上午11:55:34
 */
@Slf4j
@Component
public class ResponseProcessor {
		
	@Autowired
	private ConfigCenter configCenter; 
	
	/**
	 * @Description 拼接应答 
	 * @author wanggang-ds6
	 * @date 2016年1月27日 下午12:00:16
	 * @param req
	 * @param resp
	 * @param responseModel
	 * @throws IOException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void process(HttpServletRequest req, HttpServletResponse resp, ResponseModel responseModel) throws IOException {
		PublicParams model = (PublicParams) req.getAttribute(PublicParamsConstant.PUBLIC_PARAMS_NAME);	
		String accept = null;
		if (model != null) {
			accept = model.getAccept();
		}
		resp.setCharacterEncoding("UTF-8");
		if(responseModel.getData()==null){
			responseModel.setData(new Object());
		}
		if (accept != null && accept.indexOf(PublicParamsConstant.ACCEPT_TYPE_JAVASCRIPT) > -1) {
			resp.setHeader("Content-type", "application/javascript");	
//			responseModel.setStatus(resp.getStatus());
			resp.setStatus(200);
			resp.getWriter().write(model.getJsonp() + "(" + JSONObject.toJSONString(responseModel) + ")");
		} else {
			resp.setHeader("Content-type", "application/json;");
			SerializeWriter out = new SerializeWriter();
			JSONSerializer serializer = new JSONSerializer(out);
			serializer.getNameFilters().add(FastjsonNameFilter.nameFilter);
			serializer.write(responseModel);
			String retJson = out.toString();
			// String retJson = JSONObject.toJSONString(responseModel);
			log.debug("JsonString is {}", retJson);
			resp.getWriter().write(retJson);
		}
	
		// 记录结果
		ParametersPrinter.setResponseModel(responseModel);
		ParametersPrinter.setResponseCode(resp.getStatus());
	}
	
	/**
	 * @Description 设置重定向返回信息 
	 * @author wanggang-ds6
	 * @date 2016年1月25日 下午2:18:41
	 * @param hsReq
	 * @param hsResp
	 * @param e
	 * @throws IOException 
	 */
	@SuppressWarnings("rawtypes")
	public void modifyRedirectResponse(HttpServletRequest hsReq, HttpServletResponse hsResp, Exception e) throws IOException {
		if (e instanceof C301Exception) {
			C301Exception c301e = (C301Exception) e;
			log.debug("重定向", c301e);
			hsResp.setStatus(c301e.getCode());
			hsResp.sendRedirect(c301e.getRedirectPath());
			
			// 记录结果
			ParametersPrinter.setResponseRedirect(c301e.getRedirectPath());
		} else if (e instanceof C302Exception) {
			C302Exception c302e = (C302Exception) e;
			log.debug("302重定向", c302e);
			hsResp.setStatus(c302e.getCode());			
			hsResp.sendRedirect(c302e.getRedirectPath());
			
			// 记录结果
			ParametersPrinter.setResponseRedirect(c302e.getRedirectPath());
		}
		process(hsReq, hsResp, new ResponseModel());
	}
	
	/**
	 * @Description 设置异常返回信息
	 * @author wanggang-ds6
	 * @date 2016年1月25日 下午2:28:05
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param e 异常
	 * @throws IOException
	 */
	public void modifyExceptionResponse(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
		ResponseModel<Object> responseModel = new ResponseModel<Object>();
		boolean isPrintDebug = isPrintDebug();
        if (e instanceof BaseCodeException) {
			BaseCodeException bce = (BaseCodeException) e;
			if((bce.getCode() >= 400) || (bce.getCode() < 500)) {
				log.warn("4xx系列错误:", bce);    //4xx错误调整日志级别
			}
			else{
				log.error("过滤器捕获code异常", bce);
			}
			response.setStatus(bce.getCode());
            responseModel.setMessage(bce.getMessage());

            //添加：对于数据校验失败。message不显示具体失败原因，改在debug中显示 @author zhaozhou
            if(bce.getMessage().contains(BaseExceptionMessage.CHECK_DATA_FAILED_MSG)){
                responseModel.setMessage(BaseExceptionMessage.CHECK_DATA_FAILED_MSG);
            }else if(bce.getMessage().contains(BaseExceptionMessage.CHECK_JSON_MSG)){
                responseModel.setMessage(BaseExceptionMessage.CHECK_JSON_MSG);
            }

		} else {
			log.error("过滤器捕获异常", e);
			response.setStatus(500);
			responseModel.setMessage("系统繁忙，请稍后重试。");
		}
		//zhaozhou 2016-07-22：修改：自定义异常和其他异常都能输出debug(//TODO 错误信息不全)
		if (isPrintDebug) {
			responseModel.setDebug(e.getMessage() + ":" + Arrays.asList(e.getStackTrace()));
		}
		process(request, response, responseModel);
	}
	
	/**
	 * @Description 是否打印调试信息 TODO 调试信息需要配置,一次加入缓存?
	 * @author wanggang-ds6
	 * @date 2016年1月25日 下午2:13:34
	 * @return
	 */
	private boolean isPrintDebug() {
		try{
			String debugStatus = configCenter.get(PublicParamsConstant.WEBAPI_DEBUG_STATUS);
			if (debugStatus != null && debugStatus.trim().length() > 0 && debugStatus.trim().equalsIgnoreCase("Y")) {
				return true;
			}
		}catch(Exception e){
			log.debug("注册中心获取WEBAPI_DEBUG_STATUS失败,默认不输出debug，异常为：{}",e);
			return false;
		}
		return false;
	}
}
