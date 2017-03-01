/**   
 * Copyright © 2016 mx. All rights reserved.
 * 
 * @Title: UserFacade.java 
 * @Prject: bs-service-permission-api
 * @Package: com.gomeplus.bs.service.permission.service 
 * @Description: 会员操作类
 * @author: sunyizhong   
 * @date: 2016年10月10日 上午10:58:16 
 * @version: V1.0   
 */
package com.gomeplus.oversea.bi.service.item.clients;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gomeplus.oversea.bi.service.item.vo.MenuPrivilegeConfVo;

/**
 * 
 * @ClassName: Resource 
 * @Description: 资源操作类
 * @author: liyan
 * @date: 2016年10月12日 上午10:58:24
 */
@FeignClient("bs-service-permission")
public interface MenuPrivilegeConfResource {
	@RequestMapping(method=RequestMethod.PUT,value="/permission/menuPrivilegeConf",consumes = "application/json")
	public void doPut(@RequestParam("module") String module,MenuPrivilegeConfVo menuPrivilegeConfVo) throws Exception;
}
