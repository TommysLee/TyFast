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

    public WSocketPrincipal(String httpSessionId) {
        this.name = httpSessionId;
    }

    @Override
    public String getName() {
        return name;
    }
}
