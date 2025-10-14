import request from './request'
import type { User, LoginRequest, LoginResponse, RegisterRequest, ChangePasswordRequest, UpdateProfileRequest } from '@/types/user'

/**
 * 用户注册
 */
export function register(data: RegisterRequest) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 用户登录
 */
export function login(data: LoginRequest): Promise<LoginResponse> {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 * 注意: JWT无状态认证，登出只需前端清除token，无需调用后端
 */
export function logout() {
  // 返回一个resolved的Promise，保持API一致性
  return Promise.resolve()
}

/**
 * 获取用户信息
 */
export function getUserInfo(userId: number): Promise<User> {
  return request({
    url: `/user/${userId}`,
    method: 'get'
  })
}

/**
 * 修改密码
 */
export function changePassword(userId: number, data: ChangePasswordRequest) {
  return request({
    url: `/user/${userId}/change-password`,
    method: 'post',
    data
  })
}

/**
 * 检查用户名是否可用
 */
export function checkUsername(username: string): Promise<boolean> {
  return request({
    url: '/auth/check-username',
    method: 'get',
    params: { username }
  })
}

/**
 * 检查邮箱是否可用
 */
export function checkEmail(email: string): Promise<boolean> {
  return request({
    url: '/auth/check-email',
    method: 'get',
    params: { email }
  })
}

/**
 * 更新用户资料
 */
export function updateProfile(userId: number, data: UpdateProfileRequest): Promise<User> {
  return request({
    url: `/user/${userId}/profile`,
    method: 'put',
    data
  })
}

/**
 * 更新用户头像
 * 注意: 暂无后端接口，仅做前端演示
 */
export function updateAvatar(userId: number, avatarUrl: string): Promise<User> {
  // 实际项目中这里应该是调用上传接口
  return request({
    url: `/user/${userId}/avatar`,
    method: 'post',
    data: { avatarUrl }
  })
}

/**
 * 更新用户通知设置
 * 注意: 暂无后端接口，仅做前端演示
 */
export function updateNotificationSettings(userId: number, settings: {
  systemNotify: boolean,
  emailNotify: boolean,
  messageNotify: boolean
}): Promise<boolean> {
  return request({
    url: `/user/${userId}/notification-settings`,
    method: 'put',
    data: settings
  })
}