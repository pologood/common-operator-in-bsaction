package com.gomeplus.bs.service.lol.event.praise;

import com.gomeplus.bs.interfaces.lol.vo.mq.PraiseMqVo;
import com.gomeplus.bs.service.lol.event.common.AbstractIdEvent;

/**
 * @Description 点赞事件 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:56:57
 */
public class PostPraiseEvent extends AbstractIdEvent<PraiseMqVo> {
	public PostPraiseEvent(String id) {
		super(id);
	}
}


