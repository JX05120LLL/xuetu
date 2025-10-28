package com.xuetu.gateway.filter;


import com.xuetu.gateway.util.JwtUtil;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * JWT认证全局过滤器
 * 
 * 功能：
 * 1. 拦截所有请求进行JWT认证
 * 2. 白名单路径直接放行
 * 3. 验证JWT Token有效性
 * 4. 将用户信息传递给下游服务
 * 
 * 执行顺序：优先级最高，最先执行
 * 
 * @author star
 */
@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    /**
     * 白名单路径 - 这些路径不需要JWT认证
     * 游客可以访问的公开接口
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            // ========== 认证相关（公开） ==========
            "/auth/register",           // 用户注册
            "/auth/login",              // 用户登录
            "/auth/check-username",     // 检查用户名
            "/auth/check-email",        // 检查邮箱
            "/auth/health",             // 认证服务健康检查
            
            // ========== API前缀的认证路径（公开） ==========
            "/api/auth/register",       // 用户注册（带API前缀）
            "/api/auth/login",          // 用户登录（带API前缀）
            "/api/auth/check-username", // 检查用户名（带API前缀）
            "/api/auth/check-email",    // 检查邮箱（带API前缀）
            "/api/auth/health",         // 认证服务健康检查（带API前缀）
            
            // ========== 课程服务（游客可浏览） ==========
            "/course/list",             // 课程列表
            "/course/search",           // 搜索课程
            "/course/hot",              // 热门课程
            "/course/health",           // 课程服务健康检查
            "/course/",                 // 课程详情 /course/{id}
            
            // ========== 分类服务（公开） ==========
            "/category/list",           // 分类列表
            "/category/tree",           // 分类树
            "/category/children",       // 子分类
            "/category/",               // 分类详情 /category/{id}
            "/category/health",         // 分类服务健康检查
            
            // ========== 章节课时（游客可查看目录） ==========
            "/chapter/course",          // 课程章节 /chapter/course/{courseId}
            "/lesson/chapter",          // 章节课时 /lesson/chapter/{chapterId}
            
            // ========== 评论（游客可查看） ==========
            "/comment/list",            // 评论列表
            "/comment/course",          // 课程评论
            
            // ========== AI服务（部分公开） ==========
            "/chat/health",             // AI聊天服务健康检查
            "/recommend/health",        // AI推荐服务健康检查
            "/analysis/health",         // AI分析服务健康检查
            "/recommend/courses",       // 推荐课程（游客可查看）
            
            // ========== API文档和健康检查 ==========
            "/doc.html",                // API文档首页
            "/swagger-ui",              // Swagger UI
            "/v3/api-docs",             // OpenAPI文档
            "/webjars",                 // 静态资源
            "/swagger-resources",       // Swagger资源
            "/actuator",                // 健康检查端点
            "/favicon.ico",             // 网站图标
            "/health"                   // 通用健康检查
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        
        String path = request.getURI().getPath();
        log.info("网关收到请求: {} {}", request.getMethod(), path);

        // 1. 检查是否为白名单路径
        if (isWhiteListPath(path)) {
            log.debug("白名单路径，直接放行: {}", path);
            // 添加匿名标记，让AuthorizationFilter知道这是公开路径
            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Roles", "ANONYMOUS")
                    .build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        }

        // 2. 获取Authorization请求头
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || authHeader.trim().isEmpty()) {
            log.warn("请求头中缺少Authorization: {}", path);
            return unauthorizedResponse(response, "缺少认证信息");
        }

        // 3. 提取JWT Token
        String token = JwtUtil.extractToken(authHeader);
        if (token == null) {
            log.warn("Authorization格式错误: {}", authHeader);
            return unauthorizedResponse(response, "认证信息格式错误");
        }

        // 4. 验证Token格式
        if (!JwtUtil.isValidFormat(token)) {
            log.warn("Token格式无效: {}", token);
            return unauthorizedResponse(response, "Token格式无效");
        }

        // 5. 验证Token有效性
        if (!JwtUtil.verifyToken(token)) {
            log.warn("Token验证失败: {}", token);
            return unauthorizedResponse(response, "Token无效");
        }

        // 6. 检查Token是否过期
        if (JwtUtil.isTokenExpired(token)) {
            log.warn("Token已过期: {}", token);
            return unauthorizedResponse(response, "Token已过期");
        }

        // 7. 提取用户信息并添加到请求头中，传递给下游服务
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);
        
        if (userId == null || username == null) {
            log.warn("无法从Token中提取用户信息: {}", token);
            return unauthorizedResponse(response, "Token中缺少用户信息");
        }

        // 8. 构建新的请求，添加用户信息到请求头
        ServerHttpRequest newRequest = request.mutate()
                .header("X-User-Id", userId.toString())     // 用户ID
                .header("X-Username", username)              // 用户名
                .header("X-Original-Token", token)           // 原始Token
                .build();

        log.info("JWT认证成功，用户: {} (ID: {})", username, userId);

        // 9. 继续过滤器链
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * 检查是否为白名单路径
     * 
     * @param path 请求路径
     * @return 是否为白名单路径
     */
    private boolean isWhiteListPath(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    /**
     * 返回401未授权响应
     * 
     * @param response 响应对象
     * @param message 错误消息
     * @return Mono<Void>
     */
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        // 构建错误响应JSON
        String responseBody = String.format(
                "{\"code\": 401, \"message\": \"%s\", \"data\": null}", 
                message
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 过滤器执行顺序，数值越小优先级越高
     * JWT认证应该最先执行
     */
    @Override
    public int getOrder() {
        return 1;  // JWT验证优先级：1（在权限验证之前）
    }
}