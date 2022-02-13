package com.ty.cm.utils.cache;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于内存的简单缓存实现
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@SuppressWarnings("unchecked")
public class MemoryCache implements Cache {

    private final ConcurrentMap<String, Object> memoryData = Maps.newConcurrentMap();

    /**
     * 获取所有数据
     *
     * @return T
     */
    @Override
    public Map<String, Object> getAll() {
        return memoryData;
    }

    /**
     * 根据Key获取值
     *
     * @param key ----> Key
     * @return T
     */
    @Override
    public <T> T get(final String key) {
        if (null != key) {
            return (T) memoryData.get(key);
        } else {
            return null;
        }
    }

    /**
     * 添加数据(当且仅当Key不存在时，添加成功)
     *
     * @param key   ----> Key
     * @param value ----> 数据
     * @return boolean
     */
    @Override
    public boolean add(final String key, final Object value) {
        boolean result = false;
        if (null != key && null != value) {
            memoryData.putIfAbsent(key, value);
            result = true;
        }
        return result;
    }

    /**
     * 根据Key更新数据(当且仅当Key存在时，更新成功)
     *
     * @param key   ----> Key
     * @param value ----> 数据
     * @return boolean
     */
    @Override
    public boolean replace(final String key, final Object value) {
        boolean result = false;
        if (null != key && null != value) {
            result = null != memoryData.replace(key, value);
        }
        return result;
    }

    /**
     * 根据Key删除数据
     *
     * @param key
     *            ----> Key
     * @return boolean
     */
    public boolean delete(final String key) {
        if (null != key) {
            memoryData.remove(key);
        }
        return true;
    }

    /**
     * 删除所有数据
     *
     * @return boolean
     */
    public boolean deleteAll() {
        memoryData.clear();
        return true;
    }
}
