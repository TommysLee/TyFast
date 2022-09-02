package com.ty.web.spring.config;

import com.ty.web.spring.config.properties.TyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

    /**
     * 配置视图控制器，用于实现简单的页面跳转
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        for (Map.Entry<String, String> entry : tyProperties.getViewMapping().entrySet()) {
            registry.addViewController(entry.getKey()).setViewName(entry.getValue());
        }
        log.info("配置的视图映射，已加载：" + tyProperties.getViewMapping().size() + " 条.");
    }
}
