package com.gomeplus.bs.service.lol.event.audit;

import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.service.lol.event.common.AbstractIdEvent;

/**
 * @Description 审核动态事件 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:56:57
 */
public class AuditContentEvent extends AbstractIdEvent<EntryMqVo> {

	public AuditContentEvent(String id) {
		super(id);
	}

}


