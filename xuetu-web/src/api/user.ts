import request from './request'
import type { User, LoginRequest, LoginResponse, RegisterRequest, ChangePasswordRequest } from '@/types/user'

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
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
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