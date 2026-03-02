package com.star.order.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 *
 * 作用：告诉 Redisson 连哪台 Redis，然后把 RedissonClient 注册成 Spring Bean，
 * 这样在其他类里就可以直接 @Autowired 或用构造器注入使用了。
 *
 * 看门狗（WatchDog）说明：
 * 当你用 lock.tryLock(等待时间, -1, 时间单位) 时，第二个参数传 -1，
 * Redisson 就会启动看门狗：每隔 10 秒自动把锁的过期时间续回 30 秒，
 * 直到你主动调用 lock.unlock() 才停止续期并删掉锁。
 * 这样业务执行多久，锁就能活多久，不会中途失效。
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
