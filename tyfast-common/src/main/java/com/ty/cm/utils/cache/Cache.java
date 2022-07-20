package com.ty.cm.utils.cache;

import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

/**
 * 缓存管理规范
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
public interface Cache {

    /**
     * 获取所有数据
     *
     * @return T
     */
    public default <T> T getAll() {
        return null;
    }

    /**
     * 根据Key获取数据
     *
     * @param key
     *            ----> Key
     * @return T
     */
    public <T> T get(final String key);

    /**
     * 根据一组Key获取对应数据
     *
     * @param keys
     *             ----> Key数组
     * @return Map<String, Object>
     */
    public default Map<String, Object> get(final String... keys) {
        return null;
    }

    /**
     * 根据Key获取数据并更新有效期
     *
     * @param key
     *               ----> Key
     * @param timeout
     *               ----> 新有效期(单位秒)
     * @return T
     */
    public default <T> T getAndTouch(final String key, int timeout) {
        return null;
    }

    /**
     * 根据Key更新有效期
     *
     * @param key
     *               ----> Key
     * @param timeout
     *               ----> 新有效期(单位秒)
     * @return boolean
     */
    public default boolean touch(final String key, int timeout) {
        return false;
    }

    /**
     * 获取满足条件的Key集合
     *
     * @param pattern
     * @return Set<String>
     */
    public default Set<String> keys(final String pattern) {
        return Sets.newHashSet();
    }

    /**
     * 保存数据
     *
     * @param key
     *              ----> Key
     * @param value
     *              ----> 数据
     * @return boolean
     */
    public default boolean set(final String key, final Object value) {
        return false;
    }

    /**
     * 保存数据
     *
     * @param key
     *              ----> Key
     * @param value
     *              ----> 数据
     * @param timeout
     *              ----> 有效期(单位秒)
     * @return boolean
     */
    public default boolean set(final String key, final Object value, final int timeout) {
        return false;
    }

    /**
     * 添加数据(当且仅当Key不存在时，添加成功)
     *
     * @param key
     *              ----> Key
     * @param value
     *              ----> 数据
     * @return boolean
     */
    public default boolean add(final String key, final Object value) {
        return false;
    }

    /**
     * 添加数据(当且仅当Key不存在时，添加成功)
     *
     * @param key
     *              ----> Key
     * @param value
     *              ----> 数据
     * @param timeout
     *              ----> 有效期(单位秒)
     * @return boolean
     */
    public default boolean add(final String key, final Object value, final int timeout) {
        return false;
    }

    /**
     * 根据Key更新数据(当且仅当Key存在时，更新成功)
     *
     * @param key
     *              ----> Key
     * @param value
     *              ----> 数据
     * @return boolean
     */
    public default boolean replace(final String key, final Object value) {
        return false;
    }

    /**
     * 根据Key更新数据(当且仅当Key存在时，更新成功)
     *
     * @param key
     *              ----> Key
     * @param value
     *              ----> 数据
     * @param timeout
     *              ----> 有效期(单位秒)
     * @return boolean
     */
    public default boolean replace(final String key, final Object value, final int timeout) {
        return false;
    }

    /**
     * 根据Key删除数据
     *
     * @param key
     *            ----> Key
     * @return boolean
     */
    public boolean delete(final String key);

    /**
     * 根据Key删除数据(无需等待返回结果)
     *
     * @param key
     *            ----> Key
     */
    public default void deleteWithNoReply(final String key) { }

    /**
     * 根据一组Key删除对应数据
     *
     * @param keys
     *             ----> Key数组
     * @return boolean
     */
    public default boolean delete(final String... keys) {
        return false;
    }

    /**
     * 根据一组Key删除对应数据(无需等待返回结果)
     *
     * @param keys
     *             ----> Key数组
     */
    public default void deleteWithNoReply(final String... keys) { }

    /**
     * 删除所有数据
     *
     * @return boolean
     */
    public default boolean deleteAll() {
        return false;
    }

    /**
     * Get Cache Client
     *
     * @return T
     */
    public default <T> T getClient() {
        return null;
    }
}
