package com.ty.logic.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ty.cm.utils.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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
     * 根据Key，获取所有Hash散列
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
     * 根据 Key和一组Field 获取Hash散列
     * @param key       Key
     * @param fields    Hash Keys
     * @return Map<String, T>
     */
    @Override
    public <T> Map<String, T> hget(String key, Set<String> fields) {
        return this.hget(key, fields, null);
    }

    /**
     * 根据 Key和一组Field 获取Hash散列
     * @param key       Key
     * @param fields    Hash Keys
     * @param nonExistKeys  保存不存在的Hash Keys
     * @return Map<String, T>
     */
    @Override
    public <T> Map<String, T> hget(String key, Set<String> fields, Set<String> nonExistKeys) {
        final Map<String, T> dataMap = Maps.newHashMap();
        nonExistKeys = null != nonExistKeys? nonExistKeys : Sets.newHashSet();
        if (null != fields) {
            try {
                List<String> fieldList = Lists.newArrayList(fields);
                List<Object> dataList = hashOperations.multiGet(key, fieldList);
                for (int i = 0; i < fieldList.size(); i++) {
                    String hk = fieldList.get(i);
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
     * 根据 Key和一组Field 获取Hash散列，并更新有效期
     * @param key       Key
     * @param fields    Hash Keys
     * @param timeout   新有效期(单位秒)
     * @return Map<String, T>
     */
    @Override
    public <T> Map<String, T> hgetAndTouch(String key, Set<String> fields, int timeout) {
        if (this.touch(key, timeout)) {
            return this.hget(key, fields);
        }
        return Maps.newHashMap();
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
     * 获取Hash散列对象的所有key
     *
     * @param key Key
     * @return Set<String>
     */
    @Override
    public Set<String> hkeys(final String key) {
        try {
            return hashOperations.keys(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Sets.newHashSet();
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
     * @return boolean
     */
    @Override
    public boolean set(final String key, final Object value) {
        valueOperations.set(key, value);
        return true;
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
        valueOperations.set(key, value, timeout, TimeUnit.SECONDS);
        return true;
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
        hashOperations.put(key, field, value);
        if (timeout > 0) {
            this.touch(key, timeout);
        }
        return true;
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
        return Boolean.TRUE.equals(valueOperations.setIfAbsent(key, value, timeout, TimeUnit.SECONDS));
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
        hashOperations.putIfAbsent(key, field, value);
        if (timeout > 0) {
            this.touch(key, timeout);
        }
        return true;
    }

    /**
     * 根据Key删除数据
     *
     * @param key Key
     * @return boolean
     */
    @Override
    public boolean delete(final String key) {
        return Boolean.TRUE.equals(this.getClient().delete(key));
    }

    /**
     * 根据一组Key删除对应数据
     *
     * @param keys Key数组
     * @return boolean
     */
    @Override
    public boolean delete(final String... keys) {
        if (null != keys && keys.length > 0) {
            Long delCounts = this.getClient().delete(Lists.newArrayList(keys));
        }
        return true;
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
     * 批处理
     *
     * @param sup 函数式接口
     * @return 返回批处理结果
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object batch(Supplier sup) {
        return redisTemplate.execute(new SessionCallback<List>() {
            @Override
            public List execute(RedisOperations operations) throws DataAccessException {
                // 批处理开始
                redisTemplate.multi();

                // 执行函数式接口
                sup.get();

                // 执行批处理
                return redisTemplate.exec();
            }
        });
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
