package com.star.ai.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 学习统计DTO（从learning-service获取）
 * 
 * @author star
 */
@Data
public class LearningStatsDTO implements Serializable {

    /**
     * 总学习时长(分钟)
     */
    private Integer totalLearningTime;

    /**
     * 完成的课时数
     */
    private Integer completedLessons;

    /**
     * 连续学习天数
     */
    private Integer continuousDays;

    /**
     * 本周学习时长(分钟)
     */
    private Integer weekLearningTime;

    /**
     * 正在学习的课程数
     */
    private Integer learningCourses;

    /**
     * 完成的课程数
     */
    private Integer completedCourses;

    /**
     * 平均学习进度(%)
     */
    private Integer averageProgress;
}
