package cn.com.mx.webapi.common.service;

import static cn.com.mx.webapi.common.exceptions.BaseExceptionMessage.CHECK_DATA_FAILED;

import cn.com.mx.webapi.common.model.NeedLogInEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import cn.com.mx.webapi.common.annotation.LoggedInProcessor;
import cn.com.mx.webapi.common.constant.CommonConstant;
import cn.com.mx.webapi.common.constant.PublicParamsConstant;
import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;
import cn.com.mx.webapi.common.exceptions.code.C401Exception;
import cn.com.mx.webapi.common.exceptions.code.C422Exception;
import cn.com.mx.webapi.common.exceptions.code.C500Exception;
import cn.com.mx.webapi.common.model.PublicParams;
import cn.com.mx.webapi.common.servlet.BaseResource;
import cn.com.mx.webapi.common.utils.ResourceParameters;
import cn.com.mx.webapi.redis.template.SimpleJedisTemplate;
import cn.com.mx.webapi.redis.template.SimpleJedisTemplate.RedisCallback;
import redis.clients.jedis.JedisCommands;

/**
 * @Description 检测服务 
 * @author wanggang-ds6
 * @date 2016年1月22日 下午1:38:14
 */
@Service
public class CheckService {
		
	@Autowired
	private SimpleJedisTemplate simpleJedisTemplate;
	
	/**
	 * @Description 检测登录 
	 * @author wanggang-ds6
	 * @date 2016年1月22日 下午1:39:36
	 */
	public void checkLogin(final ResourceParameters servletParam ,BaseResource controller, String methodName,  Class<?>... parameterTypes) {
		NeedLogInEnum needLogIn = NeedLogInEnum.NO;
		try {
			needLogIn = LoggedInProcessor.needLogIn(controller, methodName, parameterTypes);
		} catch (Exception e) {
			throw new C500Exception(e);
		}

		if (needLogIn == NeedLogInEnum.YES || needLogIn == NeedLogInEnum.OPTIONAL) {//需要登录验证
			final PublicParams publicParams = (PublicParams) servletParam.get(PublicParamsConstant.PUBLIC_PARAMS_NAME);
			if (publicParams == null) {
				throw new C500Exception(BaseExceptionMessage.INTERNAL_SERVER_ERROR);
			}
			final String loingToken = publicParams.getLoginToken();
			final String userId = publicParams.getUserId();
			if (Strings.isNullOrEmpty(loingToken)) {
				if(needLogIn == NeedLogInEnum.OPTIONAL){
					return;
				}
				throw new C422Exception(CHECK_DATA_FAILED.setArgs(PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_TOKEN + "不能为空！"));
			}else{
				//loginToken的时候，必须同时传递userId
				if(Strings.isNullOrEmpty(userId)){
					throw new C422Exception(CHECK_DATA_FAILED.setArgs("userId" + "不能为空！"));
				}
			}
			//get userId from redis server
			String tolken_in_redis = simpleJedisTemplate.execute(new RedisCallback<String>() {
				@Override
				public String doInRedis(JedisCommands commands) {
//					return commands.get(CommonConstant.USER_TOKEN_KEY + token);
					return commands.get(getKey(servletParam,publicParams,userId));
				}
				
			});
			
			// 从redis获取token之后。应该是和参数比对(传递loginToken的时候，必须同时传递userId)
			if (tolken_in_redis == null || tolken_in_redis.trim().length() <= 0 || !tolken_in_redis.equals(loingToken) ) {
				throw new C401Exception(BaseExceptionMessage.UNAUTHORIZED);
			}
		}
	}
	private String getKey(ResourceParameters servletParam,PublicParams publicParams,String userId){
		//从请求参数或者请求头获取app=appid/form
		String app = servletParam.getString("app");
		if(Strings.isNullOrEmpty(app)){
			app=publicParams.getApp();
		}
		//appid  美信iOS: 001          美信Android: 002
		String appId;
		if(!Strings.isNullOrEmpty(app)){
			appId=app.split("/")[0];
		}else{
			throw new C422Exception(CHECK_DATA_FAILED.setArgs(PublicParamsConstant.QUERY_PARAM_X_GOMEPLUS_APP+ "不能为空！"));
		}
		
		if("003".equals(appId)){
			return CommonConstant.USER_WAPLOGIN_TOKEN_KEY+userId;
		}else if("005".equals(appId)){
			return CommonConstant.USER_PCLOGIN_TOKEN_KEY+userId;
		}else{
			return CommonConstant.USER_APPLOGIN_TOKEN_KEY+userId;
		}
	}

}
