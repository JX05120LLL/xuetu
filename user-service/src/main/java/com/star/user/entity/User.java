package com.star.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@Schema(description = "用户信息")
public class User extends BaseEntity {

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "密码(加密存储)", example = "$2a$10$...")
    private String password;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "手机号", example = "13888888888")
    private String phone;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "状态(0:禁用,1:启用)", example = "1")
    private Integer status;
}