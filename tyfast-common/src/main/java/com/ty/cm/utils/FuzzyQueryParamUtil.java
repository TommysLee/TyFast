package com.ty.cm.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模糊查询的参数转义处理工具类
 *
 * @Author Tommy
 * @Date 2022/2/3
 */
public class FuzzyQueryParamUtil {

    // 默认数据库类型
    private final static DatabaseType DEFAULT_DB_TYPE = DatabaseType.MYSQL;

    // 通配符
    private final static String WILDCARD = "%";

    // MySQL正则表达式
    private final static String REGX_MYSQL = "[\\%\\_\\\\]";

    /**
     * 转义模糊查询的参数值
     *
     * @param value 需要转义的值
     * @return String 返回转义后的值
     * @throws Exception
     */
    @SuppressWarnings("incomplete-switch")
    public static String escape(String value, DatabaseType... databaseType) throws Exception {

        final DatabaseType dbType = databaseType != null && databaseType.length > 0 ? databaseType[0] : DEFAULT_DB_TYPE;
        final StringBuffer escapeValue = new StringBuffer();
        if (StringUtils.isNotBlank(value)) {
            String escape = null;
            Matcher matcher = null;

            switch (dbType) {
                case MYSQL:
                    escape = "\\\\";
                    matcher = Pattern.compile(REGX_MYSQL).matcher(value);
                    break;
                case ORACLE:
                    break;
                case MSSQL:
                    break;
            }

            while (matcher.find()) {
                matcher.appendReplacement(escapeValue, escape + "$0");
            }
            matcher.appendTail(escapeValue);
        }
        return escapeValue.length() > 0? escapeValue.toString() : value;
    }

    public enum DatabaseType {
        MYSQL, ORACLE, MSSQL
    }
}
