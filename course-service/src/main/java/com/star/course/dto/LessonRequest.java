package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 课时请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "课时请求")
public class LessonRequest {

    @Schema(description = "课时标题", example = "1.1 Java环境搭建")
    @NotBlank(message = "课时标题不能为空")
    private String title;

    @Schema(description = "视频URL", example = "https://example.com/video.mp4")
    private String videoUrl;

    @Schema(description = "时长(分钟)", example = "30")
    private Integer duration;

    @Schema(description = "是否免费(0:否,1:是)", example = "1")
    @NotNull(message = "是否免费不能为空")
    private Integer isFree;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}