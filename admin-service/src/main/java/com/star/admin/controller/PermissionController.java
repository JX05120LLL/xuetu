package com.star.admin.controller;

import com.star.admin.dto.PermissionDTO;
import com.star.admin.entity.Permission;
import com.star.admin.service.PermissionService;
import com.star.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限的增删改查")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 获取所有权限
     */
    @GetMapping("/list")
    @Operation(summary = "获取权限列表", description = "查询所有权限")
    public R<List<PermissionDTO>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return R.ok(permissions);
    }

    /**
     * 根据ID获取权限
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取权限详情", description = "根据权限ID查询详细信息")
    public R<PermissionDTO> getPermissionById(@Parameter(description = "权限ID") @PathVariable Long id) {
        PermissionDTO permission = permissionService.getPermissionById(id);
        return R.ok(permission);
    }

    /**
     * 创建权限
     */
    @PostMapping("/create")
    @Operation(summary = "创建权限", description = "创建新的权限")
    public R<Permission> createPermission(@RequestBody Permission permission) {
        Permission created = permissionService.createPermission(permission);
        return R.ok(created, "权限创建成功");
    }

    /**
     * 更新权限
     */
    @PutMapping("/update")
    @Operation(summary = "更新权限", description = "更新权限信息")
    public R<Permission> updatePermission(@RequestBody Permission permission) {
        Permission updated = permissionService.updatePermission(permission);
        return R.ok(updated, "权限更新成功");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "删除指定权限")
    public R<Boolean> deletePermission(@Parameter(description = "权限ID") @PathVariable Long id) {
        Boolean result = permissionService.deletePermission(id);
        return R.ok(result, "权限删除成功");
    }

    /**
     * 获取用户的权限Key列表
     */
    @GetMapping("/user/{userId}/keys")
    @Operation(summary = "获取用户权限Key", description = "查询用户的权限标识列表")
    public R<List<String>> getUserPermissionKeys(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<String> keys = permissionService.getUserPermissionKeys(userId);
        return R.ok(keys);
    }
}
