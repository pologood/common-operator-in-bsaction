package com.gomeplus.bs.service.lol.event.praise;

import com.gomeplus.bs.interfaces.lol.vo.mq.PraiseMqVo;
import com.gomeplus.bs.service.lol.event.common.AbstractIdEvent;

/**
 * @Description 取消点赞事件 
 * @author yanyuyu
 * @date 2016年12月16日 下午3:56:57
 */
public class DeletePraiseEvent extends AbstractIdEvent<PraiseMqVo> {

	public DeletePraiseEvent(String id) {
		super(id);
	}

}


