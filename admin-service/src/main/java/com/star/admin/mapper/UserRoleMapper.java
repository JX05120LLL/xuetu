package com.star.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.admin.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 * 
 * @author star
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
