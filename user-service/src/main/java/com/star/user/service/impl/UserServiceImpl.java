package com.star.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.user.dto.UserLoginRequest;
import com.star.user.dto.UserLoginResponse;
import com.star.user.dto.UserRegisterRequest;
import com.star.user.entity.User;
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
import java.util.List;

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

        // 4. 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        if (token == null) {
            throw new UserAuthException("Token生成失败，请稍后重试");
        }

        // 5. 查询用户角色
        List<String> roles = getUserRoles(user.getId());

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

        log.info("用户登录成功: userId={}", user.getId());
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

    /**
     * 校验注册请求参数
     */
    private void validateRegisterRequest(UserRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw UserRegistrationException.passwordMismatch();
        }
    }
}