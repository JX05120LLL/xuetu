package com.star.learning.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习记录DTO
 * 
 * @author star
 */
@Data
public class LearningRecordDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程标题
     */
    private String courseTitle;

    /**
     * 课时ID
     */
    private Long lessonId;

    /**
     * 课时标题
     */
    private String lessonTitle;

    /**
     * 学习进度(秒)
     */
    private Integer progress;

    /**
     * 课时总时长(秒)
     */
    private Integer duration;

    /**
     * 学习进度百分比
     */
    private Double progressPercentage;

    /**
     * 是否完成(0:否,1:是)
     */
    private Integer isCompleted;

    /**
     * 完成状态名称
     */
    private String completionStatusName;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastLearnTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}