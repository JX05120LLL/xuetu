package com.star.ai.config;

import com.star.ai.agent.LearningAgentTools;
import com.star.ai.agent.OpsAgentTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenClawConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 将 @Tool 方法注册为 MCP Server 对外暴露的工具
     * OpenClaw / Cursor / Claude Desktop 通过 MCP 协议连接后即可调用
     */
    @Bean
    public ToolCallbackProvider agentToolCallbackProvider(
            LearningAgentTools learningTools, OpsAgentTools opsTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(learningTools, opsTools)
                .build();
    }
}
