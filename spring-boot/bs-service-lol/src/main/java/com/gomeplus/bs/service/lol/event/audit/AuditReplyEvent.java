package com.gomeplus.bs.service.lol.event.audit;

import com.gomeplus.bs.interfaces.lol.vo.mq.ReplyMqVo;
import com.gomeplus.bs.service.lol.event.common.AbstractIdEvent;

/**
 * @Description 审核回复事件 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:56:57
 */
public class AuditReplyEvent extends AbstractIdEvent<ReplyMqVo> {

	public AuditReplyEvent(String id) {
		super(id);
	}

}


