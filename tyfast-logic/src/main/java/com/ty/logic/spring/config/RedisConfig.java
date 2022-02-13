package com.ty.logic.spring.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

/**
 * Redis配置类
 *
 * @Author Tommy
 * @Date 2022/2/2
 */
@Configuration
@ConditionalOnClass(RedisOperations.class)
@Slf4j
public class RedisConfig {

    // 日期格式
    private String dateFormatPattern = "yyyy-MM-dd HH:mm:ss";

    /**
     * Redis Jackson序列化与反序列化
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean("redisTemplate")
    public RedisTemplate<Object, Object> redisTemplateForJackson(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 更改序列化为：Jackson
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.setDefaultPropertyInclusion(Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 反序列化时：忽略未知属性
        objectMapper.configure(MapperFeature.USE_GETTERS_AS_SETTERS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(dateFormatPattern));
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 重点在这四行代码（解决key-value乱码问题/二进制问题）
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();

        log.info("Redis (Jackson序列化方式) 初始化完毕.");
        return template;
    }
}
