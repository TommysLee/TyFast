package com.ty.web.spring.config.properties;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Shiro属性配置类
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@ConfigurationProperties(prefix = "shiro.web")
@Data
public class ShiroProperties {

    /** 登录URL (默认值：与shiro.loginUrl相同) **/
    @Value("${shiro.loginUrl}")
    private String loginUrl;

    /** 注销登录URL（默认值：与shiro.logoutUrl相同） **/
    @Value("${shiro.logoutUrl}")
    private String logoutUrl;

    /** 认证成功后，跳转的URL（默认值：与shiro.successUrl相同） **/
    @Value("${shiro.successUrl}")
    private String successUrl;

    /** 访问未经授权的资源时，跳转的URL（默认值：与shiro.unauthorizedUrl相同） **/
    @Value("${shiro.unauthorizedUrl}")
    private String unauthorizedUrl;

    /** 鉴权规则 **/
    private String rules;

    /** 不需要鉴权的URL **/
    private List<String> ignoreUrls = Lists.newArrayList();
}
