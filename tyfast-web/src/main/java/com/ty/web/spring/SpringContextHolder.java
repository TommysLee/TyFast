package com.ty.web.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Spring容器工具类，方便在非spring管理环境中获取bean
 *
 * 此种实现方式更为通用化，兼顾Spring普通项目与Spring Boot项目
 *
 * @Author Tommy
 * @Date 2022/10/17
 */
@Component
public class SpringContextHolder extends com.ty.cm.spring.SpringContextHolder {

    private static LocaleResolver localeResolver;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        SpringContextHolder.localeResolver = applicationContext.getBean(LocaleResolver.class);
    }

    /**
     * 获取Message信息
     *
     * @param code
     * @return String
     */
    public static String getMessage(String code) {
        return applicationContext.getMessage(code, null, getLocale());
    }

    /**
     * 获取Http Request对象
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    /**
     * 从Http Request对象中获取Locale
     *
     * @return Locale
     */
    public static Locale getLocale() {
        return localeResolver.resolveLocale(getRequest());
    }
}
