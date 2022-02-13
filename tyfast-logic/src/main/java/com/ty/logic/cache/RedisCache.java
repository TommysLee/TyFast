package com.ty.logic.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ty.cm.utils.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的缓存管理
 *
 * @Author Tommy
 * @Date 2022/2/2
 */
@Slf4j
@SuppressWarnings("unchecked")
public class RedisCache implements Cache {

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Object> valueOperations;

    /**
     * 根据Key获取数据
     *
     * @param key Key
     * @return T
     */
    @Override
    public <T> T get(final String key) {
        try {
            return (T) valueOperations.get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据一组Key获取对应数据
     *
     * @param keys Key数组
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> get(final String... keys) {
        final Map<String, Object> dataMap = Maps.newHashMap();
        try {
            List<Object> values = valueOperations.multiGet(Lists.newArrayList(keys));
            int i = 0;
            for (String k : keys) {
                dataMap.put(k, values.get(i++));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return dataMap;
    }

    /**
     * 根据Key获取值并更新有效期
     *
     * @param key     Key
     * @param timeout 新有效期(单位秒)
     * @return T
     */
    @Override
    public <T> T getAndTouch(final String key, int timeout) {
        if (this.touch(key, timeout))
            return this.get(key);
        return null;
    }

    /**
     * 根据Key更新有效期
     *
     * @param key     Key
     * @param timeout 新有效期(单位秒)
     * @return boolean
     */
    @Override
    public boolean touch(final String key, int timeout) {
        Boolean result = false;
        try {
            result = this.getClient().expire(key, timeout, TimeUnit.SECONDS);
            result = null != result ? result : true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 保存数据
     *
     * @param key     Key
     * @param value   数据
     * @param timeout 有效期(单位秒)
     * @return boolean
     */
    @Override
    public boolean set(final String key, final Object value, final int timeout) {
        try {
            valueOperations.set(key, value, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 添加数据(当且仅当Key不存在时，添加成功)
     *
     * @param key     Key
     * @param value   数据
     * @param timeout 有效期(单位秒)
     * @return boolean
     */
    @Override
    public boolean add(final String key, final Object value, final int timeout) {
        try {
            return valueOperations.setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 根据Key更新数据(当且仅当Key存在时，更新成功)
     *
     * @param key     Key
     * @param value   数据
     * @param timeout 有效期(单位秒)
     * @return boolean
     */
    @Override
    public boolean replace(final String key, final Object value, final int timeout) {
        try {
            return valueOperations.setIfPresent(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 根据Key删除数据
     *
     * @param key Key
     * @return boolean
     */
    @Override
    public boolean delete(final String key) {
        try {
            return this.getClient().delete(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 根据Key删除数据(无需等待返回结果)【Redis无此功能，调用效果同delete】
     *
     * @param key Key
     */
    @Override
    public void deleteWithNoReply(final String key) {
        this.delete(key);
    }

    /**
     * 根据一组Key删除对应数据
     *
     * @param keys Key数组
     * @return boolean
     */
    @SuppressWarnings("unused")
    @Override
    public boolean delete(final String... keys) {
        boolean flag = false;
        if (null != keys && keys.length > 0) {
            try {
                Long delCounts = this.getClient().delete(Lists.newArrayList(keys));
                flag = true;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return flag;
    }

    /**
     * 根据一组Key删除对应数据(无需等待返回结果)【Redis无此功能，调用效果同delete】
     *
     * @param keys Key数组
     */
    @Override
    public void deleteWithNoReply(final String... keys) {
        this.delete(keys);
    }

    /**
     * Get Cache Client
     *
     * @return RedisTemplate<String, Object>
     */
    @Override
    public RedisTemplate<String, Object> getClient() {
        return (RedisTemplate<String, Object>) valueOperations.getOperations();
    }
}
