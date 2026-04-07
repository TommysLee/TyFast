package com.ty.web.others.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 适用于免密登录查看Dashboard看板的Controller
 *
 * @Author Tommy
 * @Date 2023/1/31
 */
@Controller
@RequestMapping("/show")
public class ShowController {

    /**
     * 跳转到免密登录自动预处理页面
     */
    @GetMapping("{symbol}")
    public String symbol(@PathVariable String symbol, Model model) {
        model.addAttribute("symbol", symbol);
        return "auto-login";
    }
}
