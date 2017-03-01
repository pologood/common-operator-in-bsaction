package com.gomeplus.bs.service.lol.event.reply;

import com.gomeplus.bs.interfaces.lol.vo.mq.ReplyMqVo;
import com.gomeplus.bs.service.lol.event.common.AbstractIdEvent;

/**
 * @Description 删除评论事件 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:56:57
 */
public class DeleteReplyEvent extends AbstractIdEvent<ReplyMqVo> {

	public DeleteReplyEvent(String id) {
		super(id);
	}

}


