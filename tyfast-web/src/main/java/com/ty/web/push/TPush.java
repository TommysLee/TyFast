package com.ty.web.push;

import com.ty.cm.constant.enums.SocketMessageType;
import com.ty.cm.model.SocketMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;

/**
 * TPush消息推送
 *
 * @Author Tommy
 * @Date 2022/7/21
 */
public class TPush {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SimpUserRegistry userRegistry;

    /**
     * 广播消息
     *
     * @param destination
     * @param message
     */
    public void send(String destination, SocketMessage message) {
        if (StringUtils.isNotBlank(destination) && null != message) {
            messagingTemplate.convertAndSend(destination, message);
        }
    }

    /**
     * 点对点消息
     *
     * @param user
     * @param destination
     * @param message
     */
    public void sendToUser(String user, String destination, SocketMessage message) {
        if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(destination) && null != message) {
            messagingTemplate.convertAndSendToUser(user, destination, message);
        }
    }

    /**
     * 账户强制下线通知
     *
     * @param user
     */
    public void kickOut(String user) {
        final String destination = "/queue/kickout";
        this.sendToUser(user, destination, SocketMessage.create(SocketMessageType.NOTICE, "您的账户已在另一台设备上登录，如非本人操作，请立即修改密码！"));
    }
}
