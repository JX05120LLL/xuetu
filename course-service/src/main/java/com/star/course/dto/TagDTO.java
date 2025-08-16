package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签信息DTO
 * 
 * @author star
 */
@Data
@Schema(description = "标签信息")
public class TagDTO {

    @Schema(description = "标签ID", example = "1")
    private Long id;

    @Schema(description = "标签名称", example = "Java")
    private String name;

    @Schema(description = "标签颜色", example = "#FF5722")
    private String color;

    @Schema(description = "使用次数", example = "10")
    private Integer usageCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}