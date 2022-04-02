package com.ty.web.stomp;

import com.ty.web.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket STMOP 会话/握手拦截器
 *
 * @Author Tommy
 * @Date 2022/3/28
 */
@Slf4j
public class SessionHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * WebSocket握手前调用此方法
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 因为 STOMP 连接端点 受Shiro保护，所以这里直接返回true
        return true;
    }

    /**
     * WebSocket握手后调用此方法
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        String tysid = WebUtil.getSession().getId().toString();
        log.info("WebScoket连接建立成功：" + tysid + " " + request.getPrincipal());
    }
}
