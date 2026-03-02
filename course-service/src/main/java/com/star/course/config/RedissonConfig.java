package com.star.course.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 *
 * 作用：读取 application.yml 里的 Redis 连接信息，
 * 创建 RedissonClient Bean，供其他类注入使用。
 *
 * 相比普通 RedisTemplate，Redisson 额外提供了：
 *   - 分布式锁（带看门狗自动续期，防止锁提前失效）
 *   - 分布式限流器、信号量等高级功能
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.database}")
    private int database;

    /**
     * destroyMethod = "shutdown"：Spring 容器关闭时，自动关闭 Redisson 连接，
     * 防止资源泄漏。
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database)
                .setPassword(password.isEmpty() ? null : password)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(2);
        return Redisson.create(config);
    }
}
