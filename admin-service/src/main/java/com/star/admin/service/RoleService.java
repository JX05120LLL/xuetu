package com.star.admin.service;

import com.star.admin.dto.PermissionDTO;
import com.star.admin.dto.RoleDTO;
import com.star.admin.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 * 
 * @author star
 */
public interface RoleService {

    /**
     * 获取所有角色
     */
    List<RoleDTO> getAllRoles();

    /**
     * 根据ID获取角色
     */
    RoleDTO getRoleById(Long id);

    /**
     * 创建角色
     */
    Role createRole(Role role);

    /**
     * 更新角色
     */
    Role updateRole(Role role);

    /**
     * 删除角色
     */
    Boolean deleteRole(Long id);

    /**
     * 获取用户的角色列表
     */
    List<Role> getUserRoles(Long userId);

    /**
     * 为用户分配角色
     */
    Boolean assignRoleToUser(Long userId, Long roleId);

    /**
     * 为角色分配权限
     */
    Boolean grantPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色的权限列表
     */
    List<PermissionDTO> getRolePermissions(Long roleId);
}
