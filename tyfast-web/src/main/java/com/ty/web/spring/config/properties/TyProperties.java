package com.ty.web.spring.config.properties;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 项目属性配置类
 *
 * @Author Tommy
 * @Date 2022/2/21
 */
@ConfigurationProperties(prefix = "ty")
@Data
public class TyProperties {

    /** 用户的初始密码 **/
    private String initPassword;

    /** 视图映射 **/
    private Map<String, String> viewMapping = Maps.newHashMap();
}
