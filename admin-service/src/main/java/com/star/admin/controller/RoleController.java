package com.star.admin.controller;

import com.star.admin.dto.AssignRoleRequest;
import com.star.admin.dto.GrantPermissionRequest;
import com.star.admin.dto.PermissionDTO;
import com.star.admin.dto.RoleDTO;
import com.star.admin.entity.Role;
import com.star.admin.service.RoleService;
import com.star.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 角色管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色的增删改查、角色分配、权限授予")
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取所有角色
     */
    @GetMapping("/list")
    @Operation(summary = "获取角色列表", description = "查询所有角色及其权限")
    public R<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return R.ok(roles);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情", description = "根据角色ID查询详细信息")
    public R<RoleDTO> getRoleById(@Parameter(description = "角色ID") @PathVariable Long id) {
        RoleDTO role = roleService.getRoleById(id);
        return R.ok(role);
    }

    /**
     * 创建角色
     */
    @PostMapping("/create")
    @Operation(summary = "创建角色", description = "创建新的角色")
    public R<Role> createRole(@RequestBody Role role) {
        Role created = roleService.createRole(role);
        return R.ok(created, "角色创建成功");
    }

    /**
     * 更新角色
     */
    @PutMapping("/update")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public R<Role> updateRole(@RequestBody Role role) {
        Role updated = roleService.updateRole(role);
        return R.ok(updated, "角色更新成功");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "删除指定角色")
    public R<Boolean> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        Boolean result = roleService.deleteRole(id);
        return R.ok(result, "角色删除成功");
    }

    /**
     * 获取用户的角色
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户角色", description = "查询指定用户的所有角色")
    public R<List<Role>> getUserRoles(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<Role> roles = roleService.getUserRoles(userId);
        return R.ok(roles);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assign")
    @Operation(summary = "分配角色", description = "为用户分配指定角色")
    public R<Boolean> assignRole(@Valid @RequestBody AssignRoleRequest request) {
        Boolean result = roleService.assignRoleToUser(request.getUserId(), request.getRoleId());
        return R.ok(result, "角色分配成功");
    }

    /**
     * 为角色授予权限
     */
    @PostMapping("/grant")
    @Operation(summary = "授予权限", description = "为角色授予一组权限")
    public R<Boolean> grantPermissions(@Valid @RequestBody GrantPermissionRequest request) {
        Boolean result = roleService.grantPermissionsToRole(request.getRoleId(), request.getPermissionIds());
        return R.ok(result, "权限授予成功");
    }

    /**
     * 获取角色的权限
     */
    @GetMapping("/{id}/permissions")
    @Operation(summary = "获取角色权限", description = "查询角色拥有的所有权限")
    public R<List<PermissionDTO>> getRolePermissions(@Parameter(description = "角色ID") @PathVariable Long id) {
        List<PermissionDTO> permissions = roleService.getRolePermissions(id);
        return R.ok(permissions);
    }
}
