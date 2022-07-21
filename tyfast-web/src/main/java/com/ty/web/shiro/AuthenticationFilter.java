package com.ty.web.shiro;

import com.ty.api.model.system.SysUser;
import com.ty.api.system.service.SysUserService;
import com.ty.cm.model.AjaxResult;
import com.ty.web.push.TPush;
import com.ty.web.utils.WebIpUtil;
import com.ty.web.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.ty.cm.constant.ShiroConstant.DEFAULT_CAPTCHA_PARAM;

/**
 * Shiro认证服务
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@Slf4j
public class AuthenticationFilter extends FormAuthenticationFilter {

    /** 账户业务接口 **/
    @Autowired
    @Lazy
    private SysUserService sysUserService;

    /** TPush消息推送 **/
    @Autowired
    @Lazy
    private TPush tpush;

    /** "验证码"参数名称 */
    private String captchaParam = DEFAULT_CAPTCHA_PARAM;

    /**
     * 创建令牌
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {

        String username = getUsername(request);
        String password = getPassword(request);
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);
        String captcha = getCaptcha(request);
        return new com.ty.web.shiro.AuthenticationToken(username, password, rememberMe, host, captcha);
    }

    /**
     * 未经认证时访问系统在此拦截
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

        final String curUrl = WebUtils.getPathWithinApplication(WebUtils.getHttpRequest(SecurityUtils.getSubject()));
        final boolean isLoginUrl = getLoginUrl().equals(curUrl);
        if (!isLoginUrl && WebUtil.isAjax(WebUtils.toHttp(request))) { // 登录URL不能拦截
            WebUtil.sendError(WebUtils.toHttp(response), HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return super.onAccessDenied(request, response);
    }

    /**
     * 登录失败的回调函数
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ex, ServletRequest request, ServletResponse response) {

        final boolean isAjax = WebUtil.isAjax(WebUtils.toHttp(request));

        /*
         * 转换标准异常为自定义异常（因框架架构设计问题，只在多Realm情况下，才需要此操作）
         */
        if (token instanceof com.ty.web.shiro.AuthenticationToken) {
            com.ty.web.shiro.AuthenticationToken authenticationToken = (com.ty.web.shiro.AuthenticationToken) token;
            ex = null != authenticationToken.getAex()? authenticationToken.getAex() : ex;
        }

        try {
            WebUtil.writeJSON(WebUtils.toHttp(response), AjaxResult.warn(ex.getMessage()));
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        } finally {
            log.warn("登录校验失败::" + (isAjax? "异步":"同步") + "::" + ex.getMessage());
        }
        return !isAjax;
    }

    /**
     * 登录成功的回调函数
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {

        final boolean isAjax = WebUtil.isAjax(WebUtils.toHttp(request));
        String loginIp = WebIpUtil.getClientIP(WebUtils.toHttp(request));

        final SysUser account = (SysUser) subject.getPrincipal();
        log.info(account.getLoginName() + " 登录成功::" + (isAjax? "异步":"同步") + " :: From " + loginIp);

        // 此处可写业务代码
        // 如：获取员工信息等，可在账户表中，添加辅助字段，用于存储业务数据
        // ......

        // 更新用户的登录信息(IP & 登录时间)
        SysUser sysUser = new SysUser();
        sysUser.setUserId(account.getUserId());
        sysUser.setLoginTime(new Date());
        sysUser.setLoginIp(loginIp);
        sysUserService.update(sysUser);

        // 实现登录互踢
        boolean result = sysUserService.kickOut(account, subject.getSession().getId().toString());
        if (result) { // 将下线消息通知到同账户的其它客户端
            tpush.kickOut(account.getLoginName());
        }

        // 输出成功信息
        try {
            WebUtil.writeJSON(WebUtils.toHttp(response), AjaxResult.success());
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
        return !isAjax? super.onLoginSuccess(token, subject, request, response) : !isAjax;
    }

    /**
     * 获取验证码
     *
     * @param request
     * @return 验证码
     */
    protected String getCaptcha(ServletRequest request) {

        return WebUtils.getCleanParam(request, captchaParam);
    }
}
