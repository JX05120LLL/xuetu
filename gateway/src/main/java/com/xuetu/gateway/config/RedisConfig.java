package com.xuetu.gateway.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类 - 网关专用
 * 
 * 用途：
 * 1. 限流计数器存储
 * 2. JWT黑名单存储  
 * 3. 缓存路由配置
 * 4. 存储用户会话信息
 * 
 * @author star
 */
@Slf4j
@Configuration
public class RedisConfig {

    /**
     * 配置RedisTemplate
     * 
     * 网关Redis主要用于：
     * - 限流计数器（简单的数字存储）
     * - JWT黑名单（字符串存储）
     * - 临时缓存数据
     * 
     * @param factory Redis连接工厂
     * @return 配置好的RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        log.info("初始化网关Redis配置...");
        
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Spring Data Redis 3.x：通过构造器传入 ObjectMapper（setObjectMapper 已废弃）
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(om, Object.class);

        // 配置字符串序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 设置各种序列化方式
        template.setKeySerializer(stringRedisSerializer);           // key用字符串序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);   // value用JSON序列化
        template.setHashKeySerializer(stringRedisSerializer);       // hash key用字符串序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer); // hash value用JSON序列化
        
        // 初始化配置
        template.afterPropertiesSet();
        
        log.info("网关Redis配置初始化完成");
        return template;
    }
}