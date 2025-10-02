package com.star.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.admin.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper
 * 
 * @author star
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询用户的角色列表
     */
    @Select("SELECT r.* FROM role r " +
            "INNER JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> selectUserRoles(Long userId);
}
