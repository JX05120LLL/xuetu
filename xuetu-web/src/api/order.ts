import request from './request'
import type { Order, CreateOrderRequest, PaymentRequest, PaymentRecord } from '@/types/order'
import type { PageParam, PageResult } from '@/types/common'

/**
 * 创建订单
 */
export function createOrder(data: CreateOrderRequest): Promise<Order> {
  return request({
    url: '/api/orders',
    method: 'post',
    data
  })
}

/**
 * 获取我的订单列表
 */
export function getMyOrders(params: PageParam & { status?: number }): Promise<PageResult<Order>> {
  return request({
    url: '/api/orders/my',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(orderId: number): Promise<Order> {
  return request({
    url: `/api/orders/${orderId}`,
    method: 'get'
  })
}

/**
 * 根据订单号获取订单详情
 */
export function getOrderByOrderNo(orderNo: string): Promise<Order> {
  return request({
    url: `/api/orders/order-no/${orderNo}`,
    method: 'get'
  })
}

/**
 * 取消订单
 */
export function cancelOrder(orderId: number) {
  return request({
    url: `/api/orders/${orderId}/cancel`,
    method: 'put'
  })
}

/**
 * 创建支付
 */
export function createPayment(data: PaymentRequest): Promise<PaymentRecord> {
  return request({
    url: '/api/payments',
    method: 'post',
    data
  })
}

/**
 * 模拟支付
 */
export function simulatePayment(paymentNo: string) {
  return request({
    url: `/api/payments/${paymentNo}/simulate`,
    method: 'post'
  })
}

/**
 * 查询支付记录
 */
export function getPaymentRecord(paymentNo: string): Promise<PaymentRecord> {
  return request({
    url: `/api/payments/${paymentNo}`,
    method: 'get'
  })
}

/**
 * 查询支付状态
 */
export function getPaymentStatus(paymentNo: string): Promise<number> {
  return request({
    url: `/api/payments/${paymentNo}/status`,
    method: 'get'
  })
}