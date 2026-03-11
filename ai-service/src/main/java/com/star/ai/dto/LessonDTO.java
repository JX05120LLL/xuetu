package com.star.ai.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 课时 DTO
 */
@Data
public class LessonDTO implements Serializable {
    private Long id;
    private Long chapterId;
    private String title;
    private Integer duration;
    private Integer sortOrder;
}
