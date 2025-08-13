package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 章节信息DTO
 * 
 * @author star
 */
@Data
@Schema(description = "章节信息")
public class ChapterDTO {

    @Schema(description = "章节ID", example = "1")
    private Long id;

    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    @Schema(description = "章节标题", example = "第一章：Java基础语法")
    private String title;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "课时数量", example = "5")
    private Integer lessonCount;

    @Schema(description = "课时列表")
    private List<LessonDTO> lessons;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedTime;
}