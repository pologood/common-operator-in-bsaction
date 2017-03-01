package cn.com.mx.webapi.common.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.alibaba.fastjson.serializer.NameFilter;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @Description fastjson的nameFileter 
 * @author zhaozhou
 * @date 2016年5月16日 下午10:38:12
 */
@Slf4j
public class FastjsonNameFilter {
	/**
	 * 解决fastjson把实体中is开头的属性去掉的问题
	 */
	public static NameFilter nameFilter = new NameFilter() {
		@Override
		public String process(Object resource, String name, Object value) {
			if (value instanceof Boolean) {
				String nameIncludeIs = "is"
						+ name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
				Method method = null;
				Field filed = null;
				try {
					method = resource.getClass().getMethod(nameIncludeIs);
					filed = resource.getClass().getDeclaredField(nameIncludeIs);
				} catch (Exception ignored) {
				} finally {
					if (method != null && filed != null) {
						log.debug("出现以is开头的boolean变量{}",nameIncludeIs);
						return nameIncludeIs;
					}
				}
			}
			return name;
		}
	};
}
