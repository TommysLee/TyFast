package com.ty.cm.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URL实用工具类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public class URLUtils {

    // 正则对象
    private static Pattern DOMAIN_PATTERN;
    private static Pattern PRIMARY_DOMAIN_PATTERN;

    // 正则表达式
    private static final String REGEX = "(((http|https|ftp|tcp|mailto|ldap|file|news|gopher|telnet)://)?((\\w*\\.?)+)(:(\\d+))?)(.*)";
    private static final String REGEX_PRIMARY_DOMAIN = "[^\\.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.vip|\\.jp|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)$";

    // Key键
    public static final String PROTOCOL = "protocol";
    public static final String PORT = "port";
    public static final String DOMAIN = "domain";
    public static final String FULLDOMAIN = "fullDomain";
    public static final String URI = "uri";
    public static final String QUERYSTRING = "queryString";
    public static final String URL = "url";

    static {
        DOMAIN_PATTERN = Pattern.compile(REGEX);
        PRIMARY_DOMAIN_PATTERN = Pattern.compile(REGEX_PRIMARY_DOMAIN);
    }

    /**
     * 获取访问协议
     *
     * @param url
     *            ----> URL
     * @return String
     */
    public static String getProtocol(String url) {

        if (null != DOMAIN_PATTERN && StringUtils.isNotBlank(url)) {
            final Matcher matcher = DOMAIN_PATTERN.matcher(url);
            if (matcher.matches()) {
                return matcher.group(3);
            }
        }
        return null;
    }

    /**
     * 获取端口号
     *
     * @param url
     *            ----> URL
     * @return String
     */
    public static String getPort(String url) {

        if (null != DOMAIN_PATTERN && StringUtils.isNotBlank(url)) {
            final Matcher matcher = DOMAIN_PATTERN.matcher(url);
            if (matcher.matches()) {
                return matcher.group(7);
            }
        }
        return null;
    }

    /**
     * 获取主机域名
     *
     * @param url
     *            ----> URL
     * @return String
     */
    public static String getDomain(String url) {

        return getDomain(url, false);
    }

    /**
     * 获取主机域名
     *
     * @param url
     *            ----> URL
     * @param isFull
     *            ----> 是否获取完整主机域名
     * @return String
     */
    public static String getDomain(String url, boolean isFull) {

        if (null != DOMAIN_PATTERN && StringUtils.isNotBlank(url)) {
            final Matcher matcher = DOMAIN_PATTERN.matcher(url);
            if (matcher.matches()) {
                return matcher.group(isFull ? 1 : 4);
            }
        }
        return null;
    }

    /**
     * 获取一级(主)域名
     *
     * @param host 主机域名
     * @return String
     */
    public static String getPrimaryDomain(String host) {
        return getPrimaryDomain(host, false);
    }

    /**
     * 获取一级(主)域名
     *
     * @param host 主机域名
     * @param isStartWithDot 是否返回以点号开始的主域名
     * @return String
     */
    public static String getPrimaryDomain(String host, boolean isStartWithDot) {
        String pdomain = "";
        if (StringUtils.isNotBlank(host)) {
            Matcher matcher = PRIMARY_DOMAIN_PATTERN.matcher(host);
            while (matcher.find()) {
                pdomain = matcher.group();
                break;
            }
        }
        return isStartWithDot && StringUtils.isNotBlank(pdomain)? "." + pdomain : pdomain;
    }

    /**
     * 获取URI信息
     *
     * @param url
     *            ----> URL
     * @return String
     */
    public static String getURI(String url) {

        if (null != DOMAIN_PATTERN) {
            final Matcher matcher = DOMAIN_PATTERN.matcher(url);
            if (matcher.matches()) {
                String uri = matcher.group(8);
                int index = -1;
                if (StringUtils.isNotBlank(uri)
                        && -1 != (index = uri.indexOf("?"))) {
                    uri = 0 != index ? uri.substring(0, index) : "";
                }
                return uri;
            }
        }
        return null;
    }

    /**
     * 获取查询字符串
     *
     * @param url
     *            ----> URL
     * @return String
     */
    public static String getQueryString(String url) {

        if (null != DOMAIN_PATTERN) {
            final Matcher matcher = DOMAIN_PATTERN.matcher(url);
            if (matcher.matches()) {
                String qs = matcher.group(8);
                int index = -1;
                if (StringUtils.isNotBlank(qs)
                        && -1 != (index = qs.indexOf("?"))) {
                    qs = (index + 1) < qs.length() ? qs.substring(index + 1)
                            : "";
                } else {
                    qs = "";
                }
                return qs;
            }
        }
        return null;
    }

    /**
     * 获取请求地址
     *
     * @param url
     *            ---> URL
     * @return String
     */
    public static String getURL(String url) {

        return getDomain(url, true) + getURI(url);
    }

    /**
     * 获取所有信息
     *
     * @param url
     *            ----> URL
     * @return Map<String, String>
     */
    public static Map<String, String> getFull(String url) {

        final Map<String, String> urlMap = new HashMap<>();
        if (null != DOMAIN_PATTERN) {
            final Matcher matcher = DOMAIN_PATTERN.matcher(url);
            if (matcher.matches()) {
                urlMap.put(PROTOCOL, matcher.group(3));
                urlMap.put(PORT, matcher.group(7));
                urlMap.put(DOMAIN, matcher.group(4));
                urlMap.put(FULLDOMAIN, matcher.group(1));
                urlMap.put(URI, getURI(url));
                urlMap.put(QUERYSTRING, getQueryString(url));
                urlMap.put(URL, getURL(url));
            }
        }
        return urlMap;
    }
}
