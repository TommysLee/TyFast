package com.ty.web.system.controller;

import com.ty.api.model.system.SysUser;
import com.ty.cm.model.AjaxResult;
import com.ty.web.spring.SpringContextHolder;
import com.ty.web.spring.config.properties.TyProperties;
import com.ty.web.utils.WebUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 系统首页Controller
 *
 * @Author Tommy
 * @Date 2022/1/30
 */
@Controller
public class HomeController {

    @Autowired
    private TyProperties tyProperties;

    /**
     * 跳转到首页
     */
    @GetMapping("index")
    public String index() {
        SysUser account = WebUtil.getCurrentAccount();
        return (null != account && StringUtils.isNotBlank(account.getHomeAction()))? "redirect:" +  account.getHomeAction() : "index";
    }

    /**
     * 获取语言列表
     */
    @GetMapping("/lang/list")
    @ResponseBody
    public AjaxResult langlist() throws Exception {
        return AjaxResult.success(tyProperties.getLangList());
    }

    /**
     * 获取语言资源包
     */
    @RequestMapping("/lang/resources")
    @ResponseBody
    public AjaxResult resourceBundle() throws Exception {
        return AjaxResult.success(SpringContextHolder.getResourceBundle("i18n.language"));
    }
}
