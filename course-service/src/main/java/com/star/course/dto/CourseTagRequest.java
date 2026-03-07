package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 课程标签关联请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "课程标签关联请求")
public class CourseTagRequest {

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", example = "1", required = true)
    private Long courseId;

    @NotEmpty(message = "标签ID列表不能为空")
    @Size(max = 10, message = "每个课程最多只能设置10个标签")
    @Schema(description = "标签ID列表", required = true)
    private List<Long> tagIds;
}