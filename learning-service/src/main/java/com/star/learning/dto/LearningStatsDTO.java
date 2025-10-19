package com.star.learning.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习统计DTO
 * 
 * @author star
 */
@Data
public class LearningStatsDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总学习课程数
     */
    private Integer totalCourses;

    /**
     * 已完成课程数
     */
    private Integer completedCourses;

    /**
     * 正在学习课程数
     */
    private Integer learningCourses;

    /**
     * 总学习时长(分钟)
     */
    private Integer totalLearningTime;

    /**
     * 今日学习时长(分钟)
     */
    private Integer todayLearningTime;

    /**
     * 本周学习时长(分钟)
     */
    private Integer weekLearningTime;

    /**
     * 本月学习时长(分钟)
     */
    private Integer monthLearningTime;

    /**
     * 完成的课时数
     */
    private Integer completedLessons;

    /**
     * 连续学习天数
     */
    private Integer continuousDays;

    /**
     * 总笔记数
     */
    private Integer totalNotes;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastLearnTime;

    /**
     * 学习排名
     */
    private Integer rank;

    /**
     * 总用户数
     */
    private Integer totalUsers;

    /**
     * 平均学习进度(%)
     */
    private Integer averageProgress;
}