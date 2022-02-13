package com.ty.web.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 登录页面Controller
 *
 * @Author Tommy
 * @Date 2022/1/29
 */
@Controller
public class LoginController {

    /**
     * 通过GET方式，跳转到登录页面
     */
    @GetMapping("login")
    public String login() {
        return "login";
    }
}
