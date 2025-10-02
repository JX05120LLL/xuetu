package com.xuetu.gateway.filter;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.common.result.R;
import com.xuetu.gateway.config.PermissionConfig;
import com.xuetu.gateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 权限验证全局过滤器
 * 
 * 功能：
 * 1. 从JWT Token中获取用户角色和权限
 * 2. 根据请求URL匹配所需权限
 * 3. 验证用户是否拥有访问权限
 * 
 * 执行顺序：在JwtAuthFilter之后执行
 * 
 * @author star
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private final PermissionConfig permissionConfig;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 从请求头中获取用户ID、角色和权限 (由JwtAuthFilter设置)
        String userId = request.getHeaders().getFirst("X-User-Id");
        List<String> userRoles = request.getHeaders().get("X-User-Roles");
        List<String> userPermissions = request.getHeaders().get("X-User-Permissions");

        // 如果是白名单路径，直接放行
        if (userRoles != null && userRoles.contains("ANONYMOUS")) {
            log.debug("匿名访问路径: {}", path);
            return chain.filter(exchange);
        }

        // 2. 获取URL所需的权限
        List<String> requiredPermissions = getRequiredPermissions(path);
        List<String> requiredRoles = getRequiredRoles(path);

        // 如果没有配置任何权限或角色，则默认放行
        if ((requiredPermissions == null || requiredPermissions.isEmpty()) &&
            (requiredRoles == null || requiredRoles.isEmpty())) {
            log.debug("路径 {} 未配置权限，默认放行", path);
            return chain.filter(exchange);
        }

        // 3. 权限验证
        boolean hasPermission = checkPermissions(userPermissions, requiredPermissions);
        boolean hasRole = checkRoles(userRoles, requiredRoles);

        if (hasPermission || hasRole) {
            log.debug("用户 {} 访问路径 {} 权限验证通过", userId, path);
            return chain.filter(exchange);
        } else {
            log.warn("用户 {} 访问路径 {} 权限不足。所需权限: {}，用户权限: {}。所需角色: {}，用户角色: {}",
                    userId, path, requiredPermissions, userPermissions, requiredRoles, userRoles);
            return unauthorized(exchange, "权限不足，无法访问该资源");
        }
    }

    /**
     * 检查用户是否拥有所需权限中的任意一个
     */
    private boolean checkPermissions(List<String> userPermissions, List<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return true;
        }
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }
        return userPermissions.stream().anyMatch(requiredPermissions::contains);
    }

    /**
     * 检查用户是否拥有所需角色中的任意一个
     */
    private boolean checkRoles(List<String> userRoles, List<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        return userRoles.stream().anyMatch(requiredRoles::contains);
    }

    /**
     * 根据请求路径获取所需权限
     */
    private List<String> getRequiredPermissions(String path) {
        for (Map.Entry<String, List<String>> entry : permissionConfig.urlPermissionMap().entrySet()) {
            if (antPathMatcher.match(entry.getKey(), path)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 根据请求路径获取所需角色
     */
    private List<String> getRequiredRoles(String path) {
        for (Map.Entry<String, List<String>> entry : permissionConfig.urlRoleMap().entrySet()) {
            if (antPathMatcher.match(entry.getKey(), path)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        R<Object> result = R.fail(HttpStatus.FORBIDDEN.value(), message);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(result);
        } catch (JsonProcessingException e) {
            log.error("Error writing JSON response: {}", e.getMessage());
            bytes = "{\"code\":403,\"message\":\"权限不足\"}".getBytes();
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 权限验证过滤器应该在JWT认证过滤器之后执行
     */
    @Override
    public int getOrder() {
        return 2; // 优先级低于JwtAuthFilter (Order 1)
    }
}
