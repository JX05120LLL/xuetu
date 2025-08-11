package com.star.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
@Schema(description = "角色信息")
public class Role extends BaseEntity {

    @Schema(description = "角色名称", example = "ADMIN")
    private String roleName;

    @Schema(description = "角色描述", example = "系统管理员")
    private String description;
}