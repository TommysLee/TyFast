package com.ty.web.system.controller;

import com.ty.web.spring.config.properties.OAuth20Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 登录页面Controller
 *
 * @Author Tommy
 * @Date 2022/1/29
 */
@Controller
public class LoginController {

    @Autowired
    private OAuth20Properties oauth20Properties;

    /**
     * 通过GET方式，跳转到登录页面
     */
    @GetMapping("login")
    public String login(Model model) {
        model.addAttribute("weixin", oauth20Properties.getWeixinEnable());
        return "login";
    }
}
