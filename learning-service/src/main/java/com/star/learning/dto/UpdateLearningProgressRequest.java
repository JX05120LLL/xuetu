package com.star.learning.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 更新学习进度请求DTO
 * 
 * @author star
 */
@Data
public class UpdateLearningProgressRequest {

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
     * 学习进度(秒)
     */
    @NotNull(message = "学习进度不能为空")
    @Min(value = 0, message = "学习进度不能小于0")
    private Integer progress;

    /**
     * 是否完成(0:否,1:是)
     */
    private Integer isCompleted;
}