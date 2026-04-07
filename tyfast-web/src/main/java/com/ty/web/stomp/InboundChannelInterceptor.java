package com.ty.web.stomp;

import com.ty.api.model.system.SysUser;
import com.ty.cm.utils.cache.Cache;
import com.ty.web.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import static com.ty.cm.constant.Ty.ESCAPE;
import static com.ty.cm.constant.Ty.NIL;
import static com.ty.cm.constant.Ty.SLASH;
import static org.apache.shiro.subject.support.DefaultSubjectContext.PRINCIPALS_SESSION_KEY;

/**
 * WebSocket 入站消息拦截器
 *
 * @Author Tommy
 * @Date 2025/8/24
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

        // 订阅Topic拦截，判断是否拥有此Topic的订阅权限
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String topic = accessor.getDestination();
            SysUser user = getCurrentUser(accessor.getUser());
            if (topic.startsWith("/user") || WebUtil.hasTenantResourcesPermis(getTenantId(topic), user)) {
                log.info(user.getLoginName() + " 可以订阅WS Topic：" + topic);
            } else {
                log.warn(user.getLoginName() + " 无权订阅WS Topic：" + topic);
                return null;
            }
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

    /**
     * 获取租户ID
     */
    public String getTenantId(String topic) {
        String tenantId = NIL;
        if (StringUtils.isNotBlank(topic)) {
            String[] parts = topic.split(ESCAPE + SLASH);
            tenantId = parts[parts.length - 1];
        }
        return tenantId;
    }
}
