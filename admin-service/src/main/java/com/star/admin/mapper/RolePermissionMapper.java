package com.star.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.admin.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper
 * 
 * @author star
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
