package com.xuetu.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 限流过滤器
 * 
 * 功能：
 * 1. 基于Redis实现分布式限流
 * 2. 按IP地址进行限流
 * 3. 按用户ID进行限流
 * 4. 可配置限流规则
 * 
 * 限流策略：
 * - 游客：每分钟60次请求
 * - 登录用户：每分钟200次请求
 * - 管理员：不限制
 * 
 * @author star
 */
@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 限流配置
     */
    private static final int GUEST_LIMIT = 60;        // 游客每分钟限制
    private static final int USER_LIMIT = 200;        // 用户每分钟限制
    private static final int TIME_WINDOW = 60;        // 时间窗口（秒）
    
    /**
     * Redis key前缀
     */
    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 1. 获取客户端IP
        String clientIp = getClientIp(request);
        
        // 2. 获取用户信息（从JWT认证过滤器传递的请求头）
        String userId = request.getHeaders().getFirst("X-User-Id");
        String username = request.getHeaders().getFirst("X-Username");

        // 3. 确定限流key和限制数量
        String rateLimitKey;
        int limit;
        
        if (userId != null) {
            // 已登录用户：使用用户ID作为限流key
            rateLimitKey = RATE_LIMIT_PREFIX + "user:" + userId;
            limit = USER_LIMIT;
            log.debug("用户限流检查: {} ({}), IP: {}", username, userId, clientIp);
        } else {
            // 游客：使用IP作为限流key
            rateLimitKey = RATE_LIMIT_PREFIX + "ip:" + clientIp;
            limit = GUEST_LIMIT;
            log.debug("游客限流检查: IP {}", clientIp);
        }

        // 4. 执行限流检查
        try {
            if (isRateLimited(rateLimitKey, limit)) {
                log.warn("请求被限流: key={}, limit={}", rateLimitKey, limit);
                return rateLimitResponse(response);
            }
        } catch (Exception e) {
            log.error("限流检查失败: {}", e.getMessage(), e);
            // 限流服务异常时，选择放行（避免影响正常业务）
        }

        // 5. 继续过滤器链
        return chain.filter(exchange);
    }

    /**
     * 检查是否被限流
     * 
     * @param key Redis key
     * @param limit 限制数量
     * @return 是否被限流
     */
    private boolean isRateLimited(String key, int limit) {
        try {
            // 使用Redis的INCR命令实现计数器
            Long count = redisTemplate.opsForValue().increment(key);
            
            if (count == null) {
                return false;
            }
            
            // 第一次访问，设置过期时间
            if (count == 1) {
                redisTemplate.expire(key, TIME_WINDOW, TimeUnit.SECONDS);
            }
            
            // 检查是否超过限制
            return count > limit;
            
        } catch (Exception e) {
            log.error("Redis限流操作失败: {}", e.getMessage());
            return false;  // Redis异常时不限流
        }
    }

    /**
     * 获取客户端真实IP地址
     * 
     * @param request 请求对象
     * @return 客户端IP
     */
    private String getClientIp(ServerHttpRequest request) {
        // 1. 从X-Forwarded-For头获取（可能经过代理）
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // 可能有多个IP，取第一个
            return xForwardedFor.split(",")[0].trim();
        }
        
        // 2. 从X-Real-IP头获取
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        // 3. 从请求的远程地址获取
        String remoteAddr = request.getRemoteAddress() != null ? 
                           request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        
        return remoteAddr;
    }

    /**
     * 返回限流响应
     * 
     * @param response 响应对象
     * @return Mono<Void>
     */
    private Mono<Void> rateLimitResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);  // 429状态码
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        // 构建错误响应JSON
        String responseBody = "{"
                + "\"code\": 429, "
                + "\"message\": \"请求过于频繁，请稍后再试\", "
                + "\"data\": null"
                + "}";
        
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 过滤器执行顺序
     * 限流应该在JWT认证之后执行
     */
    @Override
    public int getOrder() {
        return -90;  // 在JWT认证过滤器之后执行
    }
}