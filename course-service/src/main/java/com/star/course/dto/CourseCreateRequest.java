package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 创建课程请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "创建课程请求")
public class CourseCreateRequest {

    @NotBlank(message = "课程标题不能为空")
    @Size(max = 100, message = "课程标题长度不能超过100个字符")
    @Schema(description = "课程标题", example = "Java基础入门教程", required = true)
    private String title;

    @Size(max = 2000, message = "课程描述长度不能超过2000个字符")
    @Schema(description = "课程描述", example = "从零开始学习Java编程语言")
    private String description;

    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @NotNull(message = "课程价格不能为空")
    @DecimalMin(value = "0.00", message = "课程价格不能小于0")
    @DecimalMax(value = "99999.99", message = "课程价格不能超过99999.99")
    @Schema(description = "课程价格", example = "99.00", required = true)
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "原价不能小于0")
    @DecimalMax(value = "99999.99", message = "原价不能超过99999.99")
    @Schema(description = "原价", example = "199.00")
    private BigDecimal originalPrice;

    @Schema(description = "讲师ID", example = "1")
    private Long teacherId;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", example = "1", required = true)
    private Long categoryId;

    @Min(value = 0, message = "难度级别必须在0-2之间")
    @Max(value = 2, message = "难度级别必须在0-2之间")
    @Schema(description = "难度级别(0:初级,1:中级,2:高级)", example = "0")
    private Integer level = 0;

    @Min(value = 0, message = "状态必须在0-2之间")
    @Max(value = 2, message = "状态必须在0-2之间")
    @Schema(description = "状态(0:未发布,1:已发布,2:已下架)", example = "0")
    private Integer status = 0;

    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private java.util.List<Long> tagIds;
}