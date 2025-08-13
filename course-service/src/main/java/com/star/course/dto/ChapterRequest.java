package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 章节创建/更新请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "章节请求")
public class ChapterRequest {

    @NotBlank(message = "章节标题不能为空")
    @Size(max = 100, message = "章节标题长度不能超过100个字符")
    @Schema(description = "章节标题", example = "第一章：Java基础语法", required = true)
    private String title;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}