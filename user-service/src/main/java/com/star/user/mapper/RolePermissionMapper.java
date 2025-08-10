package com.star.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.user.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关联Mapper接口
 * @author star
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}