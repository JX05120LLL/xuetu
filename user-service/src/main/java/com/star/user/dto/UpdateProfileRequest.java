package com.star.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 更新用户资料请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "更新用户资料请求")
public class UpdateProfileRequest {

    @Size(max = 20, message = "昵称最多20个字符")
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13888888888")
    private String phone;

    @Schema(description = "性别(0:保密,1:男,2:女)", example = "1")
    private Integer gender;

    @Schema(description = "生日", example = "2000-01-01")
    private String birthday;

    @Size(max = 200, message = "个人简介最多200个字符")
    @Schema(description = "个人简介", example = "这是我的个人简介")
    private String bio;
}
