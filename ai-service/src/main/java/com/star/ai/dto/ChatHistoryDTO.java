package com.star.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史DTO
 * 
 * @author star
 */
@Data
@Schema(description = "对话历史")
public class ChatHistoryDTO implements Serializable {

    @Schema(description = "对话ID")
    private Long id;

    @Schema(description = "对话会话ID")
    private String conversationId;

    @Schema(description = "角色(user/assistant)")
    private String role;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "关联课程ID")
    private Long courseId;

    @Schema(description = "关联课时ID")
    private Long lessonId;

    @Schema(description = "创建时间")
    private LocalDateTime createdTime;
}
