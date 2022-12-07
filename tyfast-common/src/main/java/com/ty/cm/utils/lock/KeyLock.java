package com.ty.cm.utils.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Key锁（更加细粒度的资源控制锁）
 *
 * @Author Tommy
 * @Date 2022/12/7
 */
@Slf4j
public class KeyLock {

    /** 资源锁库 **/
    private static final ConcurrentMap<String, ReentrantLock> LOCKS_MAP = new ConcurrentHashMap<String, ReentrantLock>();

    /** 资源互斥锁 **/
    private static final ReentrantLock RLOCK = new ReentrantLock();

    /**
     * 加锁
     *
     * <p>
     *     全局排它锁，若锁被其它线程持有，则一直等待，直到拿到锁
     * </p>
     *
     * @return boolean
     */
    public boolean lock() {
        RLOCK.lock();
        return true;
    }

    /**
     * 加锁(细粒度的资源锁)
     *
     * @param key 资源标识
     * @return boolean
     */
    public boolean lock(String key) {
        if (StringUtils.isNotBlank(key)) {
            // 若此资源不存在锁，则创建资源锁
            ReentrantLock lock = new ReentrantLock();
            LOCKS_MAP.putIfAbsent(key, lock);
            lock = this.get(key);

            // 加锁
            lock.lock();

            // 若资源锁库删除了此锁，则释放该锁
            if (null == this.get(key)) {
                this.release(lock);
                return false;
            }
        }
        return true;
    }

    /**
     * 解锁
     *
     * <p>
     *     释放全局排它锁
     * </p>
     *
     * @return boolean
     */
    public boolean unlock() {
        return this.release(RLOCK);
    }

    /**
     * 解锁
     *
     * <p>
     *     解除细粒度的资源锁
     * </p>
     *
     * @param key 资源标识
     * @return boolean
     */
    public boolean unlock(String key) {
        boolean flag = false;
        if (StringUtils.isNotBlank(key)) {
            final ReentrantLock lock = this.get(key);
            try {
                flag = this.release(lock);
            } catch (Exception e) {
                LOCKS_MAP.remove(key);
                log.error("为避免死锁，强制移除资源锁【key=" + key + "】" + e.getMessage(), e);
            }
        }
        return flag;
    }

    /**
     * 解锁
     *
     * <p>
     *     解除细粒度的资源锁
     * </p>
     *
     * @param key       资源标识
     * @param isClear   是否清除锁的副本
     * @return boolean
     */
    public boolean unlock(String key, boolean isClear) {
        boolean flag = this.unlock(key);
        if (flag) { // 解锁成功后，再判定是否清除锁副本
            final ReentrantLock lock = this.get(key);
            if (isClear && null != lock && !lock.hasQueuedThreads()) { // 若没有其它线程正在等待获取此锁时，则彻底删除锁
                this.clear(key);
            }
        }
        return flag;
    }

    /**
     * 强制删除锁
     *
     * @param keys 一组资源锁标识（（若此参数为空，则清除所有的资源锁））
     */
    public void clear(String ... keys) {
        try {
            if (null == keys || keys.length < 1) {
                LOCKS_MAP.clear();
            } else {
                for (String key : keys) {
                    LOCKS_MAP.remove(key);
                }
            }
        } catch (Exception e) {
            log.error("强制删除锁异常：" + e.getMessage(), e);
        }
    }

    /**
     * 获取资源锁
     *
     * @param key 资源标识
     * @return T
     */
    @SuppressWarnings("unchecked")
    private <T> T get(String key) {
        return (T) LOCKS_MAP.get(key);
    }

    /**
     * 释放锁
     *
     * @param lock
     * @return boolean
     */
    private boolean release(final ReentrantLock lock) {
        boolean flag = false;
        if (null != lock && lock.isHeldByCurrentThread()) {
            lock.unlock();
            flag = true;
        }
        return flag;
    }

}
