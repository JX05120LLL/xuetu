package com.star.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 学习分析报告DTO
 * 
 * @author star
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学习分析报告")
public class LearningReport implements Serializable {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "正在学习的课程数")
    private Integer learningCourses;

    @Schema(description = "学习时长(分钟)")
    private Integer learningTime;

    @Schema(description = "完成课程数")
    private Integer completedCourses;

    @Schema(description = "平均进度(%)")
    private Integer averageProgress;

    @Schema(description = "连续学习天数")
    private Integer continuousDays;

    @Schema(description = "学习状态评价")
    private String evaluation;

    @Schema(description = "优势总结")
    private String strengths;

    @Schema(description = "改进建议列表")
    private List<String> advices;

    @Schema(description = "下周学习计划")
    private String nextWeekPlan;

    @Schema(description = "推荐课程列表")
    private List<RecommendedCourse> recommendedCourses;

    @Schema(description = "完整的AI分析内容")
    private String aiAnalysis;

    /**
     * 推荐课程
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedCourse implements Serializable {
        @Schema(description = "课程ID")
        private Long courseId;

        @Schema(description = "课程标题")
        private String title;

        @Schema(description = "推荐理由")
        private String reason;
    }
}
