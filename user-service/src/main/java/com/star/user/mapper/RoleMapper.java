package com.star.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.user.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 角色Mapper接口
 * @author star
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色名称查询角色
     * @param roleName 角色名称
     * @return 角色信息
     */
    @Select("SELECT * FROM role WHERE role_name = #{roleName}")
    Role selectByRoleName(@Param("roleName") String roleName);
}