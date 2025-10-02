package com.star.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色权限关联实体
 * 
 * @author star
 */
@Data
@TableName("role_permission")
public class RolePermission {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限ID
     */
    private Long permissionId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
