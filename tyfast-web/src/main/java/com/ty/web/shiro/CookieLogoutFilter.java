package com.ty.web.shiro;

import com.ty.cm.model.AjaxResult;
import com.ty.cm.utils.URLUtils;
import com.ty.web.utils.WebUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.util.WebUtils;

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

        getSubject(request, response).logout(); // Shiro内部实现

        // Cookie 登出处理
        String domain = URLUtils.getPrimaryDomain(WebUtil.getDomain(), true);
        WebUtil.removeAllCookie((HttpServletRequest) request, (HttpServletResponse) response, domain);

        // 登出后的前端交互
        if (WebUtil.isAjax()) {
            WebUtil.writeJSON(WebUtils.toHttp(response), AjaxResult.success());
        } else {
            issueRedirect(request, response, getRedirectUrl());
        }
        return false;
    }
}
