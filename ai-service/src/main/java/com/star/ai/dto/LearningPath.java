package com.star.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 学习路径DTO
 * 
 * @author star
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学习路径")
public class LearningPath implements Serializable {

    @Schema(description = "学习目标")
    private String goal;

    @Schema(description = "预计学习时长(小时)")
    private Integer totalDuration;

    @Schema(description = "学习阶段列表")
    private List<LearningStage> stages;

    @Schema(description = "AI建议")
    private String advice;

    /**
     * 学习阶段
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningStage implements Serializable {
        
        @Schema(description = "阶段名称")
        private String stageName;

        @Schema(description = "阶段描述")
        private String description;

        @Schema(description = "推荐课程列表（混合：真实课程带图片，虚拟课程纯文字）")
        private List<CourseRecommendation> courses;

        @Schema(description = "预计时长(小时)")
        private Integer duration;

        @Schema(description = "学习要点")
        private List<String> keyPoints;
    }
}
