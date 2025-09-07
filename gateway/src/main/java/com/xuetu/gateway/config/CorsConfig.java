package com.xuetu.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 网关跨域配置类
 * 
 * 为什么在网关配置跨域？
 * 1. 前端只与网关通信，跨域问题发生在这里
 * 2. 微服务间通信不经过浏览器，无跨域问题
 * 3. 统一配置便于管理和维护
 * 4. 减少各微服务的配置重复
 *
 * @author star
 */
@Slf4j
@Configuration
public class CorsConfig {

    /**
     * 配置跨域过滤器
     * 
     * @return CorsWebFilter
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        log.info("初始化网关跨域配置...");
        
        CorsConfiguration config = new CorsConfiguration();
        
        // 1. 允许的源（前端域名）
        // 开发环境
        config.addAllowedOrigin("http://localhost:3000");    // React默认端口
        config.addAllowedOrigin("http://localhost:8081");    // Vue默认端口
        config.addAllowedOrigin("http://localhost:8082");    // Angular默认端口
        config.addAllowedOrigin("http://127.0.0.1:3000");    // 本地IP访问
        
        // 生产环境（根据实际情况配置）
        config.addAllowedOriginPattern("https://*.yourdomain.com");  // 支持子域名
        
        // 2. 允许的HTTP方法
        config.addAllowedMethod("GET");      // 查询操作
        config.addAllowedMethod("POST");     // 创建操作
        config.addAllowedMethod("PUT");      // 更新操作
        config.addAllowedMethod("DELETE");   // 删除操作
        config.addAllowedMethod("OPTIONS");  // 预检请求
        config.addAllowedMethod("HEAD");     // 头部请求
        config.addAllowedMethod("PATCH");    // 部分更新
        
        // 3. 允许的请求头
        config.addAllowedHeader("*");  // 允许所有请求头
        
        // 常用的请求头包括：
        // - Authorization: Bearer token
        // - Content-Type: application/json
        // - X-Requested-With: XMLHttpRequest
        // - Accept: application/json
        
        // 4. 允许携带凭证信息（Cookie、Authorization头等）
        config.setAllowCredentials(true);
        
        // 5. 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);  // 1小时
        
        // 6. 允许浏览器访问的响应头
        config.addExposedHeader("*");  // 前端能读取所有响应头
        
        // 常用的响应头包括：
        // - X-Total-Count: 总数量
        // - X-Page-Count: 页数
        // - Location: 资源位置
        
        // 7. 配置URL匹配规则
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 匹配所有路径
        
        log.info("网关跨域配置初始化完成");
        log.info("允许的源: localhost:3000, localhost:8081, localhost:8082");
        log.info("允许的方法: GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        log.info("允许携带凭证: true");
        
        return new CorsWebFilter(source);
    }
}

/**
 * 跨域配置说明：
 * 
 * 1. 什么是跨域？
 *    - 浏览器的同源策略限制
 *    - 不同协议、域名、端口之间的请求被限制
 *    - 例：http://localhost:3000 访问 http://localhost:8080
 * 
 * 2. 为什么需要配置跨域？
 *    - 前端开发服务器与后端API服务器端口不同
 *    - 生产环境可能使用不同的域名
 *    - 确保前端能正常调用后端API
 * 
 * 3. 预检请求（OPTIONS）：
 *    - 浏览器在某些情况下会先发送OPTIONS请求
 *    - 检查服务器是否允许实际的请求
 *    - 我们需要正确响应这些预检请求
 * 
 * 4. 注意事项：
 *    - 生产环境应该限制允许的源，不要使用 "*"
 *    - 敏感操作建议限制允许的HTTP方法
 *    - 考虑安全性，不要暴露不必要的响应头
 */