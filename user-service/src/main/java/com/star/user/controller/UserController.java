package com.star.user.controller;

import com.star.common.result.R;
import com.star.user.dto.ChangePasswordRequest;
import com.star.user.entity.User;
import com.star.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息管理、账号状态管理等功能")
public class UserController {

    private final UserService userService;

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    public R<User> getUserInfo(@Parameter(description = "用户ID") @PathVariable Long userId) {
        User user = userService.getUserInfo(userId);
        return R.ok(user);
    }

    /**
     * 禁用用户账号（管理员操作）
     */
    @PostMapping("/{userId}/disable")
    @Operation(summary = "禁用用户", description = "管理员禁用用户账号，禁用后用户无法登录")
    public R<Boolean> disableUser(@Parameter(description = "用户ID") @PathVariable Long userId) {
        Boolean result = userService.disableUser(userId);
        return R.ok(result, "用户已禁用");
    }

    /**
     * 启用用户账号（管理员操作）
     */
    @PostMapping("/{userId}/enable")
    @Operation(summary = "启用用户", description = "管理员启用用户账号，恢复用户登录权限")
    public R<Boolean> enableUser(@Parameter(description = "用户ID") @PathVariable Long userId) {
        Boolean result = userService.enableUser(userId);
        return R.ok(result, "用户已启用");
    }

    /**
     * 更新用户状态（通用方法）
     */
    @PostMapping("/{userId}/status")
    @Operation(summary = "更新用户状态", description = "管理员更新用户账号状态")
    public R<Boolean> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "状态(0:禁用,1:启用)") @RequestParam Integer status) {
        Boolean result = userService.updateUserStatus(userId, status);
        String statusText = status == 1 ? "启用" : "禁用";
        return R.ok(result, "用户状态已更新为" + statusText);
    }

    /**
     * 修改用户密码
     */
    @PostMapping("/{userId}/change-password")
    @Operation(summary = "修改密码", description = "用户修改自己的密码，需要验证旧密码")
    public R<Boolean> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        Boolean result = userService.changePassword(userId, request);
        return R.ok(result, "密码修改成功");
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查用户管理服务是否正常")
    public R<String> health() {
        return R.ok("用户管理服务运行正常");
    }
}
