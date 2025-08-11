package com.xuetu.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关配置类
 * 
 * 功能：
 * 1. 配置限流key解析器 - 解决Spring Cloud Gateway限流Bean冲突
 * 2. 配置路由相关Bean
 * 3. 其他网关组件配置
 * 
 * 重要说明：
 * - 定义了3个KeyResolver，用@Primary指定默认使用的
 * - 可以通过@Qualifier注解在需要时指定具体使用哪个KeyResolver
 * - userKeyResolver被设为@Primary，因为它能智能处理登录和游客用户
 * 
 * @author star
 */
@Slf4j
@Configuration
public class GatewayConfig {

    /**
     * 基于IP的限流key解析器
     * 
     * 特点：
     * 1. 所有来自同一IP的请求共享同一个限流配额
     * 2. 适用于按IP进行全局限流的场景
     * 3. 可通过@Qualifier("ipKeyResolver")指定使用
     * 
     * 作用：为Spring Cloud Gateway的RequestRateLimiter提供key
     * 虽然我们使用了自定义的限流过滤器，但保留这个配置以备后用
     */
    @Bean("ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            // 获取客户端IP作为限流key
            String clientIp = getClientIp(exchange);
            log.debug("IP限流key: {}", clientIp);
            return Mono.just(clientIp);
        };
    }

    /**
     * 基于用户ID的限流key解析器（主要解析器）
     * 
     * 特点：
     * 1. 对登录用户使用用户ID进行限流
     * 2. 对游客用户使用IP进行限流
     * 3. 使用@Primary注解解决多Bean冲突
     * 
     * @Primary 注解表示这是默认的KeyResolver，解决Bean冲突
     */
    @Bean("userKeyResolver")
    @Primary  // 设置为主要的KeyResolver，解决Bean冲突
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 从请求头获取用户ID
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null) {
                log.debug("用户限流key: {}", userId);
                return Mono.just("user:" + userId);
            } else {
                // 未登录用户使用IP
                String clientIp = getClientIp(exchange);
                log.debug("游客限流key: {}", clientIp);
                return Mono.just("guest:" + clientIp);
            }
        };
    }

    /**
     * 基于路径的限流key解析器
     * 
     * 特点：
     * 1. 不同路径有独立的限流配额
     * 2. 适用于按API接口进行限流的场景
     * 3. 可通过@Qualifier("pathKeyResolver")指定使用
     * 
     * 使用场景：
     * - 对不同的API接口设置不同的访问频率限制
     * - 保护重要接口免受高频访问影响
     */
    @Bean("pathKeyResolver")
    public KeyResolver pathKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getURI().getPath();
            log.debug("路径限流key: {}", path);
            return Mono.just(path);
        };
    }

    /**
     * 获取客户端IP地址
     * 
     * @param exchange 请求交换对象
     * @return 客户端IP
     */
    private String getClientIp(ServerWebExchange exchange) {
        // 1. 从X-Forwarded-For头获取
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        // 2. 从X-Real-IP头获取
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        // 3. 从远程地址获取
        return exchange.getRequest().getRemoteAddress() != null ? 
               exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }
}

/**
 * KeyResolver使用说明：
 * 
 * 1. 默认使用userKeyResolver（带@Primary注解）
 *    - Spring会自动注入userKeyResolver作为默认的KeyResolver
 * 
 * 2. 手动指定特定的KeyResolver：
 *    @Autowired
 *    @Qualifier("ipKeyResolver")
 *    private KeyResolver ipKeyResolver;
 * 
 * 3. 在配置中指定KeyResolver：
 *    routes:
 *      - id: test-route
 *        uri: lb://test-service
 *        filters:
 *          - name: RequestRateLimiter
 *            args:
 *              key-resolver: "#{@ipKeyResolver}"  # 指定使用IP限流
 *              redis-rate-limiter.replenish-rate: 10
 *              redis-rate-limiter.burst-capacity: 20
 * 
 * 4. Bean冲突解决方案总结：
 *    - 方案1：使用@Primary指定默认Bean（当前采用）
 *    - 方案2：使用@Qualifier明确指定要注入的Bean
 *    - 方案3：只定义一个KeyResolver Bean
 */