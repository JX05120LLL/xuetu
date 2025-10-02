package com.star.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.admin.dto.PermissionDTO;
import com.star.admin.dto.RoleDTO;
import com.star.admin.entity.Permission;
import com.star.admin.entity.Role;
import com.star.admin.entity.RolePermission;
import com.star.admin.entity.UserRole;
import com.star.admin.mapper.PermissionMapper;
import com.star.admin.mapper.RoleMapper;
import com.star.admin.mapper.RolePermissionMapper;
import com.star.admin.mapper.UserRoleMapper;
import com.star.admin.service.RoleService;
import com.star.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleMapper.selectList(null);
        return roles.stream().map(role -> {
            RoleDTO dto = new RoleDTO();
            BeanUtils.copyProperties(role, dto);
            
            // 查询角色的权限
            List<Permission> permissions = permissionMapper.selectRolePermissions(role.getId());
            List<PermissionDTO> permissionDTOs = permissions.stream().map(permission -> {
                PermissionDTO permissionDTO = new PermissionDTO();
                BeanUtils.copyProperties(permission, permissionDTO);
                return permissionDTO;
            }).collect(Collectors.toList());
            
            dto.setPermissions(permissionDTOs);
            dto.setPermissionIds(permissions.stream().map(Permission::getId).collect(Collectors.toList()));
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);

        // 查询角色的权限
        List<Permission> permissions = permissionMapper.selectRolePermissions(id);
        List<PermissionDTO> permissionDTOs = permissions.stream().map(permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            BeanUtils.copyProperties(permission, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());

        dto.setPermissions(permissionDTOs);
        dto.setPermissionIds(permissions.stream().map(Permission::getId).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public Role createRole(Role role) {
        // 检查角色名是否已存在
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleName, role.getRoleName());
        if (roleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("角色名已存在");
        }

        roleMapper.insert(role);
        log.info("创建角色成功: {}", role.getRoleName());
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        if (roleMapper.selectById(role.getId()) == null) {
            throw new BusinessException("角色不存在");
        }

        roleMapper.updateById(role);
        log.info("更新角色成功: {}", role.getRoleName());
        return role;
    }

    @Override
    public Boolean deleteRole(Long id) {
        // 检查是否有用户使用此角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getRoleId, id);
        if (userRoleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("该角色下还有用户，无法删除");
        }

        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> rpQueryWrapper = new LambdaQueryWrapper<>();
        rpQueryWrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(rpQueryWrapper);

        // 删除角色
        roleMapper.deleteById(id);
        log.info("删除角色成功: {}", id);
        return true;
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        return roleMapper.selectUserRoles(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean assignRoleToUser(Long userId, Long roleId) {
        // 检查角色是否存在
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否已分配
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userId)
                    .eq(UserRole::getRoleId, roleId);
        if (userRoleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户已拥有该角色");
        }

        // 创建用户角色关联
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreatedTime(LocalDateTime.now());
        
        userRoleMapper.insert(userRole);
        log.info("为用户{}分配角色{}成功", userId, roleId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean grantPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 检查角色是否存在
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException("角色不存在");
        }

        // 删除原有权限
        LambdaQueryWrapper<RolePermission> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(deleteWrapper);

        // 添加新权限
        for (Long permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermission.setCreatedTime(LocalDateTime.now());
            rolePermissionMapper.insert(rolePermission);
        }

        log.info("为角色{}分配{}个权限成功", roleId, permissionIds.size());
        return true;
    }

    @Override
    public List<PermissionDTO> getRolePermissions(Long roleId) {
        List<Permission> permissions = permissionMapper.selectRolePermissions(roleId);
        return permissions.stream().map(permission -> {
            PermissionDTO dto = new PermissionDTO();
            BeanUtils.copyProperties(permission, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}
