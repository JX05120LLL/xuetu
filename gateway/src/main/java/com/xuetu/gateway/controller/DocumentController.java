package com.xuetu.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 文档控制器 - WebFlux响应式版本
 * 提供API文档访问的重定向功能
 * 
 * @author star
 */
@Slf4j
@RestController
public class DocumentController {

    /**
     * 重定向到用户服务文档（响应式方式）
     */
    @GetMapping("/doc.html")
    public Mono<Void> redirectToUserServiceDoc(ServerWebExchange exchange) {
        log.info("重定向到用户服务API文档");
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND); // 302重定向
        response.getHeaders().setLocation(URI.create("http://localhost:8088/doc.html"));
        
        return response.setComplete();
    }
    
    /**
     * API文档代理（响应式方式）
     */
    @GetMapping("/v3/api-docs")
    public Mono<Void> redirectToApiDocs(ServerWebExchange exchange) {
        log.info("重定向到用户服务API文档数据");
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND); // 302重定向
        response.getHeaders().setLocation(URI.create("http://localhost:8088/v3/api-docs"));
        
        return response.setComplete();
    }
    
    /**
     * API文档首页
     */
    @GetMapping(value = "/docs", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> documentIndex() {
        log.info("访问API文档首页");
        
        String html = """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>学途在线教育平台 - API文档</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }
                    .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    h1 { color: #333; text-align: center; margin-bottom: 30px; }
                    .service-card { border: 1px solid #ddd; margin: 15px 0; padding: 20px; border-radius: 6px; transition: box-shadow 0.3s; }
                    .service-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
                    .service-title { font-size: 18px; font-weight: bold; color: #2c3e50; margin-bottom: 8px; }
                    .service-desc { color: #666; margin-bottom: 15px; }
                    .btn { display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px; transition: background-color 0.3s; }
                    .btn:hover { background-color: #0056b3; }
                    .gateway-link { text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🎓 学途在线教育平台 - API文档</h1>
                    
                    <div class="service-card">
                        <div class="service-title">👤 用户服务 API</div>
                        <div class="service-desc">提供用户注册、登录、认证、用户管理等功能的API接口文档</div>
                        <a href="http://localhost:8088/doc.html" class="btn" target="_blank">查看用户服务API</a>
                    </div>
                    
                    <div class="service-card">
                        <div class="service-title">📚 课程服务 API</div>
                        <div class="service-desc">提供课程管理、分类管理、章节管理、标签管理等功能的API接口文档</div>
                        <a href="http://localhost:8077/doc.html" class="btn" target="_blank">查看课程服务API</a>
                    </div>
                    
                    <div class="gateway-link">
                        <p><strong>网关聚合文档：</strong> <a href="/doc.html" class="btn">统一API文档</a></p>
                        <p style="color: #666; font-size: 14px;">如果网关聚合文档无法正常显示多个服务，可以使用上方的直接链接访问各个服务的文档</p>
                    </div>
                </div>
            </body>
            </html>
            """;
            
        return Mono.just(html);
    }
}