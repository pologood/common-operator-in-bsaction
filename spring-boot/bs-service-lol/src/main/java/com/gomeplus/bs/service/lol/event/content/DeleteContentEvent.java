package com.gomeplus.bs.service.lol.event.content;

import com.gomeplus.bs.interfaces.lol.vo.mq.EntryMqVo;
import com.gomeplus.bs.service.lol.event.common.AbstractIdEvent;

/**
 * @Description 删除动态事件 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:56:57
 */
public class DeleteContentEvent extends AbstractIdEvent<EntryMqVo> {

	public DeleteContentEvent(String id) {
		super(id);
	}

}


