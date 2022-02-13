package com.ty.cm.utils;

import org.apache.commons.lang3.StringUtils;

import java.nio.CharBuffer;

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
}
