package com.ty.cm.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程局部变量上下文工具类
 *
 * @Author Tommy
 * @Date 2025/9/1
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ThreadLocalContext {

    private static final ThreadLocal threadLocal = new ThreadLocal();
    private static final ConcurrentHashMap<String, ThreadLocal> threadLocalVars = new ConcurrentHashMap<>();

    /**
     * 设置线程本地变量值
     *
     * @param val 值
     */
    public static void set(Object val) {
        threadLocal.set(val);
    }

    /**
     * 设置线程本地变量值
     *
     * @param key Key
     * @param val 值
     */
    public static void set(String key, Object val) {
        ThreadLocal local = new ThreadLocal();
        local.set(val);
        threadLocalVars.put(key, local);
    }

    /**
     * 获取线程本地变量值
     *
     * @return 返回值
     */
    public static <T> T get() {
        return (T) threadLocal.get();
    }

    /**
     * 获取线程本地变量值
     *
     * @param key Key
     * @return 返回值
     */
    public static <T> T get(String key) {
        if (threadLocalVars.containsKey(key)) {
            return (T) threadLocalVars.get(key).get();
        }
        return null;
    }

    /**
     * 删除线程本地变量值
     */
    public static void remove() {
        threadLocal.remove();
    }

    /**
     * 删除线程本地变量值
     *
     * @param key Key
     */
    public static void remove(String key) {
        if (threadLocalVars.containsKey(key)) {
            threadLocalVars.get(key).remove();
        }
    }
}
