package com.ty.cm.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JxltEngine;
import org.apache.commons.jexl3.JxltEngine.Expression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 基于 JeXL表达式引擎 实现文本模板的解析
 *
 * <p>
 *  可用于消息模板、EMail模板、站内信模板等内容的占位符替换。
 *  <br/>
 *  占位符格式：${...}
 * </p>
 *
 * @Author Tommy
 * @Date 2022/8/26
 */
@Slf4j
public final class TemplateUtil {

    public static final JexlEngine JEXL_ENGINE;
    public static final JxltEngine JXLT_ENGINE;

    static {
        // 以不严格模式初始化JeXL引擎（此模式：自动处理null或未定义的属性，不会引发异常）
        JEXL_ENGINE = new JexlBuilder().strict(false).create();

        // A simple "JeXL Template" engine
        JXLT_ENGINE = JEXL_ENGINE.createJxltEngine();
    }

    /**
     * 解析文本模板，返回结果内容
     *
     * @param templateText 模板内容
     * @param vars  数据
     * @return 返回解析后的内容
     */
    public static String resolveTemplate(String templateText, final Map<String, Object> vars) {
        String result = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(templateText)) {
            try {
                JexlContext context = new MapContext(vars);
                Expression expression = JXLT_ENGINE.createExpression(templateText);
                result = (String) expression.evaluate(context);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 解析文本模板，返回结果内容
     *
     * @param templateText 模板内容
     * @param bean Bean数据
     * @return 返回解析后的内容
     */
    public static String resolveTemplate(String templateText, Object bean) {
        String result = StringUtils.EMPTY;
        if (null != bean) {
            result = resolveTemplate(templateText, DataUtil.toMap(bean));
        }
        return result;
    }
}
