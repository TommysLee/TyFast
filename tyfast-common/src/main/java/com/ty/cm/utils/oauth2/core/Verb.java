package com.ty.cm.utils.oauth2.core;

/**
 * HTTP请求类型枚举类
 *
 * @Author Tommy
 * @Date 2022/12/22
 */
public enum Verb {

    GET(false), POST(true), PUT(true), DELETE(false, true), HEAD(false), OPTIONS(false), TRACE(false), PATCH(true), POSTBODY(true);

    private final boolean requiredBody;
    private final boolean permitBody;

    Verb(boolean requiredBody) {
        this(requiredBody, requiredBody);
    }

    Verb(boolean requiredBody, boolean permitBody) {
        if (requiredBody && !permitBody) {
            throw new IllegalArgumentException();
        }
        this.requiredBody = requiredBody;
        this.permitBody = permitBody;
    }

    public boolean isrequiredBody() {
        return requiredBody;
    }

    public boolean isPermitBody() {
        return permitBody;
    }
}
