package com.gomeplus.bs.framework.dubbor.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhaozhou
 * @Description
 * @date 2016/8/16 15:53
 */
@Slf4j
public final class InternetProtocol {
    /**
     * 构造函数.
     */
    private InternetProtocol() {
    }

    /**
     * 获取客户端IP地址支持多级反向代理
     * @param request  HttpServletRequest
     * @return 客户端真实IP地址
     */
    public static String getRemoteAddr(final HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-Forwarded-For");
        // 如果通过多级反向代理，[X-Forwarded-For]的值不止一个，而是一串用逗号分隔的IP值
        // 此时取X-Forwarded-For中第一个非unknown的有效IP字符串
        if (isEffective(remoteAddr)) {
            return remoteAddr.split(",")[0].trim();
        }
        remoteAddr = request.getHeader("X-Real-IP");
        if (isEffective(remoteAddr)) {
            return remoteAddr ;
        }
        return  request.getRemoteAddr();
    }
    /**
     * 远程地址是否有效.
     * @param remoteAddr 远程地址
     * @return true代表远程地址有效，false代表远程地址无效
     */
    private static boolean isEffective(final String remoteAddr) {
        boolean isEffective = false;
        if ((null != remoteAddr) && (!"".equals(remoteAddr.trim())) && (!"unknown".equalsIgnoreCase(remoteAddr.trim()))) {
            isEffective = true;
        }
        return isEffective;
    }
}
