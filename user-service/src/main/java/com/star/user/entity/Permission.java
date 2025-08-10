package com.star.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission")
public class Permission extends BaseEntity {

    /**
     * 权限名称
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限标识
     */
    @TableField("permission_key")
    private String permissionKey;

    /**
     * 请求URL
     */
    @TableField("url")
    private String url;

    /**
     * 权限描述
     */
    @TableField("description")
    private String description;
}