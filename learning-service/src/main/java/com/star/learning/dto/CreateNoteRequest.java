package com.star.learning.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 创建笔记请求DTO
 * 
 * @author star
 */
@Data
public class CreateNoteRequest {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 课时ID
     */
    @NotNull(message = "课时ID不能为空")
    private Long lessonId;

    /**
     * 笔记标题
     */
    @NotBlank(message = "笔记标题不能为空")
    @Size(max = 100, message = "笔记标题长度不能超过100个字符")
    private String title;

    /**
     * 笔记内容
     */
    @NotBlank(message = "笔记内容不能为空")
    @Size(max = 5000, message = "笔记内容长度不能超过5000个字符")
    private String content;
}