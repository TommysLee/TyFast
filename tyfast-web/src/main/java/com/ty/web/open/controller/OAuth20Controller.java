package com.ty.web.open.controller;

import com.ty.api.model.system.SysUser;
import com.ty.api.system.service.SysUserService;
import com.ty.cm.utils.cache.Cache;
import com.ty.web.base.controller.BaseController;
import com.ty.web.shiro.AuthenticationFilter;
import com.ty.web.shiro.AuthenticationToken;
import com.ty.web.utils.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * 基于 OAuth2.0 实现第三方登录的Base Controller
 *
 * @Author Tommy
 * @Date 2025/6/21
 */
public class OAuth20Controller extends BaseController {

    @Autowired
    protected SysUserService sysUserService;

    @Autowired
    @Lazy
    protected AuthenticationFilter authenFilter;

    @Autowired
    protected Cache cache;

    /**
     * Shiro Login
     */
    protected String login(SysUser user, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AuthenticationToken token = new AuthenticationToken(user.getLoginName(), user.getPassword(), false);
        try {
            Subject subject = WebUtil.getSubject();
            subject.login(token);
            authenFilter.postHandle(subject, false, request, response);
        } catch (AuthenticationException e) {
            request.setAttribute("message", (null != token.getAex()? token.getAex() : e).getMessage());
            request.setAttribute("authed", false);
            return "/fail";
        }
        return "redirect:/index";
    }
}
