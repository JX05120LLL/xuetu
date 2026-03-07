package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 评论请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "评论请求")
public class CommentRequest {

    @Schema(description = "课程ID", example = "1")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @Schema(description = "评论内容", example = "这门课程很棒，讲解得很详细！")
    @NotBlank(message = "评论内容不能为空")
    private String content;

    @Schema(description = "评分(1-5)", example = "5")
    @Min(value = 1, message = "评分最小值为1")
    @Max(value = 5, message = "评分最大值为5")
    private Integer rating;
}