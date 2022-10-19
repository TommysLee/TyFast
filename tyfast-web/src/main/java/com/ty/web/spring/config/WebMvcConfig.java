package com.ty.web.spring.config;

import com.ty.web.spring.config.properties.TyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Map;

/**
 * Spring MVC配置类 等同于过去的XML配置方式
 *
 * @Author Tommy
 * @Date 2022/9/2
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TyProperties tyProperties;

    @Value("${spring.web.locale:zh_CN}")
    private String defaultLocale;

    /**
     * 配置i18N国际化解析器
     */
    @Bean
    public LocaleResolver localeResolver(ResourceBundleMessageSource messageSource) {
        log.info("基于Cookie的国际化解析器初始化完毕，Cookie标识：" + tyProperties.getLang() + "\t默认语言：" + defaultLocale);

        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieName(tyProperties.getLang());
        resolver.setCookiePath("/");
        resolver.setDefaultLocale(StringUtils.parseLocale(defaultLocale));
        messageSource.setDefaultLocale(StringUtils.parseLocale(defaultLocale));
        return resolver;
    }

    /**
     * 配置视图控制器，用于实现简单的页面跳转
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        for (Map.Entry<String, String> entry : tyProperties.getViewMapping().entrySet()) {
            registry.addViewController(entry.getKey()).setViewName(entry.getValue());
        }
        log.info("配置的视图映射数量：" + tyProperties.getViewMapping().size() + " 条.");
    }
}
