package cn.com.mx.webapi.common.filter;

import java.lang.reflect.Method;
import java.net.URLDecoder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import cn.com.mx.webapi.common.constant.PublicParamsConstant;
import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;
import cn.com.mx.webapi.common.exceptions.code.C406Exception;
import cn.com.mx.webapi.common.exceptions.code.C422Exception;
import cn.com.mx.webapi.common.model.PublicParams;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @Description 组织公参：优先从url获取，如果获取不到，去header取
 * @author zhaozhou
 * @date 2016年5月13日 上午9:53:32
 */
@Slf4j
public abstract class PublicParamFilterABS {

	public void publicParam(ServletRequest sr){
		HttpServletRequest req = (HttpServletRequest)sr;
		String pathInfo = req.getPathInfo();
		PublicParams model = new PublicParams();

		//TODO 应该设置默认必填项
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_TOKEN,PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_TOKEN,"setLoginToken",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_USER_ID,PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_USER_ID,"setUserId",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_ACCESSTOKEN,PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_ACCESSTOKEN,"setAccessToken",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_DEVICE,PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_DEVICE,"setDevice",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_APP,PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_APP,"setApp",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_NET,PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_NET,"setNet",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.ACCEPT,PublicParamsConstant.QUERY_PARAM_ACCEPT,"setAccept",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_TRACE_ID,PublicParamsConstant.QUERY_PARAM_TRACEID,"setTraceId",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.JSONP,PublicParamsConstant.QUERY_PARAM_JSONP,"setJsonp",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.QUERY_PARAM_APPVERSION,PublicParamsConstant.QUERY_PARAM_APPVERSION,"setAppVersion",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_PUBPLAT,PublicParamsConstant.QUERY_PARAM_PUBPLAT,"setPubPlat",String.class,pathInfo,false);
		setValue(req,model,PublicParamsConstant.X_GOMEPLUS_IP,PublicParamsConstant.QUERY_PARAM_IP,"setIp",String.class,pathInfo,false);
		req.setAttribute(PublicParamsConstant.PUBLIC_PARAMS_NAME, model);
		checkAccept(model);
		checkPublicParams(req,model);//新增公参校验  @auther zhaozhou
	}
	private void setValue(HttpServletRequest req, PublicParams model, String key,String queryParamKey,String methodName,Class<?> keyType,String pathInfo,boolean isNullAble) {
		try {
			String value = null;

//			if (req.getParameter(queryParamKey) != null) {
//				value = req.getParameter(queryParamKey);
//			} else {
//				if (req.getHeader(key) != null) {
//					value = req.getHeader(key);
//				}
//			}

			//如果在参数列表找不到，去header找
			value=req.getParameter(queryParamKey)!=null?req.getParameter(queryParamKey):req.getHeader(key);

			if(isNullAble && value==null){//是否非必填
				throw new C422Exception(BaseExceptionMessage.CHECK_DATA_FAILED.setArgs(key));
			}

			if(value!=null){
				value = URLDecoder.decode(value, "UTF-8");
				setvalueByInvokeMethod(req, model, pathInfo, keyType, key, value,methodName);
			}
		} catch (Exception ex) {
			throw new C422Exception(BaseExceptionMessage.CHECK_DATA_FAILED.setArgs(key));
		}
	}
	/**
	 *
	 * @Description 反射组装PublicParams
	 * @author zhaozhou
	 * @date 2016年5月13日 上午10:23:11
	 * @param req
	 * @param model
	 * @param pathInfo
	 * @param keyType
	 * @param key
	 * @param value
	 * @param methodName
	 * @throws Exception
	 */
	private void setvalueByInvokeMethod(HttpServletRequest req, PublicParams model,String pathInfo, Class<?> keyType, String key, String value,String methodName)
			throws Exception {
		Method method = model.getClass().getMethod(methodName,keyType);
		if ("Integer".equals(keyType.getSimpleName())) {
			method.invoke(model,Integer.valueOf(value));
		} else if ("Long".equals(keyType.getSimpleName())) {
			method.invoke(model,Long.valueOf(value));
		} else if ("String".equals(keyType.getSimpleName())) {
			method.invoke(model,value);
		} else {
			throw new RuntimeException("unhandled exception !");
		}
	}
	/**
	 *
	 * @Description Accept校验： application/javascript,application/*,application/json,application/json
	 * @author zhaozhou
	 * @date 2016年5月13日 上午10:24:10
	 * @param model
	 */
	private void checkAccept(PublicParams model){
		String accept = model.getAccept();
		if(accept.indexOf(PublicParamsConstant.ACCEPT_TYPE_JAVASCRIPT)<0
				&&accept.indexOf(PublicParamsConstant.ACCEPT_TYPE_JSON)<0
					&&accept.indexOf(PublicParamsConstant.ACCEPT_TYPE_ANYTHING)<0
						&&accept.indexOf(PublicParamsConstant.ACCEPT_TYPE_APPLICATION_ANYTHING)<0){
			throw new C406Exception(BaseExceptionMessage.NOT_ACCEPTABLE);
		}
	}
	/**
	 *
	 * @Description 工参校验 //TODO:暂时打出警告，等客户端调整参数后，再强制校验
	 * @author zhaozhou
	 * @date 2016年7月19日 上午10:24:10
	 * @param model
	 */
	private void checkPublicParams(HttpServletRequest req,PublicParams model){
        //{OSType}/{OSVersion}/{DeviceModel}/{DeviceId}
        String regex_device = "^\\w{2,10}[/][0-9\\.]{1,10}[/][0-9a-zA-Z_ ]{2,30}[/]\\w{2,30}$";
        //app=appid/from
        String regex_app = "^[0-9]{3}[/][0-9A-Za-z\\._]{1,20}$";

        if (Strings.isNullOrEmpty(model.getDevice())) {
            log.warn("Request[{}] does not pass parameter [device]", req.getRequestURI());
        } else if (!model.getDevice().matches(regex_device)) {
            log.warn("Request[{}] parameters[{}] validation failed", req.getRequestURI(), model.getDevice());
        }

        if (Strings.isNullOrEmpty(model.getApp())) {
            log.warn("Request[] does not pass parameters [app]", req.getRequestURI());
        } else if (!model.getApp().matches(regex_app)) {
            log.warn("Request[{}] parameters[{}] validation failed", model.getApp(),model.getApp());
        }
        if (Strings.isNullOrEmpty(model.getAppVersion())) {
            log.warn("Request[] does not pass parameters [appVersion]", req.getRequestURI());
        }
    }
}
