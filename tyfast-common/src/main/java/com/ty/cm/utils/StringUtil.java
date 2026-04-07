package com.ty.cm.utils;

import com.google.common.base.CaseFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @Author Tommy
 * @Date 2022/2/10
 */
public class StringUtil {

    /**
     * 去除字符串两边指定的字符
     *
     * @param text 待处理的文本
     * @param symbol 需要去除的字符
     * @return String
     */
    public static String trim(String text, String symbol) {
        return StringUtil.trim(text, symbol, null);
    }

    /**
     * 去除字符串两边指定的字符
     *
     * @param text 待处理的文本
     * @param symbol 需要去除的字符
     * @param prepend 去除后，在结果前追加的内容
     * @return String
     */
    public static String trim(String text, String symbol, String prepend) {
        String result = text;
        if (StringUtils.isNotBlank(text) && StringUtils.isNotEmpty(symbol)) {
            text = text.trim();
            char csymbol = symbol.charAt(0);
            char[] charArray = text.toCharArray();
            int len = charArray.length;
            int begin = 0, end = len;
            // 从左边查找，最后一个 symbol 索引
            for (int i = 0; i < len; i++) {
                if (charArray[i] == csymbol) {
                    begin = i + 1;
                    continue;
                }
                break;
            }

            // 从右边查找，第一个 symbol 索引
            for (int i = len - 1; i >= 0; i--) {
                if (charArray[i] == csymbol) {
                    end = i;
                    continue;
                }
                break;
            }
            end = 0 == end? len : end;

            // 得出结果
            result = (null != prepend? prepend : StringUtils.EMPTY) + text.substring(begin, end);
        }
        return result;
    }

    /**
     * 通配符匹配验证
     *
     * @param text      文本
     * @param wildcard  通配符
     * @return String
     */
    public static boolean wildcardMatch(String text, String wildcard) {
        boolean isMatch = false;
        if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(wildcard)) {
            isMatch = FilenameUtils.wildcardMatch(text, wildcard, IOCase.INSENSITIVE);
        }
        return isMatch;
    }

    /**
     * 从完全限定名中获取类名
     *
     * @param fullClassName 完全限定名
     * @return String
     */
    public static String getClassName(String fullClassName) {
        return ClassUtils.getShortClassName(fullClassName);
    }

    /**
     * 将驼峰式字符串转换为以连接符拼接的字符串
     *
     * @param str 驼峰式字符串
     * @return String
     */
    public static String camel2Hyphen(String str) {
        if (StringUtils.isNotBlank(str)) {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
        }
        return str;
    }

    /**
     * 检查字符串是否为数字字符串
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        boolean flag = false;
        if (null != str) {
            Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
            flag = pattern.matcher(str).matches();
        }
        return flag;
    }
}
