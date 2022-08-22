package com.ty.web.system.controller;

import com.ty.api.model.system.SysUser;
import com.ty.web.utils.WebUtil;
import org.apache.commons.lang3.StringUtils;
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
        SysUser account = WebUtil.getCurrentAccount();
        return (null != account && StringUtils.isNotBlank(account.getHomeAction()))? "redirect:" +  account.getHomeAction() : "index";
    }
}
