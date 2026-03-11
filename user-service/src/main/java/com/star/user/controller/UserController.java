package com.star.user.controller;

import com.star.common.result.R;
import com.star.common.utils.FileUploadUtil;
import com.star.user.dto.ChangePasswordRequest;
import com.star.user.dto.UpdateProfileRequest;
import com.star.user.entity.User;
import com.star.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

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

    @Value("${file.upload.path:/opt/xuetu/uploads/avatars}")
    private String uploadPath;

    @Value("${file.upload.base-url:http://localhost:8088/uploads/avatars}")
    private String baseUrl;

    @Value("${file.upload.max-size:2097152}")
    private long maxSize;

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
     * 根据用户名查询用户（供 OpenClaw/Agent 等根据 username 解析 userId 使用）
     */
    @GetMapping("/by-username")
    @Operation(summary = "根据用户名查询用户", description = "根据用户名获取用户信息，用于解析 userId")
    public R<User> getByUsername(@Parameter(description = "用户名", required = true) @RequestParam("username") String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return R.error("用户不存在: " + username);
        }
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
            @Parameter(description = "状态(0:禁用,1:启用)") @RequestParam("status") Integer status) {
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
     * 更新用户资料
     */
    @PutMapping("/{userId}/profile")
    @Operation(summary = "更新用户资料", description = "更新用户的基本信息，如昵称、邮箱、手机号、性别、生日、个人简介等")
    public R<User> updateProfile(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        User user = userService.updateProfile(userId, request);
        return R.ok(user, "用户资料更新成功");
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/{userId}/avatar")
    @Operation(summary = "上传用户头像", description = "上传用户头像文件，支持jpg、png、jpeg、gif、bmp格式，大小不超过10MB")
    public R<User> uploadAvatar(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "头像文件") @RequestParam("file") MultipartFile file) {
        try {
            log.info("开始上传用户头像: userId={}, fileName={}, fileSize={}", 
                    userId, file.getOriginalFilename(), FileUploadUtil.formatFileSize(file.getSize()));
            
            // 上传图片到服务器本地目录（使用配置的文件大小限制）
            String avatarUrl = FileUploadUtil.uploadImage(file, uploadPath, baseUrl, maxSize);
            
            // 更新用户头像URL
            User user = userService.updateAvatar(userId, avatarUrl);
            
            log.info("用户头像上传成功: userId={}, avatarUrl={}", userId, avatarUrl);
            return R.ok(user, "头像上传成功");
        } catch (IllegalArgumentException e) {
            log.warn("头像上传失败-参数错误: userId={}, error={}", userId, e.getMessage());
            return R.error(e.getMessage());
        } catch (Exception e) {
            log.error("头像上传失败: userId={}", userId, e);
            return R.error("头像上传失败: " + e.getMessage());
        }
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
