package com.gomeplus.bs.interfaces.lolengine.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @Description 发送消息提醒提供的vo类型
 * @author mojianli
 * @date 2016年12月19日 下午2:35:02
 */
@Data
public class MsgAlertVo implements Serializable {
    
    private static final long serialVersionUID = -5645370064964572915L;
    
    // mq消息类型 single:单条im消息;batch:批量im消息
    private String batchFlag;
    // 单条消息
    private MsgParamVo msgParamVo;
    // 批量消息
    private List<MsgParamVo> list;

}
