package com.ty.web.stomp;

import com.ty.api.model.system.SysUser;
import com.ty.cm.utils.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.Map;

import static com.ty.cm.constant.ShiroConstant.TOKEN_ID;
import static org.apache.shiro.subject.support.DefaultSubjectContext.PRINCIPALS_SESSION_KEY;

/**
 * WebSocket 入站消息拦截器
 *
 * @Author Tommy
 * @Date 2022/3/28
 */
@Slf4j
public class InboundChannelInterceptor implements ChannelInterceptor {

    @Autowired
    @Lazy
    private Cache cache;

    /**
     * 数据入站的前置处理（类似于Filter）
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 订阅指令：逻辑处理
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String topic = accessor.getDestination();
            SysUser user = getCurrentUser(accessor.getUser());
            log.info("Topic = " + accessor.getDestination() + ", 订阅用户：" + user);
            log.warn("请在这里，添加 “订阅”StompCommand 权限验证逻辑。。。");

            /*
             * 请大家根据各自项目的实际需求,
             * 在这里添加业务权限判定逻辑，如当前用户是否可以订阅 此Topic
             * 可以订阅，则无需任何处理；
             * 不可订阅，请 return null
             *
             * 添加类似逻辑，可以确保消息推送给正确的人接收，防止业务消息泄露
             */
        }
        return message;
    }

    /**
     * 获取当前用户
     */
    public SysUser getCurrentUser(Principal principal) {
        WSocketPrincipal wSocketPrincipal = (WSocketPrincipal) principal;
        String tysid = wSocketPrincipal.getHttpSessionId();

        SysUser user = null;
        String shiroSessionKey = TOKEN_ID + tysid;
        Map<String, Object> sessionMap = cache.get(shiroSessionKey);
        if (null != sessionMap && sessionMap.containsKey(PRINCIPALS_SESSION_KEY)) {
            user = (SysUser) sessionMap.get(PRINCIPALS_SESSION_KEY);
        }
        return user;
    }
}
