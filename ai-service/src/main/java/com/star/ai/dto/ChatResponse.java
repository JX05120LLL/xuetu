package com.star.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI聊天响应
 * 
 * @author star
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI聊天响应")
public class ChatResponse implements Serializable {

    @Schema(description = "AI回答")
    private String answer;

    @Schema(description = "对话ID")
    private String conversationId;

    @Schema(description = "用户问题")
    private String question;

    @Schema(description = "回答时间")
    private LocalDateTime answerTime;

    @Schema(description = "消耗的tokens(可选)")
    private Integer tokens;

    @Schema(description = "知识来源列表（RAG 模式下返回，标注回答引用了哪些知识片段）")
    private List<String> sources;
}
