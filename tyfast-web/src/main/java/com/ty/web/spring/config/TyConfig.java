package com.ty.web.spring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目全局配置类
 *
 * @Author Tommy
 * @Date 2022/2/4
 */
@Component
@ConfigurationProperties(prefix = "ty")
@Data
public class TyConfig {

    /** 用户的初始密码 **/
    private String initPassword;
}
