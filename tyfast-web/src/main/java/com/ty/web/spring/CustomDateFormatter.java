package com.ty.web.spring;

import com.ty.cm.utils.DateUtils;
import org.springframework.expression.ParseException;
import org.springframework.format.datetime.DateFormatter;

import java.util.Date;
import java.util.Locale;

/**
 * 自定义SpringMVC日期类型转换器
 *
 * @Author Tommy
 * @Date 2022/2/3
 */
public class CustomDateFormatter extends DateFormatter {

    /**
     * 日期字符串转换为日期对象
     */
    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        Date date = DateUtils.toDate(text);
        return date;
    }
}
