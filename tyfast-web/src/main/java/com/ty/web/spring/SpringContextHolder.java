package com.ty.web.spring;

import com.google.common.collect.Maps;
import com.ty.web.utils.WebUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.LocaleResolver;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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
    private static ResourceBundleMessageSource messageSource;
    private static Method getResourceBundleMethod;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        SpringContextHolder.localeResolver = applicationContext.getBean(LocaleResolver.class);
        SpringContextHolder.messageSource = applicationContext.getBean(ResourceBundleMessageSource.class);
        getResourceBundleMethod = ReflectionUtils.findMethod(messageSource.getClass(), "getResourceBundle", String.class, Locale.class);
        ReflectionUtils.makeAccessible(getResourceBundleMethod);
    }

    /**
     * 从Http Request对象中获取Locale
     *
     * @return Locale
     */
    public static Locale getLocale() {
        return localeResolver.resolveLocale(WebUtil.getHttpRequest());
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
     * 获取资源包的所有内容
     *
     * @param baseName
     * @return Map<String, String>
     */
    public static Map<String, String> getResourceBundle(String baseName) {
        return getResourceBundle(baseName, getLocale());
    }

    /**
     * 获取资源包的所有内容
     *
     * @param baseName
     * @param locale
     * @return Map<String, String>
     */
    public static Map<String, String> getResourceBundle(String baseName, Locale locale) {
        Map<String, String> bundleMap = Maps.newHashMap();
        ResourceBundle bundle = (ResourceBundle) ReflectionUtils.invokeMethod(getResourceBundleMethod, messageSource, baseName, locale);
        if (null != bundle) {
            bundle.keySet().stream().forEach(k -> bundleMap.put(k, bundle.getString(k)));
        }
        return bundleMap;
    }
}
