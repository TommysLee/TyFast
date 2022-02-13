package com.ty.web.utils;

import com.ty.api.model.system.SysUser;
import com.ty.cm.utils.DataUtil;
import com.ty.web.shiro.AuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static com.ty.cm.constant.Ty.DEFAULT_CHARSET;
import static com.ty.cm.constant.Ty.NIL;

/**
 * Web工具类
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@Slf4j
public class WebUtil {

    /** Cookie 默认域名 **/
    private static final String CK_DEFAULT_DOMAIN = "";

    /** Cooike 默认路径 **/
    private static final String CK_DEFAULT_PATH = "/";

    /** Cooike 默认HttpOnly **/
    private static final boolean CK_DEFAULT_HTTP_ONLY = false;

    /**
     * 是否为异步请求
     *
     * @param request
     * @return boolean
     */
    public static boolean isAjax(HttpServletRequest request) {

        final String requestType = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestType);
    }

    /**
     * 禁用HTTP缓存
     *
     * @param response
     */
    public static void disableHttpCache(HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragrma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    /**
     * URL Encode编码字符串
     *
     * @param text
     * @return String
     */
    public static String encodeU8(String text) {

        String result = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(text)) {
            try {
                result = URLEncoder.encode(text, DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException ex) {
                log.error("URL Encode 编码字符串 \" " + text + "\"错误！", ex);
            }
        }
        return result;
    }

    /**
     * URL Decode解码字符串
     *
     * @param encodeText
     * @return String
     */
    public static String decodeU8(String encodeText) {

        String result = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(encodeText)) {
            try {
                result = URLDecoder.decode(encodeText, DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException ex) {
                log.error("URL Encode 解码字符串 \" " + encodeText + "\"错误！", ex);
            }
        }
        return result;
    }

    /**
     * 发送错误状态
     *
     * @param response
     * @param statusCode
     */
    public static void sendError(HttpServletResponse response, int statusCode) throws IOException {

        response.sendError(statusCode);
    }

    /**
     * 发送错误状态
     *
     * @param response
     * @param statusCode
     * @param message
     */
    public static void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {

        response.setStatus(statusCode);
        writeText(response, message);
    }

    /**
     * 重定向到指定URL
     *
     * @param url
     * @param response
     */
    public static void redirect(String url, HttpServletResponse response) {

        if (StringUtils.isNotBlank(url)) {
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 跳转到指定URL
     *
     * @param url
     * @param request
     */
    public static void forward(String url, HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isNotBlank(url)) {
            try {
                request.getRequestDispatcher(url).forward(request, response);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 将字符串写入到输出流
     *
     * @param response
     * @param text
     * @throws IOException
     */
    public static void writeText(HttpServletResponse response, String text) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(text);
    }

    /**
     * 将JSON写入到输出流
     *
     * @param response
     * @param jsonObject
     * @throws IOException
     */
    public static void writeJSON(HttpServletResponse response, Object jsonObject) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(DataUtil.toJSON(jsonObject));
    }

    /**
     * 将JSON对象以JSONP方式写入到response
     *
     * @param response
     * @param funcName
     * @param jsonObject
     * @throws Exception
     */
    public static void writeJSONP(HttpServletResponse response, String funcName, Object jsonObject) throws IOException {

        String text = DataUtil.toJSON(jsonObject);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(funcName + "(" + text + ");");
    }

    /**
     * 生成Session ID
     *
     * @return String
     */
    public static String generateSessionId() {

        final JavaUuidSessionIdGenerator uuidGen = new JavaUuidSessionIdGenerator();
        String sessionId = uuidGen.generateId(null).toString();
        sessionId = sessionId.replaceAll("\\-", StringUtils.EMPTY);
        return sessionId;
    }

    /**
     * 获取User Agent
     *
     * @return String
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 保存COOKIE
     *
     * @param name
     * @param value
     * @param request
     * @param response
     */
    public static void saveCookie(String name, String value,  HttpServletRequest request, HttpServletResponse response) {
        saveCookie(name, value, SimpleCookie.DEFAULT_MAX_AGE, request, response);
    }

    /**
     * 保存COOKIE
     *
     * @param name
     * @param value
     * @param maxAge
     * @param request
     * @param response
     */
    public static void saveCookie(String name, String value, int maxAge, HttpServletRequest request, HttpServletResponse response) {
        saveCookie(name, value, maxAge, CK_DEFAULT_PATH, request, response);
    }

    /**
     * 保存COOKIE
     *
     * @param name
     * @param value
     * @param maxAge
     * @param path
     * @param request
     * @param response
     */
    public static void saveCookie(String name, String value, int maxAge, String path, HttpServletRequest request, HttpServletResponse response) {
        saveCookie(name, value, maxAge, path, CK_DEFAULT_DOMAIN, request, response);
    }

    /**
     * 保存COOKIE
     *
     * @param name
     * @param value
     * @param maxAge
     * @param path
     * @param domain
     * @param request
     * @param response
     */
    public static void saveCookie(String name, String value, int maxAge, String path, String domain, HttpServletRequest request, HttpServletResponse response) {

        final SimpleCookie cookie = new SimpleCookie(name);
        cookie.setValue(value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setHttpOnly(CK_DEFAULT_HTTP_ONLY);
        cookie.saveTo(request, response);
    }

    /**
     * 删除Cookie
     *
     * @param name
     * @param request
     * @param response
     */
    public static void removeCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        removeCookie(name, CK_DEFAULT_PATH, request, response);
    }

    /**
     * 删除Cookie
     *
     * @param name
     * @param path
     * @param request
     * @param response
     */
    public static void removeCookie(String name, String path, HttpServletRequest request, HttpServletResponse response) {
        removeCookie(name, path, CK_DEFAULT_DOMAIN, request, response);
    }

    /**
     * 删除Cookie
     *
     * @param name
     * @param path
     * @param domain
     * @param request
     * @param response
     */
    public static void removeCookie(String name, String path, String domain, HttpServletRequest request, HttpServletResponse response) {

        final SimpleCookie cookie = new SimpleCookie(name);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.removeFrom(request, response);
    }

    /**
     * 删除所有Cookie
     *
     * @param request
     * @param response
     */
    public static void removeAllCookie(HttpServletRequest request, HttpServletResponse response) {

        final Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie ck : cookies) {
                ck.setMaxAge(0);
                response.addCookie(ck);
                removeCookie(ck.getName(), request, response);
            }
        }
    }

    /**
     * 读取Cookie值
     *
     * @param name
     * @param request
     * @return String
     */
    public static String readCookie(String name, HttpServletRequest request) {

        String value = null;
        if (StringUtils.isNotBlank(name)) {
            final SimpleCookie cookie = new SimpleCookie(name);
            value = cookie.readValue(request, null);
        }
        return value;
    }

    /**
     * 获取HttpServletRequest
     */
    public static HttpServletRequest getHttpRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取Http Session
     */
    public static HttpSession getHttpSession() {
        return getHttpRequest().getSession();
    }

    /**
     * 获取Shiro Subject
     */
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 获取Shiro Session（Session门面模式：依据Shiro配置，可为集群Session 或 单机Session）
     */
    public static Session getSession() {
        return getSubject().getSession();
    }

    /**
     * 获取Shiro Session属性
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(String key) {
        return (T) getSession().getAttribute(key);
    }

    /**
     * 获取当前账户
     *
     * @return <T> T
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCurrentAccount() {
        return (T) getSubject().getPrincipal();
    }

    /**
     * 获取当前用户ID
     *
     * @return String
     */
    public static String getCurrentUserId() {

        String userId = NIL;
        final Object account = getCurrentAccount();
        if (account instanceof SysUser) {
            userId = ((SysUser) account).getUserId();
        }
        return userId;
    }

    /**
     * Shiro 登录
     *
     * @param token
     */
    public static void login(AuthenticationToken token) throws AuthenticationException {
        getSubject().login(token);
    }

    /**
     * Shiro 注销登录
     */
    public static void logout() {
        getSubject().logout();
    }
}
