package com.ty.web.shiro;

import com.ty.web.utils.WebUtil;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Shiro 鉴权服务
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
public class AuthorizationFilter extends PermissionsAuthorizationFilter {

    /**
     * 无权限访问被拦截时回调
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (WebUtil.isAjax(request)) {
            int statusCode = null == getSubject(request, response).getPrincipal() ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN;
            WebUtil.sendError(response, statusCode);
            return false;
        }
        return super.onAccessDenied(servletRequest, servletResponse);
    }
}
