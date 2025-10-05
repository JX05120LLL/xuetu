/**
 * 订单相关类型定义
 */

export interface Order {
  id: number
  orderNo: string
  userId: number
  totalAmount: number
  actualAmount: number
  discountAmount: number
  status: number
  statusName: string
  remark: string
  createTime: string
  updateTime: string
  orderItems: OrderItem[]
  paymentRecords: PaymentRecord[]
}

export interface OrderItem {
  id: number
  orderId: number
  courseId: number
  courseTitle: string
  courseCover: string
  price: number
  createTime: string
}

export interface PaymentRecord {
  id: number
  orderId: number
  paymentNo: string
  paymentAmount: number
  paymentType: number
  paymentTypeName: string
  paymentStatus: number
  paymentStatusName: string
  paymentTime: string
  createTime: string
}

export interface CreateOrderRequest {
  courseIds: number[]
  remark?: string
}

export interface PaymentRequest {
  orderNo: string
  paymentType: number
  remark?: string
}