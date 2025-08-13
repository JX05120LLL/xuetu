package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 回复请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "回复请求")
public class ReplyRequest {

    @Schema(description = "评论ID", example = "1")
    @NotNull(message = "评论ID不能为空")
    private Long commentId;

    @Schema(description = "回复目标用户ID（可选）", example = "2")
    private Long targetUserId;

    @Schema(description = "回复内容", example = "说得很对，我也有同样的感受！")
    @NotBlank(message = "回复内容不能为空")
    private String content;
}