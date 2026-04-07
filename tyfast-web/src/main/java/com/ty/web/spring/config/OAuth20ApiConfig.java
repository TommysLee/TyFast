package com.ty.web.spring.config;

import com.ty.cm.utils.oauth2.apis.WeixinApi20;
import com.ty.cm.utils.oauth2.core.OAuth20Service;
import com.ty.web.spring.config.properties.OAuth20Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth2.0 API 服务配置
 *
 * @Author Tommy
 * @Date 2025/6/21
 */
@Configuration
@EnableConfigurationProperties(OAuth20Properties.class)
@Slf4j
public class OAuth20ApiConfig {

    /**
     * 微信登录接口服务
     */
    @Bean
    public OAuth20Service weixinOAuth20Service(OAuth20Properties properties) {
        WeixinApi20 weixinApi20 = new WeixinApi20(properties.getWeixinAppId(), properties.getWeixinSecret(), properties.getWeixinCallbackUrl());
        OAuth20Service weixinOAuth20Service = new OAuth20Service(weixinApi20);
        log.info("微信登录接口服务配置完毕 App_ID = {}", properties.getWeixinAppId());
        return weixinOAuth20Service;
    }
}
