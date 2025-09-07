package com.star.learning.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 笔记DTO
 * 
 * @author star
 */
@Data
public class NoteDTO {

    /**
     * 笔记ID
     */
    private Long id;

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
     * 笔记标题
     */
    private String title;

    /**
     * 笔记内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}