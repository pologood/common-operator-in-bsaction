package cn.com.mx.webapi.common.utils;

import com.google.common.base.Strings;
import cn.com.mx.webapi.common.exceptions.BaseExceptionMessage;
import cn.com.mx.webapi.common.exceptions.code.C422Exception;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Description 提供切分公参的工具 ：app=AppId/From
 * @author zhaozhou
 * @date 2016年7月19日 下午2:47:13
 */
@Slf4j
public class SplitPublicParam {
	/**
	 * @Description TODO 
	 * @author zhaozhou
	 * @date 2016年7月19日 下午2:52:23
	 * @param parameter 公参
	 * @param n  取公参的第n部分
	 * @return
	 */
	public static String getParameterPart(String parameter,int n){
		if( Strings.isNullOrEmpty(parameter) || parameter.indexOf("/") == -1 ){
			throw new C422Exception(BaseExceptionMessage.CHECK_DATA_FAILED);
		}
		String[] strs = parameter.split("/");
		if(strs.length<n){
			log.error("所给参数不能分成{}部分",n);
			return null;
		}
		return strs[n-1];
	}
	
	public static void main(String[] args) {
		System.out.println(getParameterPart("003/adfadsfadasdf/121",4));
	}
}
