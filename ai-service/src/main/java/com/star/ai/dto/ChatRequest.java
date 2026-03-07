package com.star.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * AI聊天请求
 * 
 * @author star
 */
@Data
@Schema(description = "AI聊天请求")
public class ChatRequest implements Serializable {

    @NotBlank(message = "问题不能为空")
    @Size(max = 2000, message = "问题长度不能超过2000字符")
    @Schema(description = "用户问题", example = "什么是Spring Boot?")
    private String question;

    @Schema(description = "对话ID(用于上下文，首次对话可不传)", example = "conv-123456")
    private String conversationId;

    @Schema(description = "关联课程ID(可选)", example = "1")
    private Long courseId;

    @Schema(description = "关联课时ID(可选)", example = "1")
    private Long lessonId;

    @Schema(description = "是否启用知识库增强（RAG），默认 false；true 时会先检索向量库再生成回答", example = "false")
    private Boolean useRag = false;
}
