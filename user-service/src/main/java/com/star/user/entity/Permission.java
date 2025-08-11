package com.star.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission")
@Schema(description = "权限信息")
public class Permission extends BaseEntity {

    @Schema(description = "权限名称", example = "用户管理")
    private String permissionName;

    @Schema(description = "权限标识", example = "user:manage")
    private String permissionKey;

    @Schema(description = "请求URL", example = "/api/user/**")
    private String url;

    @Schema(description = "权限描述", example = "管理用户信息的权限")
    private String description;
}