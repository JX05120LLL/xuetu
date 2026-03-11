package com.star.ai.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.ai.openclaw.OpenClawSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 运维智能体工具集（4 个 @Tool 方法）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpsAgentTools {

    private final OpenClawSkillService skillService;
    private final ObjectMapper objectMapper;

    @Tool(description = "查询 Nacos 注册的微服务实例健康状态、实例数量，排查故障时首先调用")
    public String serviceHealthCheck(
            @ToolParam(description = "服务名，选填，不填查所有服务") @Nullable String serviceName) {
        log.info("[Tool] serviceHealthCheck: serviceName={}", serviceName);
        Object result = skillService.serviceHealthCheckSkill(serviceName);
        String json = toJson(result);
        ToolCallRecorder.record("serviceHealthCheck", toJson(Map.of("serviceName", str(serviceName))), json);
        return json;
    }

    @Tool(description = "查询 Spring Cloud Gateway 的接口监控数据（QPS、响应时间、错误率）")
    public String gatewayInterfaceMonitor(
            @ToolParam(description = "时间范围，如 最近5分钟") @Nullable String timeRange,
            @ToolParam(description = "服务名，选填") @Nullable String serviceName) {
        log.info("[Tool] gatewayInterfaceMonitor: timeRange={}, serviceName={}", timeRange, serviceName);
        Object result = skillService.gatewayInterfaceMonitorSkill(timeRange, serviceName);
        String json = toJson(result);
        ToolCallRecorder.record("gatewayInterfaceMonitor",
                toJson(Map.of("timeRange", str(timeRange), "serviceName", str(serviceName))), json);
        return json;
    }

    @Tool(description = "查询 Redis 和 RocketMQ 的运行状态（内存、缓存命中率、消息堆积）")
    public String middlewareStatusCheck(
            @ToolParam(description = "中间件类型：redis 或 rocketmq，不填查所有") @Nullable String middlewareType) {
        log.info("[Tool] middlewareStatusCheck: middlewareType={}", middlewareType);
        Object result = skillService.middlewareStatusCheckSkill(middlewareType);
        String json = toJson(result);
        ToolCallRecorder.record("middlewareStatusCheck",
                toJson(Map.of("middlewareType", str(middlewareType))), json);
        return json;
    }

    @Tool(description = "检索指定时间范围内的异常日志，按服务名或关键词过滤")
    public String exceptionLogQuery(
            @ToolParam(description = "时间范围，如 最近10分钟") @Nullable String timeRange,
            @ToolParam(description = "服务名，选填") @Nullable String serviceName,
            @ToolParam(description = "关键词，选填") @Nullable String keyword) {
        log.info("[Tool] exceptionLogQuery: timeRange={}, serviceName={}, keyword={}", timeRange, serviceName, keyword);
        Object result = skillService.exceptionLogQuerySkill(timeRange, serviceName, keyword);
        String json = toJson(result);
        ToolCallRecorder.record("exceptionLogQuery",
                toJson(Map.of("timeRange", str(timeRange), "serviceName", str(serviceName), "keyword", str(keyword))), json);
        return json;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\":\"序列化失败: " + e.getMessage() + "\"}";
        }
    }

    private static String str(Object v) {
        return v != null ? v.toString() : "";
    }
}
