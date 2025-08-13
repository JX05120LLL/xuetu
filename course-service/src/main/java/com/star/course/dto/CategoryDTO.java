package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类信息DTO
 * 
 * @author star
 */
@Data
@Schema(description = "分类信息")
public class CategoryDTO {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "Java编程")
    private String name;

    @Schema(description = "父分类ID", example = "0")
    private Long parentId;

    @Schema(description = "父分类名称", example = "编程语言")
    private String parentName;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "课程数量", example = "10")
    private Integer courseCount;

    @Schema(description = "子分类列表")
    private List<CategoryDTO> children;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdTime;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedTime;
}