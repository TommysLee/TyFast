package com.ty.cm.constant.enums;

/**
 * WebSocket消息类型
 *
 * @Author Tommy
 * @Date 2022/3/28
 */
public enum SocketMessageType {

    /** 通知 **/
    NOTICE("notice"),

    /** 告警 **/
    ALARM("alarm"),

    /** 其它 **/
    OTHERS("others");

    private final String type;

    SocketMessageType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
