package com.ty.cm.model;

import com.ty.cm.constant.enums.SocketMessageType;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一WebSocket响应的数据结构
 *
 * @Author Tommy
 * @Date 2022/3/28
 */
@Data
public class SocketMessage implements Serializable {

    private static final long serialVersionUID = 6644659294990516411L;

    private String type;
    private Object data;

    public SocketMessage() {
    }

    public SocketMessage(SocketMessageType messageType, Object data) {
        this.type = messageType.type();
        this.data = data;
    }

    public static SocketMessage create(SocketMessageType messageType, Object data) {
        return new SocketMessage(messageType, data);
    }
}
