package com.star.ai.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 章节 DTO（与 course-service 返回结构兼容）
 */
@Data
public class ChapterDTO implements Serializable {
    private Long id;
    private Long courseId;
    private String title;
    private Integer sortOrder;
    private Integer lessonCount;
    private List<LessonDTO> lessons;
}
