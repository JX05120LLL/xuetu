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

    @Schema(description = "预计完成时间(天)")
    private Integer estimatedDays;

    @Schema(description = "预计学习时长(小时)")
    private Integer estimatedHours;

    @Schema(description = "学习阶段列表")
    private List<LearningStage> stages;

    @Schema(description = "AI生成的完整路径说明")
    private String pathDescription;

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

        @Schema(description = "推荐课程列表")
        private List<String> courses;

        @Schema(description = "预计时长(小时)")
        private Integer hours;

        @Schema(description = "学习要点")
        private List<String> keyPoints;
    }
}
