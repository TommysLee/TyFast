package com.ty.cm.utils.uusn;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * UUSN工具类，简化使用
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public class UUSNUtil {

    // UUSN实例
    private static final UUSN _UUSN;

    static {
        _UUSN = new SimpleUUSN();
    }

    /**
     * 生成UUSN序列号
     *
     * @return String
     */
    public static String nextUUSN() {
        return _UUSN.nextUUSN();
    }

    /**
     * 生成Java UUID
     */
    public static String javaUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("\\-", StringUtils.EMPTY);
    }
}
