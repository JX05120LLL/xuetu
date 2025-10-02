package com.xuetu.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuetu.gateway.config.PermissionConfig;
import com.xuetu.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限验证过滤器
 * 在JWT验证之后执行，验证用户是否有访问特定URL的权限
 * 
 * @author star
 */
@Slf4j
@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        log.debug("权限验证: {}", path);

        // 1. 检查是否在白名单中
        if (PermissionConfig.isWhiteListed(path)) {
            log.debug("白名单URL，跳过权限验证: {}", path);
            return chain.filter(exchange);
        }

        // 2. 获取Token
        String token = getToken(exchange);
        if (token == null) {
            log.warn("未提供Token: {}", path);
            return unauthorized(exchange, "未登录，请先登录");
        }

        // 3. 解析Token获取用户信息
        Claims claims;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            return unauthorized(exchange, "Token无效或已过期");
        }

        // 4. 获取用户角色
        @SuppressWarnings("unchecked")
        List<String> userRoles = (List<String>) claims.get("roles");
        log.debug("用户角色: {}", userRoles);

        // 5. 获取URL需要的角色
        List<String> requiredRoles = PermissionConfig.getRequiredRoles(path);
        log.debug("需要的角色: {}", requiredRoles);

        // 6. 验证权限
        if (requiredRoles != null && !PermissionConfig.hasAnyRole(userRoles, requiredRoles)) {
            log.warn("权限不足: path={}, userRoles={}, requiredRoles={}", 
                    path, userRoles, requiredRoles);
            return forbidden(exchange, "权限不足，无法访问该资源");
        }

        // 7. 将用户信息传递给下游服务
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", claims.get("userId").toString())
                .header("X-Username", claims.get("username").toString())
                .header("X-User-Roles", String.join(",", userRoles))
                .build();

        log.debug("权限验证通过: path={}, userId={}", path, claims.get("userId"));
        return chain.filter(exchange.mutate().request(request).build());
    }

    /**
     * 从请求中获取Token
     */
    private String getToken(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

    /**
     * 返回401未授权
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        return writeResponse(exchange, HttpStatus.UNAUTHORIZED, 401, message);
    }

    /**
     * 返回403禁止访问
     */
    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        return writeResponse(exchange, HttpStatus.FORBIDDEN, 403, message);
    }

    /**
     * 写入响应
     */
    private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status, 
                                     int code, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            DataBuffer buffer = response.bufferFactory()
                    .wrap(("{\"code\":" + code + ",\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }

    /**
     * 过滤器顺序：在JWT验证之后
     */
    @Override
    public int getOrder() {
        return 2;  // JWT验证过滤器是1，这个是2
    }
}
