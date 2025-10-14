<template>
  <div class="order-detail-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <div class="page-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/order/list' }">我的订单</el-breadcrumb-item>
            <el-breadcrumb-item>订单详情</el-breadcrumb-item>
          </el-breadcrumb>
          
          <h2 class="page-title">订单详情</h2>
        </div>

        <div class="order-detail" v-loading="loading">
          <template v-if="order">
            <!-- 订单状态 -->
            <div class="order-status-card">
              <div class="status-section">
                <div class="status-icon" :class="statusClass">
                  <el-icon :size="40">
                    <component :is="statusIcon"></component>
                  </el-icon>
                </div>
                <div class="status-info">
                  <h3>{{ order.statusName }}</h3>
                  <p v-if="order.status === 1">
                    请在 <span class="countdown">{{ paymentCountdown }}</span> 内完成支付，
                    超时订单将自动取消
                  </p>
                  <p v-if="order.status === 2">
                    您已成功支付订单，可前往课程中心开始学习
                  </p>
                  <p v-if="order.status === 3">
                    订单已取消，您可以重新下单
                  </p>
                </div>
              </div>

              <div class="status-actions">
                <el-button 
                  v-if="order.status === 1" 
                  type="primary"
                  @click="goToPay"
                >
                  去支付
                </el-button>
                <el-button 
                  v-if="order.status === 1"
                  type="success"
                  @click="handleQuickPay"
                  :loading="quickPaying"
                >
                  <el-icon><Lightning /></el-icon>
                  模拟支付
                </el-button>
                <el-button 
                  v-if="order.status === 1"
                  @click="handleCancelOrder"
                  :loading="canceling"
                >
                  取消订单
                </el-button>
                <el-button
                  v-if="order.status === 2"
                  @click="goToCourse"
                >
                  去学习
                </el-button>
                <el-button
                  v-if="order.status === 3"
                  @click="goToCourseList"
                >
                  再次购买
                </el-button>
              </div>
            </div>

            <!-- 订单进度 -->
            <div class="order-progress-card">
              <h3 class="card-title">订单进度</h3>
              <el-steps 
                :active="orderStepActive" 
                finish-status="success"
                process-status="process"
                class="order-steps"
              >
                <el-step title="提交订单" :description="formatDate(order.createTime)"></el-step>
                <el-step 
                  title="支付订单" 
                  :description="order.status >= 2 && order.paymentRecords?.length > 0 
                    ? formatDate(order.paymentRecords[0].paymentTime) 
                    : '等待支付'"
                ></el-step>
                <el-step 
                  title="交易完成" 
                  :description="order.status === 2 ? '交易成功' : ''"
                ></el-step>
              </el-steps>
            </div>

            <!-- 订单信息 -->
            <div class="order-info-card">
              <h3 class="card-title">订单信息</h3>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDate(order.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="订单状态">
                  <el-tag :type="statusTagType">{{ order.statusName }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="支付方式">
                  {{ getPaymentMethodName(order.paymentRecords?.[0]?.paymentType) }}
                </el-descriptions-item>
                <el-descriptions-item label="订单备注" :span="2">
                  {{ order.remark || '无' }}
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- 订单商品 -->
            <div class="order-items-card">
              <h3 class="card-title">订单商品</h3>
              <div class="order-items">
                <div class="item-header">
                  <div class="item-course">课程信息</div>
                  <div class="item-price">价格</div>
                </div>

                <div 
                  v-for="item in order.orderItems" 
                  :key="item.id"
                  class="item-row"
                >
                  <div class="item-course">
                    <div class="course-image">
                      <img :src="item.courseCover" :alt="item.courseTitle" />
                    </div>
                    <div class="course-info">
                      <h4>{{ item.courseTitle }}</h4>
                    </div>
                  </div>
                  <div class="item-price">{{ formatPrice(item.price) }}</div>
                </div>
              </div>
            </div>

            <!-- 价格信息 -->
            <div class="order-price-card">
              <h3 class="card-title">价格信息</h3>
              <div class="price-details">
                <div class="price-item">
                  <span>商品总价</span>
                  <span>{{ formatPrice(order.totalAmount) }}</span>
                </div>
                <div class="price-item" v-if="order.discountAmount > 0">
                  <span>优惠金额</span>
                  <span>- {{ formatPrice(order.discountAmount) }}</span>
                </div>
                <div class="price-divider"></div>
                <div class="price-item total">
                  <span>实付金额</span>
                  <span class="total-price">{{ formatPrice(order.actualAmount) }}</span>
                </div>
              </div>
            </div>

            <!-- 支付记录 -->
            <div class="payment-records-card" v-if="order.paymentRecords && order.paymentRecords.length > 0">
              <h3 class="card-title">支付记录</h3>
              <el-table :data="order.paymentRecords" style="width: 100%">
                <el-table-column prop="paymentNo" label="支付单号" width="180" />
                <el-table-column prop="paymentAmount" label="支付金额">
                  <template #default="{ row }">
                    {{ formatPrice(row.paymentAmount) }}
                  </template>
                </el-table-column>
                <el-table-column prop="paymentTypeName" label="支付方式" />
                <el-table-column prop="paymentStatusName" label="支付状态">
                  <template #default="{ row }">
                    <el-tag :type="row.paymentStatus === 2 ? 'success' : 'info'">
                      {{ row.paymentStatusName }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="paymentTime" label="支付时间">
                  <template #default="{ row }">
                    {{ row.paymentTime ? formatDate(row.paymentTime) : '未支付' }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>

          <!-- 空状态 -->
          <el-empty 
            v-else-if="!loading" 
            description="订单不存在或已被删除" 
          >
            <el-button type="primary" @click="goToOrderList">返回订单列表</el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { formatDate, formatPrice } from '@/utils/format'
import { getOrderDetail, cancelOrder, createPayment, simulatePayment } from '@/api/order'
import type { Order } from '@/types/order'
import { CircleCheck, Warning, MoreFilled, Lightning } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const canceling = ref(false)
const quickPaying = ref(false)
const order = ref<Order | null>(null)
const countdownTimer = ref<number | null>(null)
const paymentTimeLeft = ref(30 * 60) // 默认30分钟

// 获取订单状态对应的样式和图标
const statusClass = computed(() => {
  if (!order.value) return ''
  switch (order.value.status) {
    case 1: return 'status-pending'
    case 2: return 'status-success'
    case 3: return 'status-canceled'
    default: return ''
  }
})

const statusIcon = computed(() => {
  if (!order.value) return ''
  switch (order.value.status) {
    case 1: return 'MoreFilled'
    case 2: return 'CircleCheck'
    case 3: return 'Warning'
    default: return ''
  }
})

const statusTagType = computed(() => {
  if (!order.value) return ''
  switch (order.value.status) {
    case 1: return 'warning'
    case 2: return 'success'
    case 3: return 'info'
    default: return ''
  }
})

// 订单进度
const orderStepActive = computed(() => {
  if (!order.value) return 0
  switch (order.value.status) {
    case 1: return 1
    case 2: return 3
    case 3: return 1
    default: return 0
  }
})

// 支付倒计时
const paymentCountdown = computed(() => {
  const minutes = Math.floor(paymentTimeLeft.value / 60)
  const seconds = paymentTimeLeft.value % 60
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
})

// 获取支付方式名称
const getPaymentMethodName = (type?: number) => {
  if (!type) return '未支付'
  
  const methods: Record<number, string> = {
    1: '支付宝',
    2: '微信支付',
    3: '银行卡'
  }
  
  return methods[type] || '其他'
}

// 加载订单详情
const loadOrderDetail = async () => {
  const orderId = Number(route.params.orderId)
  if (!orderId || isNaN(orderId)) {
    loading.value = false
    return
  }
  
  loading.value = true
  try {
    order.value = await getOrderDetail(orderId)
    
    // 计算支付剩余时间
    if (order.value.status === 1) {
      const createTime = new Date(order.value.createTime).getTime()
      const now = Date.now()
      const expireTime = createTime + 30 * 60 * 1000 // 假设订单有效期为30分钟
      
      if (now < expireTime) {
        paymentTimeLeft.value = Math.floor((expireTime - now) / 1000)
        startCountdown()
      } else {
        paymentTimeLeft.value = 0
      }
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 支付倒计时
const startCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
  
  countdownTimer.value = window.setInterval(() => {
    paymentTimeLeft.value--
    
    if (paymentTimeLeft.value <= 0) {
      clearInterval(countdownTimer.value as number)
      ElMessage.warning('订单已超时')
      loadOrderDetail() // 重新加载订单状态
    }
  }, 1000) as unknown as number
}

// 取消订单
const handleCancelOrder = async () => {
  if (!order.value) return
  
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    canceling.value = true
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    loadOrderDetail() // 重新加载订单状态
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  } finally {
    canceling.value = false
  }
}

// 快速支付（模拟支付）
const handleQuickPay = async () => {
  if (!order.value) return
  
  try {
    await ElMessageBox.confirm(
      '确认使用模拟支付功能？（仅用于测试）',
      '模拟支付',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    quickPaying.value = true
    
    // 1. 创建支付记录
    const payment = await createPayment({
      orderNo: order.value.orderNo,
      paymentType: 3 // 模拟支付
    })
    
    console.log('支付记录创建成功:', payment)
    
    // 2. 立即模拟支付成功
    await simulatePayment(payment.paymentNo)
    
    ElMessage.success('支付成功！课程已开通')
    
    // 3. 重新加载订单详情
    await loadOrderDetail()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error(error.message || '支付失败')
    }
  } finally {
    quickPaying.value = false
  }
}

// 跳转函数
const goToPay = () => {
  if (!order.value) return
  router.push(`/order/pay/${order.value.orderNo}`)
}

const goToCourse = () => {
  if (!order.value || !order.value.orderItems || order.value.orderItems.length === 0) return
  
  const firstCourse = order.value.orderItems[0]
  router.push(`/course/${firstCourse.courseId}/play`)
}

const goToCourseList = () => {
  router.push('/course/list')
}

const goToOrderList = () => {
  router.push('/order/list')
}

// 页面加载时获取订单详情
onMounted(() => {
  loadOrderDetail()
})

// 清理定时器
onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})
</script>

<style scoped lang="scss">
.order-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-content {
  padding: 30px 0 60px;
  
  .container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 15px;
  }
}

.page-header {
  margin-bottom: 20px;
  
  .el-breadcrumb {
    margin-bottom: 15px;
  }
  
  .page-title {
    font-size: 24px;
    font-weight: 500;
    color: #333;
    margin: 0;
  }
}

// 卡片通用样式
.order-status-card,
.order-progress-card,
.order-info-card,
.order-items-card,
.order-price-card,
.payment-records-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  margin: 0 0 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

// 订单状态卡片
.order-status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .status-section {
    display: flex;
    align-items: center;
    gap: 15px;
    
    .status-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      
      &.status-pending {
        background: #e6a23c;
      }
      
      &.status-success {
        background: #67c23a;
      }
      
      &.status-canceled {
        background: #909399;
      }
    }
    
    .status-info {
      h3 {
        font-size: 20px;
        margin: 0 0 5px;
      }
      
      p {
        color: #606266;
        margin: 0;
      }
      
      .countdown {
        color: #f56c6c;
        font-weight: bold;
      }
    }
  }
  
  .status-actions {
    display: flex;
    gap: 10px;
  }
}

// 订单进度
.order-steps {
  padding: 20px 0;
}

// 订单商品
.order-items {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  
  .item-header {
    display: flex;
    background: #f5f7fa;
    padding: 15px;
    border-bottom: 1px solid #ebeef5;
    font-weight: bold;
    
    .item-course {
      flex: 1;
    }
    
    .item-price {
      width: 120px;
      text-align: right;
    }
  }
  
  .item-row {
    display: flex;
    padding: 15px;
    border-bottom: 1px solid #ebeef5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .item-course {
      flex: 1;
      display: flex;
      gap: 15px;
      
      .course-image {
        width: 120px;
        height: 80px;
        overflow: hidden;
        border-radius: 4px;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      
      .course-info {
        flex: 1;
        
        h4 {
          margin: 0 0 10px;
          font-size: 16px;
        }
      }
    }
    
    .item-price {
      width: 120px;
      text-align: right;
      font-size: 16px;
      font-weight: 500;
    }
  }
}

// 价格信息
.price-details {
  .price-item {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;
    
    &.total {
      font-size: 18px;
      font-weight: 500;
      
      .total-price {
        color: #f56c6c;
      }
    }
  }
  
  .price-divider {
    height: 1px;
    background: #ebeef5;
    margin: 10px 0;
  }
}

// 响应式调整
@media (max-width: 768px) {
  .order-status-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .order-items {
    .item-row {
      .item-course {
        flex-direction: column;
        
        .course-image {
          width: 100%;
          height: auto;
        }
      }
    }
  }
}
</style>