package com.ty.cm.constant;

/**
 * 项目常量
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public interface Ty {

    /** 默认字符集 **/
    public static final String DEFAULT_CHARSET = "utf-8";

    /** 对称加密算法默认密钥 **/
    public static final String DEFAULT_DES_PASSWORD = "D5FE558C2B0FDC5AD838EBAC9B80355B";

    /** 默认的安全码 **/
    public static final String DEFAULT_SECURITY_CODE = "4120B3A1D30E366612DC45FE0FB93C7C";

    /** 数据总记录数Key **/
    public static final String TOTAL = "total";

    /** 数据结果集Key **/
    public static final String DATA = "data";

    /** 总页数Key **/
    public static final String PAGES = "pages";

    /** 24小时秒数 **/
    public static final int SECONDS_OF_DAY = 3600 * 24;

    /** 24小时秒数近似值(差1秒) **/
    public static final int SECONDS_OF_DAY_APPROX = SECONDS_OF_DAY - 1;

    /** 默认页码 **/
    public static final String DEFAULT_PAGE = "1";

    /** 默认每页显示条数 **/
    public static final String DEFAULT_PAGESIZE = "20";

    /** 正则转义符 **/
    public static final String ESCAPE = "\\";

    /** 竖线分隔符 **/
    public static final String DELIMITER_VLINE = ESCAPE + "|";

    /** 逗号 **/
    public static final String COMMA = ",";

    /** 分号 **/
    public static final String SEMICOLON = ";";

    /** 点号 **/
    public static final String POINT = ".";

    /** 横线 **/
    public static final String HLINE = "-";

    /** 美元符号 **/
    public static final String DOLLAR = "$";

    /** 斜杠 **/
    public  static final String SLASH = "/";

    /** 默认缓存区大小 **/
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    /** '空'标识 **/
    public static final String NIL = "nil";
}
