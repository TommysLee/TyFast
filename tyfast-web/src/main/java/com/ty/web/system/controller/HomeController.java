package com.ty.web.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 系统首页Controller
 *
 * @Author Tommy
 * @Date 2022/1/30
 */
@Controller
public class HomeController {

    /**
     * 跳转到首页
     */
    @GetMapping("index")
    public String index() {
        return "index";
    }
}
