package com.star.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应DTO
 * @author star
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {

    /**
     * JWT Token
     */
    @Schema(description = "JWT Token")
    private String token;

    /**
     * Token类型
     */
    @Schema(description = "Token类型", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Token过期时间(秒)
     */
    @Schema(description = "Token过期时间(秒)", example = "604800")
    private Long expireTime;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserDTO userInfo;

    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private List<String> roles;

    /**
     * 用户权限列表
     */
    @Schema(description = "用户权限列表")
    private List<String> permissions;
}