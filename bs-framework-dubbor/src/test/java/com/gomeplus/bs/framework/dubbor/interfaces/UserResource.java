package com.gomeplus.bs.framework.dubbor.interfaces;

import com.gomeplus.bs.framework.dubbor.entity.User;

import java.io.Serializable;

/**
 * Created by zhaozhou on 16/12/29.
 */
public interface UserResource extends Serializable {
    User doGet(Long id);
}
