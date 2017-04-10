package com.gomeplus.oversea.bs.service.gateway.model;

import com.gomeplus.oversea.bs.common.exception.entity.Error;
import lombok.Data;

import java.util.List;

/**
 * Created by shangshengfang on 2017/3/22.
 */
@Data
public class UserTokenModel {
    int statusCode;
    String errorMessage;
    String errorCode;
}
