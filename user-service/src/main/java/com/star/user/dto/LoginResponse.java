package com.star.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应DTO
 * @author star
 */
@Data
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

    public LoginResponse(String token, Long expireTime, UserDTO userInfo) {
        this.token = token;
        this.expireTime = expireTime;
        this.userInfo = userInfo;
    }
}