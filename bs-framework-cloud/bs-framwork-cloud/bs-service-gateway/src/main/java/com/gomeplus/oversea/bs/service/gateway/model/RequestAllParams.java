package com.gomeplus.oversea.bs.service.gateway.model;

import java.util.List;
import java.util.Map;

import lombok.Data;
/**
 * Created by shangshengfang on 2017/2/28.
 */
@Data
public class RequestAllParams {
    String method;
    String url;
    String header;
    String query;
    String body;
}
