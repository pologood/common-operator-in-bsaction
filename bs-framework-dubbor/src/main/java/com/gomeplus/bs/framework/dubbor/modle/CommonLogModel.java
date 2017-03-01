package com.gomeplus.bs.framework.dubbor.modle;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 日志实体
 * @author zhaozhou
 */
@Data
@Component
public class CommonLogModel {
    private Long costTime;
    private Long startTime;
    private Long endTime;
    private String requestUrl;
    private Object requestBody;
    private Object responseBody;
    private Map<String, String[]> requestParams;
    private PublicParams publicParams;
    private int responseCode;
}
