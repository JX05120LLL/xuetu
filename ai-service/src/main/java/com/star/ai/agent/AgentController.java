package com.star.ai.agent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
@Tag(name = "Agent 智能体", description = "基于 Spring AI Function Calling 的自主学习智能体与智能运维 Agent，工具同时通过 MCP 暴露给 OpenClaw")
public class AgentController {

    private final ChatModel chatModel;
    private final LearningAgentTools learningTools;
    private final OpsAgentTools opsTools;

    @PostMapping("/learning/run")
    @Operation(summary = "学习智能体",
            description = "发送一句话，大模型自动编排调用课程查询、进度跟踪、计划生成、RAG答疑等工具，返回最终回答与工具调用记录")
    public AgentResponse learningRun(@RequestBody AgentRequest request) {
        log.info("[LearningAgent] username={}, message={}", request.getUsername(), request.getMessage());

        ToolCallRecorder.clear();

        String answer = ChatClient.create(chatModel)
                .prompt()
                .system(buildLearningPrompt(request.getUsername()))
                .user(request.getMessage())
                .tools(learningTools)
                .call()
                .content();

        return AgentResponse.builder()
                .answer(answer)
                .toolCalls(ToolCallRecorder.getAndClear())
                .conversationId(request.getConversationId())
                .build();
    }

    @PostMapping("/ops/run")
    @Operation(summary = "运维智能体",
            description = "发送故障描述，大模型按「服务健康→接口监控→中间件→日志」顺序自动排查，输出根因分析与解决方案")
    public AgentResponse opsRun(@RequestBody AgentRequest request) {
        log.info("[OpsAgent] message={}", request.getMessage());

        ToolCallRecorder.clear();

        String answer = ChatClient.create(chatModel)
                .prompt()
                .system(OPS_PROMPT)
                .user(request.getMessage())
                .tools(opsTools)
                .call()
                .content();

        return AgentResponse.builder()
                .answer(answer)
                .toolCalls(ToolCallRecorder.getAndClear())
                .conversationId(request.getConversationId())
                .build();
    }

    private String buildLearningPrompt(String username) {
        return """
                你是「学途」在线教育平台的智能学习助手。当前用户：%s
                
                你可以使用以下工具获取真实数据：
                - courseQuery：查询用户已购课程和章节
                - learningProgressTrack：查询学习进度
                - learningPlanGenerate：生成学习计划
                - courseRagQa：知识库问答
                - learningRemind：设置学习提醒
                - afterClassTestGenerate：生成课后测验
                
                规则：
                1. 涉及用户数据必须调用工具获取，不允许编造
                2. 用户没指定课程时，先调 courseQuery 查已购课程
                3. 生成学习计划前，先调 learningProgressTrack 了解进度
                4. 多步任务逐步完成
                5. 用中文回答，简洁有结构
                """.formatted(username != null ? username : "未知");
    }

    private static final String OPS_PROMPT = """
            你是「学途」在线教育平台的智能运维助手。
            
            你可以使用以下工具获取真实数据：
            - serviceHealthCheck：查询 Nacos 微服务健康状态
            - gatewayInterfaceMonitor：查询网关接口监控
            - middlewareStatusCheck：查询 Redis/RocketMQ 状态
            - exceptionLogQuery：查询异常日志
            
            排查流程：按 serviceHealthCheck → gatewayInterfaceMonitor → middlewareStatusCheck → exceptionLogQuery 顺序调用
            
            输出格式：故障根因、影响范围、临时方案、优化建议
            
            规则：必须基于工具返回的真实数据分析，用中文回答
            """;
}
