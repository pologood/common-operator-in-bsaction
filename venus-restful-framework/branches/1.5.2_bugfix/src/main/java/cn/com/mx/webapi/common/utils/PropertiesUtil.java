package cn.com.mx.webapi.common.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;


/**
 * @Description: 获取配置文件中的信息 
 * @author: lijiahuan
 * @date: 2015年5月15日 上午10:31:02
 */
@Slf4j
public class PropertiesUtil {
	
	/**
	 * @Description: 通过属性名称直接获取，service.properties文件中的属性值
	 * @author: lijiahuan
	 * @date: 2015年5月15日 上午10:51:08
	 * @param pName 属性名
	 * @param defultValue 默认属性值
	 * @return
	 */
	public static String getProProperties(String pName, String defultValue){	
		try{
		    Properties prop = new Properties();// 属性集合对象     
		    InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties");// 属性文件输入流，默认app
		    prop.load(fis);// 将属性文件流装载到Properties对象中     
		    fis.close();// 关闭流    
	    
	    return prop.getProperty(pName,defultValue);
		}catch(Exception e){
			log.error("读取配置属性：{} ,出错,{}", pName,e.getMessage());
			return defultValue;
		}
	}
	
	/**
	 * @Description: 通过属性名称获取，指定properties文件中的属性值
	 * @author: lijiahuan
	 * @date: 2015年5月15日 上午10:51:52
	 * @param fileName 指定文件名，不带.properties
	 * @param pName 属性名
	 * @param defultValue 默认属性值
	 * @return
	 */
	public  static String getProProperties(String fileName,String pName, String defultValue){	
		try{
		    Properties prop = new Properties();// 属性集合对象     
		    FileInputStream fis = new FileInputStream("src/main/resources/"+fileName+".properties");// 属性文件输入流，默认service     
		    prop.load(fis);// 将属性文件流装载到Properties对象中     
		    fis.close();// 关闭流    
	    
	    return prop.getProperty(defultValue,defultValue);
		}catch(Exception e){
			log.error("读取配置属性：{} ,出错,{}", pName,e.getMessage());
			return defultValue;
		}
	}

}
