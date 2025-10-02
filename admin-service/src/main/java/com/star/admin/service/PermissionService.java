package com.star.admin.service;

import com.star.admin.dto.PermissionDTO;
import com.star.admin.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 * 
 * @author star
 */
public interface PermissionService {

    /**
     * 获取所有权限
     */
    List<PermissionDTO> getAllPermissions();

    /**
     * 根据ID获取权限
     */
    PermissionDTO getPermissionById(Long id);

    /**
     * 创建权限
     */
    Permission createPermission(Permission permission);

    /**
     * 更新权限
     */
    Permission updatePermission(Permission permission);

    /**
     * 删除权限
     */
    Boolean deletePermission(Long id);

    /**
     * 获取用户的权限列表
     */
    List<String> getUserPermissionKeys(Long userId);
}
