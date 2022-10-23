package com.ty.web.utils;

import com.ty.cm.utils.IpUtils;
import com.ty.web.spring.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取Web IP相关信息的实用工具类
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@Slf4j
public class WebIpUtil extends IpUtils {

    /**
     * 获取客户端的IP地址
     *
     * @return String
     */
    public static String getClientIP() {
        return getClientIP(SpringContextHolder.getRequest());
    }

    /**
     * 获取客户端的IP地址 <br/>
     *
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过Nginx等反向代理软件后，这个方法会失效。<br/>
     *
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，<br/>
     * 如果还不存在再从Proxy-Client-IP与WL-Proxy-Client-IP中查找，<br/>
     * 如果仍不存在，则调用Request.getRemoteAddr()<br/>
     *
     * 说明：
     * 从Header中取值时，属性名称不区分大小写
     *
     * @param request
     * @return String
     */
    public static String getClientIP(HttpServletRequest request) {

        if (null == request) {
            return "0.0.0.0";
        }

        log.info("url :" + request.getRequestURI());
        log.info("X-Real-IP : " + request.getHeader("X-Real-IP"));
        log.info("X-Forwarded-For : " + request.getHeader("X-Forwarded-For"));
        log.info("Proxy-Client-IP : " + request.getHeader("Proxy-Client-IP"));
        log.info("WL-Proxy-Client-IP : " + request.getHeader("WL-Proxy-Client-IP"));
        log.info("RemoteAddr : " + request.getRemoteAddr());

        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotBlank(ip) && !StringUtils.equalsIgnoreCase("unknown", ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP。
                int index = ip.indexOf(',');
                if (index != -1) {
                    ip = ip.substring(0, index);
                }
            }
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return"0:0:0:0:0:0:0:1".equals(ip)? "127.0.0.1" : ip;
    }
}
