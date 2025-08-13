package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课时信息DTO
 * 
 * @author star
 */
@Data
@Schema(description = "课时信息")
public class LessonDTO {

    @Schema(description = "课时ID", example = "1")
    private Long id;

    @Schema(description = "章节ID", example = "1")
    private Long chapterId;

    @Schema(description = "课时标题", example = "1.1 Java环境搭建")
    private String title;

    @Schema(description = "视频URL", example = "https://example.com/video.mp4")
    private String videoUrl;

    @Schema(description = "时长(分钟)", example = "30")
    private Integer duration;

    @Schema(description = "是否免费(0:否,1:是)", example = "1")
    private Integer isFree;

    @Schema(description = "是否免费名称", example = "免费")
    private String isFreeLabel;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedTime;
}