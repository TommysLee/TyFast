package com.ty.logic.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ty.cm.utils.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private RedisTemplate redisTemplate;

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
     * @param keys Key集合
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> get(List<String> keys) {
        return this.get(keys, null);
    }

    /**
     * 根据一组Key获取对应数据
     *
     * @param keys          Key集合
     * @param nonExistKeys  存储不存在的Key
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> get(final List<String> keys, List<String> nonExistKeys) {
        final Map<String, Object> dataMap = Maps.newHashMap();
        nonExistKeys = null != nonExistKeys? nonExistKeys : Lists.newArrayList();
        try {
            List<Object> values = valueOperations.multiGet(keys);
            int i = 0;
            for (String k : keys) {
                Object val = values.get(i++);
                if (null != val) {
                    dataMap.put(k, val);
                } else {
                    nonExistKeys.add(k);
                }
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
     * 根据Key，获取所有Hash散列的值
     *
     * @param key Key
     * @return 返回散列集合
     */
    @Override
    public <T> Map<String, T> hgetAll(String key) {
        Map<String, T> dataMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(key)) {
            hashOperations.entries(key).forEach((hk, hv) -> dataMap.put(hk, (T) hv));
        }
        return dataMap;
    }

    /**
     * 根据Key，获取所有Hash散列，并更新有效期
     *
     * @param key Key
     * @param timeout 新有效期(单位秒)
     * @return 返回散列集合
     */
    @Override
    public <T> Map<String, T> hgetAllAndTouch(String key, int timeout) {
        if (this.touch(key, timeout)) {
            return this.hgetAll(key);
        }
        return Maps.newHashMap();
    }

    /**
     * 根据 Key和Field 获取Hash散列的值
     *
     * @param key   Key
     * @param field Hash Key
     * @return T
     */
    @Override
    public <T> T hget(String key, String field) {
        try {
            return (T) hashOperations.get(key, field);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据 Key和一组Field 获取Hash散列的值
     * @param key       Key
     * @param fields    Hash Keys
     * @return Map<String, Object>
     */
    @Override
    public <T> Map<String, T> hget(String key, List<String> fields) {
        return this.hget(key, fields, null);
    }

    /**
     * 根据 Key和一组Field 获取Hash散列的值
     * @param key       Key
     * @param fields    Hash Keys
     * @param nonExistKeys  保存不存在的Hash Keys
     * @return Map<String, Object>
     */
    @Override
    public <T> Map<String, T> hget(String key, List<String> fields, List<String> nonExistKeys) {
        final Map<String, T> dataMap = Maps.newHashMap();
        nonExistKeys = null != nonExistKeys? nonExistKeys : Lists.newArrayList();
        if (null != fields) {
            try {
                List<Object> dataList = hashOperations.multiGet(key, fields);
                for (int i = 0; i < fields.size(); i++) {
                    String hk = fields.get(i);
                    Object hv = dataList.get(i);
                    if (null != hv) {
                        dataMap.put(hk, (T) hv);
                    } else {
                        nonExistKeys.add(hk);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return dataMap;
    }

    /**
     * 根据 Key和Field 获取Hash散列的值，并更新有效期
     *
     * @param key   Key
     * @param field 字段名
     * @param timeout 新有效期(单位秒)
     * @return T
     */
    @Override
    public <T> T hgetAndTouch(String key, String field, int timeout) {
        if (this.touch(key, timeout)) {
            return this.hget(key, field);
        }
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
     * 获取满足条件的Key集合
     *
     * @param pattern
     * @return Set<String>
     */
    @Override
    public Set<String> keys(final String pattern) {
        return this.getClient().keys(pattern);
    }

    /**
     * 查询 Key 是否存在
     *
     * @param key
     * @return boolean
     */
    @Override
    public boolean existKey(String key) {
        Boolean flag = this.getClient().hasKey(key);
        return null != flag? flag : false;
    }

    /**
     * 查询Hash散列对象的 Key 是否存在
     *
     * @param key   Key
     * @param field Hash Key
     * @return boolean
     */
    @Override
    public boolean existHKey(String key, String field) {
        boolean flag = false;
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(field)) {
            flag = hashOperations.hasKey(key, field);
        }
        return flag;
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
     * 保存Hash散列数据
     *
     * @param key       Key
     * @param field     Hash Key
     * @param value     Hash Value
     * @param timeout   有效期(单位秒)
     * @return boolean
     */
    @Override
    public boolean hset(String key, String field, Object value, int timeout) {
        try {
            hashOperations.put(key, field, value);
            if (timeout > 0) {
                this.touch(key, timeout);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 保存Hash散列数据
     *
     * @param key       Key
     * @param dataMap   散列数据集合
     * @param timeout   有效期(单位秒)
     * @return boolean
     */
    @Override
    public boolean hset(String key, Map<String, ?> dataMap, int timeout) {
        return this.hset(key, dataMap, timeout, true);
    }

    /**
     * 保存Hash散列数据
     *
     * @param key       Key
     * @param dataMap   散列数据集合
     * @param timeout   有效期(单位秒)
     * @param isAppend  是否为Append模式
     * @return boolean
     */
    @Override
    public boolean hset(String key, Map<String, ?> dataMap, int timeout, boolean isAppend) {
        try {
            if (StringUtils.isNotBlank(key) && null != dataMap) {
                if (!isAppend) { // 非Append模式，则完全删除原有数据
                    this.delete(key);
                }

                hashOperations.putAll(key, dataMap);
                if (timeout > 0) {
                    this.touch(key, timeout);
                }
                return true;
            }
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
     * 添加Hash散列数据(当且仅当Hash Key不存在时，添加成功)
     *
     * @param key       Key
     * @param field     Hash Key
     * @param value     Hash Value
     * @param timeout   有效期(单位秒)
     * @return boolean
     */
    @Override
    public boolean hadd(String key, String field, Object value, int timeout) {
        try {
            hashOperations.putIfAbsent(key, field, value);
            if (timeout > 0) {
                this.touch(key, timeout);
            }
            return true;
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
     * 根据 Key和Field 删除Hash散列
     *
     * @param key Key
     * @param fields Hash Keys
     */
    @Override
    public void hdelete(String key, String... fields) {
        if (StringUtils.isNotBlank(key) && null != fields && fields.length > 0) {
            hashOperations.delete(key, fields);
        }
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
        return redisTemplate;
    }
}
