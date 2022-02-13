package com.ty.web.shiro;

import com.ty.web.utils.WebUtil;
import org.apache.shiro.web.filter.authc.LogoutFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基于Cookie机制的注销登录服务
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
public class CookieLogoutFilter extends LogoutFilter {

    /**
     * 注销登录业务逻辑处理
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response)
            throws Exception {

        // getSubject(request, response).logout(); // Shiro内部实现

        // Cookie 登出处理
        WebUtil.removeAllCookie((HttpServletRequest) request, (HttpServletResponse) response);
        issueRedirect(request, response, getRedirectUrl());
        return false;
    }
}
