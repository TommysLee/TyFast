package com.ty.cm.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程工具类
 *
 * @Author Tommy
 * @Date 2025/9/1
 */
@Slf4j
public class ThreadUtil {

    /**
     * 让当前线程休眠
     *
     * @param seconds 休眠的时间，单位：秒
     */
    public static void sleep(int seconds) {
        seconds = Math.max(seconds, 1);
        try {
            log.info("线程休眠 {} 秒", seconds);
            Thread.sleep(seconds * 1000L);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
