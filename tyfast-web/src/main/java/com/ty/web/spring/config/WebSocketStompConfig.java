package com.ty.web.spring.config;

import com.ty.cm.constant.enums.SocketMessageType;
import com.ty.cm.model.SocketMessage;
import com.ty.cm.spring.SpringContextHolder;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.web.stomp.InboundChannelInterceptor;
import com.ty.web.stomp.SessionHandshakeInterceptor;
import com.ty.web.stomp.WSocketPrincipal;
import com.ty.web.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket STOMP子协议配置类
 *
 * @Author Tommy
 * @Date 2022/1/10
 */
@Configuration
@EnableWebSocketMessageBroker
//@EnableScheduling // 启动定时任务
@Slf4j
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册STOMP端点：SocketJS通过Http访问此端口升级为WebSocket STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/stomp")
                .addInterceptors(new SessionHandshakeInterceptor()) // Session拦截器，用于判断Session是否存在
                .setHandshakeHandler(new DefaultHandshakeHandler() { // 将Shiro用户与WebSocket用户绑定

                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) { // 实例化当前Session的WSocketPrincipal
                        return new WSocketPrincipal(WebUtil.getCurrentLoginName(), WebUtil.getSession().getId().toString());
                    }
                })
                .withSockJS();
    }

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app").enableSimpleBroker("/topic", "/queue");
        config.setUserDestinationPrefix("/user"); // 点对点消息前缀

        log.info("Spring WebSocket STOMP服务已启动...");
    }

    /**
     * 配置入站数据拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(inboundChannelInterceptor());
    }

    @Bean
    public InboundChannelInterceptor inboundChannelInterceptor() {
        return new InboundChannelInterceptor();
    }

    /*
     * STOMP 消息发送测试
     */
    //@Scheduled(cron = "0/5 * * * * ?")
    public void testStompSendMessage() {
        String uusn = UUSNUtil.nextUUSN();
        log.info("STOMP 消息发送测试: " + uusn);

        SimpMessageSendingOperations sendingOperations = SpringContextHolder.getBean(SimpMessageSendingOperations.class);
        sendingOperations.convertAndSend("/topic/message/22021526926366000108", SocketMessage.create(SocketMessageType.NOTICE, uusn));
    }
}
