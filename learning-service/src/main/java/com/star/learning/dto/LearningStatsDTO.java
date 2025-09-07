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
     * 总学习时长(秒)
     */
    private Long totalLearningTime;

    /**
     * 今日学习时长(秒)
     */
    private Long todayLearningTime;

    /**
     * 本周学习时长(秒)
     */
    private Long weekLearningTime;

    /**
     * 本月学习时长(秒)
     */
    private Long monthLearningTime;

    /**
     * 总笔记数
     */
    private Integer totalNotes;

    /**
     * 连续学习天数
     */
    private Integer consecutiveDays;

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
}