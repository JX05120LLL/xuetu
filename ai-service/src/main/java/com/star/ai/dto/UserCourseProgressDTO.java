package com.star.ai.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户课程进度DTO
 * 
 * @author star
 */
@Data
public class UserCourseProgressDTO implements Serializable {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 学习进度(0-100)
     */
    private Integer progress;

    /**
     * 是否完成
     */
    private Boolean isCompleted;
}
