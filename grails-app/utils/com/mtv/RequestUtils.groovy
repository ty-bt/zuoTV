package com.mtv

import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

import javax.servlet.http.HttpServletRequest

/**
 * request工具类
 */
class RequestUtils {

    /**
     * 获取当前请求的IP
     * @return
     */
    public static String getRequestIp(HttpServletRequest request = getRequest()) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip
    }

    /**
     * 获得当前请求的真实IP
     *
     * @return
     */
    public static String getRealIp(HttpServletRequest request = getRequest()) {
        def ips = StringUtils.split(getRequestIp(request), ",")
        if (ips) {
            return ips[0]
        }
        return null
    }

    /**
     * 获取当前request
     * @return
     */
    public static HttpServletRequest getRequest() {
        try {
            GrailsWebRequest webRequest = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
            if (webRequest && webRequest.getRequest()) {
                return webRequest.getRequest()
            }
        } catch (e) {
            return null
        }
    }

}
