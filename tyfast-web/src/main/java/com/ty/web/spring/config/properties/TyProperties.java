package com.ty.web.spring.config.properties;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
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

    /** 自动登录账户字典Key **/
    private String autoLoginKey;

    /** 站点动态配置字典Key **/
    private String siteKey;

    /** 本地化语言的Cookie标识 **/
    private String lang;

    /** 语言列表 **/
    private List<Map<String, String>> langList;

    /** 视图映射 **/
    private Map<String, String> viewMapping = Maps.newHashMap();

    /** 网站ICP备案号 **/
    private String icp;

    /** Copyright **/
    private String copyright;

    /** 萤石开放平台 **/
    private String ysOpenUrl;
}
