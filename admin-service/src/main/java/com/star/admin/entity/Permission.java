package com.star.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.star.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体
 * 
 * @author star
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission")
public class Permission extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限标识
     */
    private String permissionKey;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 权限描述
     */
    private String description;
}
