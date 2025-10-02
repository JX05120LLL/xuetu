package com.star.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 课程推荐DTO
 * 
 * @author star
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程推荐")
public class CourseRecommendation implements Serializable {

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程标题")
    private String title;

    @Schema(description = "课程描述")
    private String description;

    @Schema(description = "课程封面")
    private String coverImage;

    @Schema(description = "课程价格")
    private BigDecimal price;

    @Schema(description = "难度级别(0:初级,1:中级,2:高级)")
    private Integer level;

    @Schema(description = "学习人数")
    private Integer studentCount;

    @Schema(description = "AI推荐理由")
    private String reason;

    @Schema(description = "推荐分数(0-100)")
    private Integer score;

    @Schema(description = "是否适合当前水平")
    private Boolean suitable;
}
