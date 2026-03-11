package com.star.ai.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Agent 智能体响应")
public class AgentResponse implements Serializable {

    @Schema(description = "最终回答")
    private String answer;

    @Schema(description = "工具调用记录（展示 Agent 的推理过程）")
    private List<ToolCallRecord> toolCalls;

    @Schema(description = "会话ID")
    private String conversationId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单次工具调用记录")
    public static class ToolCallRecord implements Serializable {

        @Schema(description = "工具名称", example = "courseQuery")
        private String name;

        @Schema(description = "模型传入的参数 JSON")
        private String arguments;

        @Schema(description = "工具执行结果 JSON")
        private String result;
    }
}
