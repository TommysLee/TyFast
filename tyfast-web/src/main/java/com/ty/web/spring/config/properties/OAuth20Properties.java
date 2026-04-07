package com.ty.web.spring.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OAuth2.0 属性配置类
 *
 * @Author Tommy
 * @Date 2025/6/21
 */
@ConfigurationProperties(prefix = "oauth2")
@Data
public class OAuth20Properties {

    /** 微信应用唯一标识 **/
    @Value("${oauth2.weixin.appId}")
    private String weixinAppId;

    /** 微信应用密钥 **/
    @Value("${oauth2.weixin.secret}")
    private String weixinSecret;

    /** 给微信应用的回调地址 **/
    @Value("${oauth2.weixin.callbackUrl}")
    private String weixinCallbackUrl;

    /** 是否启用微信登录 **/
    @Value("${oauth2.weixin.enable}")
    private Boolean weixinEnable = false;
}
