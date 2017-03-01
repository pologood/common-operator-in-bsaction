package com.gomeplus.bs.service.lol.event.content;

import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.service.lol.event.common.AbstractIdEvent;

/**
 * @Description 发表动态事件 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:56:57
 */
public class PostContentEvent extends AbstractIdEvent<EntryMqVo> {

	public PostContentEvent(String id) {
		super(id);
	}

}


