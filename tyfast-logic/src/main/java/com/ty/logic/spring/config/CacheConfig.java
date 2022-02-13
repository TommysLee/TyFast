package com.ty.logic.spring.config;

import com.ty.cm.utils.cache.Cache;
import com.ty.logic.cache.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存体系初始化配置类
 *
 * @Author Tommy
 * @Date 2022/2/2
 */
@Configuration
@Slf4j
public class CacheConfig {

    /**
     * 初始化缓存实例
     */
    @Bean
    public Cache cache() {
        log.info("缓存抽象工厂的具体实例：Redis 初始化完毕");
        return new RedisCache();
    }
}
