package com.gomeplus.bs.service.lol.event.reply;

import com.gomeplus.bs.interfaces.lol.vo.mq.ReplyMqVo;
import com.gomeplus.bs.service.lol.event.common.AbstractIdEvent;

/**
 * @Description 发表评论事件 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:56:57
 */
public class PostReplyEvent extends AbstractIdEvent<ReplyMqVo> {
	public PostReplyEvent(String id) {
		super(id);
	}
}


