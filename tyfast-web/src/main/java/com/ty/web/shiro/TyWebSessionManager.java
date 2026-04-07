package com.ty.web.shiro;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import java.io.Serializable;

/**
 * 增强 Web Session Manager，支持从Header中获取Session ID
 *
 * @Author Tommy
 * @Date 2025/3/11
 */
@Data
public class TyWebSessionManager extends DefaultWebSessionManager {

    private String sessionIdHeader;

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        Serializable id = super.getSessionId(request, response);

        // 若 Shiro 原生获取不到SessionID，则从Header中尝试获取
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String sessionId = httpRequest.getHeader(sessionIdHeader);
        if (StringUtils.isNotBlank(sessionId)) {
            id = sessionId;
        }
        return id;
    }
}
