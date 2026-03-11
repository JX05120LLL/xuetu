package com.star.ai.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "Agent 智能体请求")
public class AgentRequest implements Serializable {

    @Schema(description = "用户名（学习智能体必填，运维可不传）", example = "zhangsan")
    private String username;

    @Schema(description = "用户消息", example = "帮我制定30天Java学习计划，每天2小时", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "会话ID（可选，传相同值可保持多轮上下文）", example = "conv-001")
    private String conversationId;
}
