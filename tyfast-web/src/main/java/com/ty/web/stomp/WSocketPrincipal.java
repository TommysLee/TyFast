package com.ty.web.stomp;

import java.security.Principal;

/**
 * WebSocket Principal
 *
 * @Author Tommy
 * @Date 2022/3/28
 */
public class WSocketPrincipal implements Principal {

    private String name;
    private String httpSessionId;

    public WSocketPrincipal(String loginName, String httpSessionId) {
        this.name = loginName;
        this.httpSessionId = httpSessionId;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getHttpSessionId() {
        return httpSessionId;
    }
}
