package com.star.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.star.admin.dto.PermissionDTO;
import com.star.admin.entity.Permission;
import com.star.admin.entity.RolePermission;
import com.star.admin.mapper.PermissionMapper;
import com.star.admin.mapper.RolePermissionMapper;
import com.star.admin.service.PermissionService;
import com.star.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionMapper.selectList(null);
        return permissions.stream().map(permission -> {
            PermissionDTO dto = new PermissionDTO();
            BeanUtils.copyProperties(permission, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        PermissionDTO dto = new PermissionDTO();
        BeanUtils.copyProperties(permission, dto);
        return dto;
    }

    @Override
    public Permission createPermission(Permission permission) {
        // 检查权限key是否已存在
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getPermissionKey, permission.getPermissionKey());
        if (permissionMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("权限标识已存在");
        }

        permissionMapper.insert(permission);
        log.info("创建权限成功: {}", permission.getPermissionName());
        return permission;
    }

    @Override
    public Permission updatePermission(Permission permission) {
        if (permissionMapper.selectById(permission.getId()) == null) {
            throw new BusinessException("权限不存在");
        }

        permissionMapper.updateById(permission);
        log.info("更新权限成功: {}", permission.getPermissionName());
        return permission;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePermission(Long id) {
        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getPermissionId, id);
        rolePermissionMapper.delete(queryWrapper);

        // 删除权限
        permissionMapper.deleteById(id);
        log.info("删除权限成功: {}", id);
        return true;
    }

    @Override
    public List<String> getUserPermissionKeys(Long userId) {
        return permissionMapper.selectUserPermissionKeys(userId);
    }
}
