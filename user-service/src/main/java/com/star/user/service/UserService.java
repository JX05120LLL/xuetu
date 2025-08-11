package com.star.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.user.dto.UserLoginRequest;
import com.star.user.dto.UserLoginResponse;
import com.star.user.dto.UserRegisterRequest;
import com.star.user.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author star
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * 
     * @param request 注册请求参数
     * @return 注册成功的用户ID
     */
    Long register(UserRegisterRequest request);

    /**
     * 用户登录
     * 
     * @param request 登录请求参数
     * @return 登录响应信息（包含Token）
     */
    UserLoginResponse login(UserLoginRequest request);

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(String email);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    User findByPhone(String phone);

    /**
     * 检查用户名是否已存在
     * 
     * @param username 用户名
     * @return true-已存在，false-不存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否已存在
     * 
     * @param email 邮箱
     * @return true-已存在，false-不存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否已存在
     * 
     * @param phone 手机号
     * @return true-已存在，false-不存在
     */
    boolean existsByPhone(String phone);

    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);
}