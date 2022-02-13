package com.ty.web.spring;

import com.ty.cm.exception.CustomException;
import com.ty.cm.model.AjaxResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SpringMVC 全局异常处理
 *
 * @Author Tommy
 * @Date 2022/2/6
 */
@ControllerAdvice
public class GlobalWebExceptionHandler {

    /**
     * 以友好的方式返回自定义错误消息
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public AjaxResult customException(CustomException e) {
        return AjaxResult.info(e.getCode(), e.getMessage());
    }
}
