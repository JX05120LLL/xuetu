package com.star.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 标签请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "标签请求")
public class TagRequest {

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 20, message = "标签名称长度不能超过20个字符")
    @Schema(description = "标签名称", example = "Java", required = true)
    private String name;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式错误，应为十六进制颜色值，如：#FF5722")
    @Schema(description = "标签颜色", example = "#FF5722")
    private String color;
}