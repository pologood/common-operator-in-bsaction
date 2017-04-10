package com.gomeplus.oversea.bs.service.gateway.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by shangshengfang on 2017/3/2.
 */
@Data
public class MQModel {
    public String sendSource ="gateway";
    public String action;
    public Map pubParam;
    public Object data;
    public Map ext;
}
