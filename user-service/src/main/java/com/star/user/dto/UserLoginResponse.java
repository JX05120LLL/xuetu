package com.star.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户登录响应DTO
 * 
 * @author star
 */
@Data
@Schema(description = "用户登录响应")
public class UserLoginResponse {

    @Schema(description = "访问Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token类型", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "过期时间(秒)", example = "86400")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Schema(description = "用户基本信息")
    public static class UserInfo {
        @Schema(description = "用户ID", example = "1")
        private Long id;

        @Schema(description = "用户名", example = "zhangsan")
        private String username;

        @Schema(description = "昵称", example = "张三")
        private String nickname;

        @Schema(description = "邮箱", example = "zhangsan@example.com")
        private String email;

        @Schema(description = "头像", example = "https://example.com/avatar.jpg")
        private String avatar;

        @Schema(description = "角色列表", example = "[\"STUDENT\"]")
        private List<String> roles;
    }
}