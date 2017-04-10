package com.gomeplus.oversea.bs.service.gateway.event;

import com.gomeplus.oversea.bs.service.gateway.model.MQModel;
import lombok.Data;

/**
 * Created by shangshengfang on 2017/3/2.
 */
@Data
public class ItemLogMQEvent extends BaseEvent {
    public MQModel mqModel;
}
