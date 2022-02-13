package com.ty.cm.exception;

import com.ty.cm.constant.enums.AjaxResultType;
import lombok.Data;

/**
 * 自定义业务异常，返回友好的提示信息
 *
 * 用途：前端提交来的数据，不符合业务需要时，通过此异常返回友好的提示信息
 *
 * @Author Tommy
 * @Date 2022/2/6
 */
@Data
public class CustomException extends RuntimeException {

    // 错误代码
    private int code = AjaxResultType.WARN.code();

    /**
     * 构造空的自定义异常
     */
    public CustomException() {
    }

    /**
     * 构造含自定义消息的自定义异常
     */
    public CustomException(String message) {
        super(message);
    }

    /**
     * 构造含自定义消息和错误代码的自定义异常
     */
    public CustomException(int code, String message) {
        super(message);
        this.code = code;
    }
}
