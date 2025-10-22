package com.star.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.common.result.R;
import com.star.user.dto.UserLoginRequest;
import com.star.user.dto.UserLoginResponse;
import com.star.user.dto.UserRegisterRequest;
import com.star.user.entity.User;
import com.star.user.feign.AdminServiceClient;
import com.star.user.feign.RoleDTO;
import com.star.user.mapper.UserMapper;
import com.star.user.service.UserService;
import com.star.common.utils.JwtUtil;
import com.star.common.utils.PasswordUtil;
import com.star.common.constant.CommonConstant;
import com.star.user.exception.UserAuthException;
import com.star.user.exception.UserRegistrationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final AdminServiceClient adminServiceClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterRequest request) {
        log.info("用户注册: {}", request.getUsername());

        // 1. 参数校验
        validateRegisterRequest(request);

        // 2. 检查用户名、邮箱、手机号是否已存在
        if (existsByUsername(request.getUsername())) {
            throw UserRegistrationException.usernameExists(request.getUsername());
        }
        if (existsByEmail(request.getEmail())) {
            throw UserRegistrationException.emailExists(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw UserRegistrationException.phoneExists(request.getPhone());
        }

        // 3. 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setStatus(1);  // 默认启用
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        // 4. 保存用户
        save(user);

        log.info("用户注册成功: userId={}", user.getId());
        return user.getId();
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        log.info("用户登录: {}", request.getUsername());

        // 1. 查询用户
        User user = userMapper.findByIdentifier(request.getUsername());
        if (user == null) {
            throw UserAuthException.userNotFound();
        }

        // 2. 验证密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw UserAuthException.wrongPassword();
        }

        // 3. 检查用户状态
        if (user.getStatus() != 1) {
            throw UserAuthException.accountDisabled();
        }

        // 4. 调用admin-service获取用户角色和权限
        List<String> roles = Collections.emptyList();
        List<String> permissions = Collections.emptyList();
        
        try {
            // 获取用户角色
            R<List<RoleDTO>> rolesResult = adminServiceClient.getUserRoles(user.getId());
            if (rolesResult != null && rolesResult.isSuccess() && rolesResult.getData() != null) {
                roles = rolesResult.getData().stream()
                    .map(RoleDTO::getRoleName)
                    .collect(Collectors.toList());
                log.info("用户角色: {}", roles);
            }
            
            // 获取用户权限
            R<List<String>> permsResult = adminServiceClient.getUserPermissionKeys(user.getId());
            if (permsResult != null && permsResult.isSuccess() && permsResult.getData() != null) {
                permissions = permsResult.getData();
                log.info("用户权限数量: {}", permissions.size());
            }
        } catch (Exception e) {
            log.warn("获取用户角色权限失败: {}", e.getMessage());
            // 继续执行，使用空列表
        }

        // 5. 生成包含角色和权限的JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        
        String token = JwtUtil.generateToken(claims, CommonConstant.JWT.EXPIRE_TIME.longValue());
        if (token == null) {
            throw new UserAuthException("Token生成失败，请稍后重试");
        }

        // 6. 构建响应
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(CommonConstant.JWT.EXPIRE_TIME / 1000);  // 转换为秒

        UserLoginResponse.UserInfo userInfo = new UserLoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoles(roles);

        response.setUserInfo(userInfo);
        response.setPermissions(permissions);  // 🆕 添加权限列表

        log.info("用户登录成功: userId={}, roles={}", user.getId(), roles);
        return response;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public User findByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return StringUtils.hasText(phone) && findByPhone(phone) != null;
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        return userMapper.findUserRoles(userId);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return userMapper.findUserPermissions(userId);
    }

    @Override
    public Boolean disableUser(Long userId) {
        log.info("禁用用户账号: userId={}", userId);
        
        User user = getById(userId);
        if (user == null) {
            throw new UserAuthException("用户不存在");
        }
        
        if (user.getStatus() == 0) {
            throw new UserAuthException("用户已经是禁用状态");
        }
        
        user.setStatus(0); // 0:禁用
        boolean result = updateById(user);
        
        if (result) {
            log.info("用户禁用成功: userId={}, username={}", userId, user.getUsername());
        } else {
            log.error("用户禁用失败: userId={}", userId);
        }
        
        return result;
    }

    @Override
    public Boolean enableUser(Long userId) {
        log.info("启用用户账号: userId={}", userId);
        
        User user = getById(userId);
        if (user == null) {
            throw new UserAuthException("用户不存在");
        }
        
        if (user.getStatus() == 1) {
            throw new UserAuthException("用户已经是启用状态");
        }
        
        user.setStatus(1); // 1:启用
        boolean result = updateById(user);
        
        if (result) {
            log.info("用户启用成功: userId={}, username={}", userId, user.getUsername());
        } else {
            log.error("用户启用失败: userId={}", userId);
        }
        
        return result;
    }

    @Override
    public Boolean updateUserStatus(Long userId, Integer status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);
        
        // 验证状态值
        if (status != 0 && status != 1) {
            throw new UserAuthException("状态值无效，只能是0或1");
        }
        
        User user = getById(userId);
        if (user == null) {
            throw new UserAuthException("用户不存在");
        }
        
        if (user.getStatus().equals(status)) {
            String statusText = status == 1 ? "启用" : "禁用";
            throw new UserAuthException("用户已经是" + statusText + "状态");
        }
        
        user.setStatus(status);
        boolean result = updateById(user);
        
        if (result) {
            String statusText = status == 1 ? "启用" : "禁用";
            log.info("用户状态更新成功: userId={}, username={}, status={}", 
                    userId, user.getUsername(), statusText);
        } else {
            log.error("用户状态更新失败: userId={}", userId);
        }
        
        return result;
    }

    @Override
    public Boolean changePassword(Long userId, com.star.user.dto.ChangePasswordRequest request) {
        log.info("修改用户密码: userId={}", userId);
        
        // 1. 检查用户是否存在
        User user = getById(userId);
        if (user == null) {
            throw new UserAuthException("用户不存在");
        }
        
        // 2. 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new UserAuthException("两次输入的密码不一致");
        }
        
        // 3. 验证旧密码是否正确
        if (!PasswordUtil.matches(request.getOldPassword(), user.getPassword())) {
            throw new UserAuthException("旧密码错误");
        }
        
        // 4. 检查新密码是否与旧密码相同
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new UserAuthException("新密码不能与旧密码相同");
        }
        
        // 5. 更新密码
        String encodedNewPassword = PasswordUtil.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);
        boolean result = updateById(user);
        
        if (result) {
            log.info("密码修改成功: userId={}, username={}", userId, user.getUsername());
        } else {
            log.error("密码修改失败: userId={}", userId);
        }
        
        return result;
    }

    @Override
    public User getUserInfo(Long userId) {
        log.info("获取用户信息: userId={}", userId);
        
        User user = getById(userId);
        if (user == null) {
            throw new UserAuthException("用户不存在");
        }
        
        // 清除敏感信息
        user.setPassword(null);
        
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateProfile(Long userId, com.star.user.dto.UpdateProfileRequest request) {
        log.info("更新用户资料: userId={}", userId);
        
        // 1. 检查用户是否存在
        User user = getById(userId);
        if (user == null) {
            throw new UserAuthException("用户不存在");
        }
        
        // 2. 检查邮箱是否被其他用户使用
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            User existUser = findByEmail(request.getEmail());
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new UserAuthException("邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }
        
        // 3. 检查手机号是否被其他用户使用
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            User existUser = findByPhone(request.getPhone());
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new UserAuthException("手机号已被使用");
            }
            user.setPhone(request.getPhone());
        }
        
        // 4. 更新其他字段
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (StringUtils.hasText(request.getBirthday())) {
            user.setBirthday(request.getBirthday());
        }
        if (StringUtils.hasText(request.getBio())) {
            user.setBio(request.getBio());
        }
        
        user.setUpdatedTime(LocalDateTime.now());
        
        // 5. 保存更新
        boolean result = updateById(user);
        if (!result) {
            throw new UserAuthException("更新用户资料失败");
        }
        
        log.info("用户资料更新成功: userId={}, username={}", userId, user.getUsername());
        
        // 清除敏感信息
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateAvatar(Long userId, String avatarUrl) {
        log.info("更新用户头像: userId={}, avatarUrl={}", userId, avatarUrl);
        
        // 1. 检查用户是否存在
        User user = getById(userId);
        if (user == null) {
            throw new UserAuthException("用户不存在");
        }
        
        // 2. 更新头像
        user.setAvatar(avatarUrl);
        user.setUpdatedTime(LocalDateTime.now());
        
        boolean result = updateById(user);
        if (!result) {
            throw new UserAuthException("更新头像失败");
        }
        
        log.info("用户头像更新成功: userId={}, username={}", userId, user.getUsername());
        
        // 清除敏感信息
        user.setPassword(null);
        return user;
    }

    /**
     * 校验注册请求参数
     */
    private void validateRegisterRequest(UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw UserRegistrationException.passwordMismatch();
        }
    }
}