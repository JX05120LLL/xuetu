package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 分类创建/更新请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "分类请求")
public class CategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    @Schema(description = "分类名称", example = "Java编程", required = true)
    private String name;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId = 0L;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;
}