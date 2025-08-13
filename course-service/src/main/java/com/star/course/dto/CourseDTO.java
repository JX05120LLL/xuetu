package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程信息DTO
 * 
 * @author star
 */
@Data
@Schema(description = "课程信息")
public class CourseDTO {

    @Schema(description = "课程ID", example = "1")
    private Long id;

    @Schema(description = "课程标题", example = "Java基础入门教程")
    private String title;

    @Schema(description = "课程描述", example = "从零开始学习Java编程语言")
    private String description;

    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "课程价格", example = "99.00")
    private BigDecimal price;

    @Schema(description = "原价", example = "199.00")
    private BigDecimal originalPrice;

    @Schema(description = "讲师ID", example = "1")
    private Long teacherId;

    @Schema(description = "讲师名称", example = "张老师")
    private String teacherName;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "Java编程")
    private String categoryName;

    @Schema(description = "难度级别(0:初级,1:中级,2:高级)", example = "0")
    private Integer level;

    @Schema(description = "难度级别名称", example = "初级")
    private String levelName;

    @Schema(description = "状态(0:未发布,1:已发布,2:已下架)", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "已发布")
    private String statusName;

    @Schema(description = "总时长(分钟)", example = "1200")
    private Integer totalDuration;

    @Schema(description = "学习人数", example = "520")
    private Integer studentCount;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedTime;
}