package com.ty.cm.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

import static com.ty.cm.constant.Ty.DEFAULT_CHARSET;

/**
 * Base64编码工具类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Slf4j
public class Base64Util {

    /**
     * Base64编码
     *
     * @param b 需要编码的字节数组
     * @return String
     */
    public static String encode(byte[] b) {
        return Base64.encodeBase64String(b);
    }

    /**
     * Base64编码
     *
     * @param text 需要编码的字符串
     * @return String
     */
    public static String encode(String text) {
        String result = null;
        try {
            result = Base64.encodeBase64String(text.getBytes(DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 使用Base64算法的URL-safe模式编码
     *
     * @param text 需要编码的字符串
     * @return String
     */
    public static String encodeSafe(String text) {
        String result = null;
        try {
            result = Base64.encodeBase64URLSafeString(text.getBytes(DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Base64解码
     *
     * @param b 需要解码的字节数组
     * @return String
     */
    public static byte[] decode(byte[] b) {
        return Base64.decodeBase64(b);
    }

    /**
     * Base64解码
     *
     * @param text 需要解码的字符串
     * @return String
     */
    public static String decode(String text) {
        String result = null;
        try {
            result = new String(Base64.decodeBase64(text), DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}
