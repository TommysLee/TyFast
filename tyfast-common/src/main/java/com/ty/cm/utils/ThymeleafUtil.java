package com.ty.cm.utils;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;

/**
 * Thymeleaf工具类
 *
 * <p>
 * 用于本地化执行字符串模板 <br/>
 * 参考：https://gitee.com/jonathanzyf/thymeleaf-example
 * </p>
 *
 * @Author Tommy
 * @Date 2022/4/12
 */
public final class ThymeleafUtil {

    static final TemplateEngine engine;

    static {
        // 初始化字符串模板解析器
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setCacheable(false);
        resolver.setTemplateMode(TemplateMode.TEXT);

        // 初始化引擎
        engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
    }

    /**
     * 模板解析
     *
     * @param templateText 模板内容
     * @param dataModel 数据
     * @return 返回解析后的内容
     */
    public static String resolveTemplate(String templateText, Object dataModel) {

        String result = null;
        if (null != dataModel) {
            result = resolveTemplate(templateText, DataUtil.toMap(dataModel));
        }
        return result;
    }

    /**
     * 模板解析
     *
     * @param templateText 模板内容
     * @param dataModel 数据
     * @return 返回解析后的内容
     */
    public static String resolveTemplate(String templateText, Map<String, Object> dataModel) {

        String result = null;
        if (StringUtils.isNotBlank(templateText) && null != dataModel) {
            Context ctx = new Context(null, dataModel);
            result = engine.process(templateText, ctx);
        }
        return result;
    }
}
