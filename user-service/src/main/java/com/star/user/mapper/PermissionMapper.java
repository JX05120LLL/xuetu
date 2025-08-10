package com.star.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.user.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper接口
 * @author star
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}