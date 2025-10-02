package com.star.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体
 * 
 * @author star
 */
@Data
@TableName("user_role")
public class UserRole {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}
