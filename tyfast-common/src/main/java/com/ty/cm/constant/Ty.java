package com.ty.cm.constant;

/**
 * 项目常量
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public interface Ty {

    /** 默认字符集 **/
    String DEFAULT_CHARSET = "utf-8";

    /** 对称加密算法默认密钥 **/
    String DEFAULT_DES_PASSWORD = "D5FE558C2B0FDC5AD838EBAC9B80355B";

    /** 默认的安全码 **/
    String DEFAULT_SECURITY_CODE = "4120B3A1D30E366612DC45FE0FB93C7C";

    /** 数据总记录数Key **/
    String TOTAL = "total";

    /** 数据结果集Key **/
    String DATA = "data";

    /** 总页数Key **/
    String PAGES = "pages";

    /** 1分钟秒数 **/
    int SECONDS_OF_MINUTE = 60;

    /** 24小时秒数 **/
    int SECONDS_OF_DAY = 3600 * 24;

    /** 24小时秒数近似值(差1秒) **/
    int SECONDS_OF_DAY_APPROX = SECONDS_OF_DAY - 1;

    /** 1天小时数 **/
    int HOURS_OF_DAY = 24;

    /** 默认页码 **/
    String DEFAULT_PAGE = "1";

    /** 默认每页显示条数 **/
    String DEFAULT_PAGESIZE = "20";

    /** 正则转义符 **/
    String ESCAPE = "\\";

    /** 竖线分隔符 **/
    String DELIMITER_VLINE = ESCAPE + "|";

    /** 逗号 **/
    String COMMA = ",";

    /** 分号 **/
    String SEMICOLON = ";";

    /** 点号 **/
    String POINT = ".";

    /** 横线 **/
    String HLINE = "-";

    /** 美元符号 **/
    String DOLLAR = "$";

    /** 斜杠 **/
    String SLASH = "/";

    /** 星号 **/
    String ASTERISK = "*";

    /** 32个问号 **/
    String QUESTION_THIRTY_TWO = "????????????????????????????????";

    /** 默认缓存区大小 **/
    int DEFAULT_BUFFER_SIZE = 4096;

    /** '空'标识 **/
    String NIL = "nil";

    /** 未知 **/
    String UNKNOWN = "Unknown";

    /** 租户标识 **/
    String TENANT_ID = "tenantId";
}
