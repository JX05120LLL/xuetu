package com.xuetu.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 网关全局异常处理器
 * 
 * 功能：
 * 1. 处理网关路由异常
 * 2. 处理微服务不可用异常
 * 3. 处理超时异常
 * 4. 统一异常响应格式
 * 
 * @author star
 */
@Slf4j
@Order(-1)  // 优先级最高
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        // 设置响应头
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        // 根据异常类型返回不同的错误信息
        if (ex instanceof NotFoundException) {
            // 服务未找到
            return handleNotFoundException(exchange, ex);
        } else if (ex instanceof ResponseStatusException) {
            // HTTP状态异常
            return handleResponseStatusException(exchange, (ResponseStatusException) ex);
        } else if (ex instanceof java.net.ConnectException) {
            // 连接异常
            return handleConnectException(exchange, ex);
        } else if (ex instanceof java.util.concurrent.TimeoutException) {
            // 超时异常
            return handleTimeoutException(exchange, ex);
        } else {
            // 其他异常
            return handleGenericException(exchange, ex);
        }
    }

    /**
     * 处理服务未找到异常
     */
    private Mono<Void> handleNotFoundException(ServerWebExchange exchange, Throwable ex) {
        log.error("服务未找到: {}", ex.getMessage());
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.NOT_FOUND);
        
        String errorMessage = "请求的服务不存在";
        return buildErrorResponse(exchange, 404, errorMessage);
    }

    /**
     * 处理HTTP状态异常
     */
    private Mono<Void> handleResponseStatusException(ServerWebExchange exchange, ResponseStatusException ex) {
        log.error("HTTP状态异常: {}", ex.getMessage());
        
        ServerHttpResponse response = exchange.getResponse();
        // Spring 6.x: getStatus() 已移除，改为 getStatusCode()
        response.setStatusCode(ex.getStatusCode());
        
        String errorMessage = "请求处理失败: " + ex.getReason();
        return buildErrorResponse(exchange, ex.getStatusCode().value(), errorMessage);
    }

    /**
     * 处理连接异常
     */
    private Mono<Void> handleConnectException(ServerWebExchange exchange, Throwable ex) {
        log.error("服务连接失败: {}", ex.getMessage());
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        
        String errorMessage = "服务暂时不可用，请稍后重试";
        return buildErrorResponse(exchange, 503, errorMessage);
    }

    /**
     * 处理超时异常
     */
    private Mono<Void> handleTimeoutException(ServerWebExchange exchange, Throwable ex) {
        log.error("请求超时: {}", ex.getMessage());
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.GATEWAY_TIMEOUT);
        
        String errorMessage = "请求超时，请稍后重试";
        return buildErrorResponse(exchange, 504, errorMessage);
    }

    /**
     * 处理通用异常
     */
    private Mono<Void> handleGenericException(ServerWebExchange exchange, Throwable ex) {
        log.error("网关内部错误: {}", ex.getMessage(), ex);
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        
        String errorMessage = "网关内部错误";
        return buildErrorResponse(exchange, 500, errorMessage);
    }

    /**
     * 构建错误响应
     */
    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, int code, String message) {
        ServerHttpResponse response = exchange.getResponse();
        
        // 构建统一的错误响应格式
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", code);
        errorResponse.put("message", message);
        errorResponse.put("data", null);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("path", exchange.getRequest().getURI().getPath());
        
        try {
            // 转换为JSON字符串
            String responseBody = objectMapper.writeValueAsString(errorResponse);
            
            // 写入响应
            DataBuffer buffer = response.bufferFactory().wrap(responseBody.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
            
        } catch (JsonProcessingException e) {
            log.error("构建错误响应失败: {}", e.getMessage());
            
            // 如果JSON序列化失败，返回简单的错误信息
            String fallbackResponse = String.format(
                "{\"code\": %d, \"message\": \"%s\", \"data\": null}",
                code, message
            );
            
            DataBuffer buffer = response.bufferFactory().wrap(fallbackResponse.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }
}

/**
 * 异常处理说明：
 * 
 * 1. 常见的网关异常：
 *    - NotFoundException: 路由目标服务不存在
 *    - ConnectException: 无法连接到目标服务
 *    - TimeoutException: 请求超时
 *    - ResponseStatusException: HTTP状态异常
 * 
 * 2. 错误响应格式：
 *    {
 *      "code": 404,
 *      "message": "请求的服务不存在",
 *      "data": null,
 *      "timestamp": 1699123456789,
 *      "path": "/api/user/info"
 *    }
 * 
 * 3. 状态码说明：
 *    - 404: 服务不存在
 *    - 503: 服务不可用
 *    - 504: 网关超时
 *    - 500: 网关内部错误
 * 
 * 4. 注意事项：
 *    - 不要暴露敏感的内部错误信息
 *    - 记录详细的错误日志用于排查问题
 *    - 为前端提供友好的错误提示
 */