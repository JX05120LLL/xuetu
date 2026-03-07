package com.star.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录请求DTO
 * 
 * @author star
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginRequest {

    @NotBlank(message = "用户名/邮箱/手机号不能为空")
    @Schema(description = "用户名/邮箱/手机号", example = "zhangsan")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456")
    private String password;
}