package com.star.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.admin.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper
 * 
 * @author star
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 查询角色的权限列表
     */
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<Permission> selectRolePermissions(Long roleId);

    /**
     * 查询用户的权限列表
     */
    @Select("SELECT DISTINCT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Permission> selectUserPermissions(Long userId);
    
    /**
     * 查询用户的权限Key列表
     */
    @Select("SELECT DISTINCT p.permission_key FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectUserPermissionKeys(Long userId);
}
