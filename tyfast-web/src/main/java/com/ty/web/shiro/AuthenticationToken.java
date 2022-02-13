package com.ty.web.shiro;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 认证令牌
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@Data
public class AuthenticationToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 7832374823825395982L;

    /** 验证码 */
    private String captcha;

    /** 跳转URL */
    private String redirectUrl;

    /** 认证异常：用于在多Realm环境下，可以正常把异常抛出给前端 **/
    private AuthenticationException aex;

    /**
     * 构造函数
     *
     * @param username 用户名
     * @param password 密码
     */
    public AuthenticationToken(final String username, final String password) {
        super(username, password);
    }

    /**
     * 构造函数(含验证码)
     *
     * @param username   用户名
     * @param password   密码
     * @param rememberMe 记住我
     * @param host       主机IP地址
     * @param captcha    验证码
     */
    public AuthenticationToken(String username, String password, boolean rememberMe, String host, String captcha) {

        super(username, password, rememberMe, host);
        this.captcha = captcha;
    }

    /**
     * 构造函数(含验证码、跳转URL)
     *
     * @param username    用户名
     * @param password    密码
     * @param rememberMe  记住我
     * @param host        主机IP地址
     * @param captcha     验证码
     * @param redirectUrl 跳转URL
     */
    public AuthenticationToken(String username, String password, boolean rememberMe, String host, String captcha, String redirectUrl) {

        super(username, password, rememberMe, host);
        this.captcha = captcha;
        this.redirectUrl = redirectUrl;
    }

    /**
     * 设置认证异常
     *
     * @param aex 认证异常
     */
    public AuthenticationException setAex(AuthenticationException aex) {
        this.aex = aex;
        return aex;
    }

    /**
     * 设置或修改认证异常
     *
     * @param aex 认证异常
     */
    public AuthenticationException megreAex(AuthenticationException aex) {
        if (null == this.aex || this.aex instanceof UnknownAccountException) {
            this.aex = aex;
        }
        return aex;
    }
}
