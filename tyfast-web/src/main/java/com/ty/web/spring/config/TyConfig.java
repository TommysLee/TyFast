package com.ty.web.spring.config;

import com.ty.web.push.TPush;
import com.ty.web.spring.config.properties.TyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 项目配置类
 *
 * @Author Tommy
 * @Date 2022/2/4
 */
@Configuration
@EnableConfigurationProperties(TyProperties.class)
@Slf4j
public class TyConfig {

    /**
     * TPush消息推送
     */
    @Bean
    public TPush tpush() {
        log.info("TPush消息推送已启动");
        return new TPush();
    }
}
