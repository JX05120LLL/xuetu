<template>
  <div class="order-pay-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <div class="pay-container" v-loading="loading">
          <!-- 支付选择界面 -->
          <div v-if="!paymentNo">
            <h2>订单支付</h2>
            <div class="order-info" v-if="order">
              <p>订单号: {{ order.orderNo }}</p>
              <p class="amount">应付金额: <span>¥ {{ order.actualAmount.toFixed(2) }}</span></p>
            </div>

            <div class="payment-methods">
              <h3>选择支付方式</h3>
              <el-radio-group v-model="paymentType">
                <el-radio :label="1">模拟支付（测试）</el-radio>
                <el-radio :label="2" disabled>支付宝（开发中）</el-radio>
                <el-radio :label="3" disabled>微信支付（开发中）</el-radio>
              </el-radio-group>
            </div>

            <el-button 
              type="primary" 
              size="large" 
              :loading="paying"
              @click="handlePay"
            >
              确认支付
            </el-button>
          </div>

          <!-- 支付中界面（二维码/等待界面） -->
          <div v-else class="paying-status">
            <div class="qrcode-container" v-if="showQRCode">
              <h2>请扫码支付</h2>
              <div class="qrcode-wrapper">
                <!-- 这里可以集成实际的二维码库，现在显示模拟 -->
                <div class="qrcode-placeholder">
                  <el-icon :size="100"><QrCode /></el-icon>
                  <p>二维码展示区域</p>
                </div>
              </div>
              <p class="qrcode-tip">请使用{{ paymentTypeName }}扫码完成支付</p>
            </div>

            <div class="waiting-payment" v-else>
              <el-icon class="spinning" :size="60"><Loading /></el-icon>
              <h3>支付处理中...</h3>
              <p>支付单号: {{ paymentNo }}</p>
            </div>

            <div class="payment-info">
              <p>应付金额: <span class="amount-highlight">¥{{ order?.actualAmount.toFixed(2) }}</span></p>
              <p class="countdown">剩余时间: {{ formatCountdown(countdown) }}</p>
            </div>

            <div class="payment-actions">
              <el-button @click="handleCancelPay">取消支付</el-button>
              <el-button type="primary" @click="handleCheckStatus">
                我已支付，立即查询
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, QrCode } from '@element-plus/icons-vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { getOrderByOrderNo } from '@/api/order'
import { createPayment, simulatePayment, getPaymentStatus } from '@/api/order'
import type { Order } from '@/types/order'

const route = useRoute()
const router = useRouter()

const orderNo = ref(route.params.orderNo as string)
const paymentType = ref(1) // 默认模拟支付
const loading = ref(false)
const paying = ref(false)
const order = ref<Order | null>(null)
const paymentNo = ref('')
const showQRCode = ref(false)

// 支付状态轮询
let pollingTimer: ReturnType<typeof setInterval> | null = null
const POLL_INTERVAL = 3000 // 3秒轮询一次
const PAYMENT_TIMEOUT = 30 * 60 * 1000 // 30分钟超时

// 倒计时
const countdown = ref(30 * 60) // 30分钟倒计时（秒）
let countdownTimer: ReturnType<typeof setInterval> | null = null

// 支付方式名称
const paymentTypeName = computed(() => {
  const names: Record<number, string> = {
    1: '模拟支付',
    2: '支付宝',
    3: '微信支付'
  }
  return names[paymentType.value] || '未知'
})

/**
 * 格式化倒计时
 */
const formatCountdown = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
}

/**
 * 加载订单信息
 */
const loadOrder = async () => {
  loading.value = true
  try {
    order.value = await getOrderByOrderNo(orderNo.value)
    
    // 检查订单状态
    if (order.value.status !== 0) {
      ElMessage.warning('该订单已支付或已取消')
      router.push('/user/orders')
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
    router.push('/user/orders')
  } finally {
    loading.value = false
  }
}

/**
 * 处理支付
 */
const handlePay = async () => {
  if (!order.value) return
  
  paying.value = true
  try {
    // 1. 创建支付记录
    const payment = await createPayment({
      orderNo: order.value.orderNo,
      paymentType: paymentType.value,
      remark: '用户支付订单'
    })
    
    console.log('支付记录创建成功:', payment)
    paymentNo.value = payment.paymentNo
    
    // 2. 根据支付方式处理
    if (paymentType.value === 1) {
      // 模拟支付 - 直接模拟支付成功
      await simulatePayment(payment.paymentNo)
      ElMessage.success('支付成功！')
      
      // 跳转到支付结果页
      setTimeout(() => {
        router.push(`/order/result/${order.value!.orderNo}`)
      }, 1500)
    } else {
      // 真实支付方式 - 显示二维码并开始轮询
      showQRCode.value = true
      startPolling()
      startCountdown()
    }
  } catch (error: any) {
    console.error('支付失败:', error)
    ElMessage.error(error.message || '支付失败')
    paymentNo.value = ''
  } finally {
    paying.value = false
  }
}

/**
 * 开始轮询支付状态
 */
const startPolling = () => {
  pollingTimer = setInterval(async () => {
    try {
      const status = await getPaymentStatus(paymentNo.value)
      
      // 支付成功 (status: 1)
      if (status === 1) {
        stopPolling()
        stopCountdown()
        ElMessage.success('支付成功！')
        
        setTimeout(() => {
          router.push(`/order/result/${order.value!.orderNo}`)
        }, 1500)
      }
      // 支付失败 (status: 2)
      else if (status === 2) {
        stopPolling()
        stopCountdown()
        ElMessage.error('支付失败，请重新支付')
        paymentNo.value = ''
        showQRCode.value = false
      }
    } catch (error) {
      console.error('查询支付状态失败:', error)
    }
  }, POLL_INTERVAL)

  // 设置超时
  setTimeout(() => {
    if (pollingTimer) {
      stopPolling()
      stopCountdown()
      handlePaymentTimeout()
    }
  }, PAYMENT_TIMEOUT)
}

/**
 * 停止轮询
 */
const stopPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

/**
 * 开始倒计时
 */
const startCountdown = () => {
  countdown.value = 30 * 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      stopCountdown()
      handlePaymentTimeout()
    }
  }, 1000)
}

/**
 * 停止倒计时
 */
const stopCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

/**
 * 处理支付超时
 */
const handlePaymentTimeout = () => {
  ElMessageBox.alert(
    '支付已超时，订单将自动取消',
    '提示',
    {
      confirmButtonText: '确定',
      callback: () => {
        router.push('/user/orders')
      }
    }
  )
}

/**
 * 取消支付
 */
const handleCancelPay = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要取消支付吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '继续支付',
        type: 'warning'
      }
    )

    stopPolling()
    stopCountdown()
    router.push('/user/orders')
  } catch {
    // 用户点击了"继续支付"，不做任何操作
  }
}

/**
 * 手动检查支付状态
 */
const handleCheckStatus = async () => {
  try {
    const status = await getPaymentStatus(paymentNo.value)
    
    if (status === 1) {
      stopPolling()
      stopCountdown()
      ElMessage.success('支付成功！')
      
      setTimeout(() => {
        router.push(`/order/result/${order.value!.orderNo}`)
      }, 1500)
    } else if (status === 0) {
      ElMessage.warning('支付尚未完成，请完成支付')
    } else if (status === 2) {
      stopPolling()
      stopCountdown()
      ElMessage.error('支付失败，请重新支付')
      paymentNo.value = ''
      showQRCode.value = false
    }
  } catch (error) {
    ElMessage.error('查询支付状态失败')
  }
}

// 组件挂载
onMounted(() => {
  if (orderNo.value) {
    loadOrder()
  } else {
    ElMessage.warning('订单号无效')
    router.push('/user/orders')
  }
})

// 组件卸载时清理定时器
onUnmounted(() => {
  stopPolling()
  stopCountdown()
})
</script>

<style scoped lang="scss">
.order-pay-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-content {
  padding: 40px 0;
}

.pay-container {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  max-width: 600px;
  margin: 0 auto;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  h2 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 24px;
    color: #303133;
  }

  .order-info {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;

    p {
      margin: 8px 0;
      color: #606266;
    }

    .amount {
      font-size: 18px;
      margin-top: 12px;

      span {
        font-size: 32px;
        color: #f56c6c;
        font-weight: bold;
      }
    }
  }

  .payment-methods {
    margin-bottom: 30px;

    h3 {
      margin-bottom: 15px;
      font-size: 16px;
      color: #303133;
    }

    .el-radio-group {
      display: flex;
      flex-direction: column;
      gap: 12px;

      :deep(.el-radio) {
        padding: 12px;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        margin: 0;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
          background: #f0f9ff;
        }

        &.is-checked {
          border-color: #409eff;
          background: #f0f9ff;
        }
      }
    }
  }

  .el-button {
    width: 100%;
  }

  // 支付中状态
  .paying-status {
    text-align: center;

    .qrcode-container {
      margin-bottom: 30px;

      h2 {
        margin-bottom: 20px;
      }

      .qrcode-wrapper {
        display: flex;
        justify-content: center;
        margin-bottom: 20px;

        .qrcode-placeholder {
          width: 200px;
          height: 200px;
          border: 2px dashed #dcdfe6;
          border-radius: 8px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          background: #f5f7fa;

          p {
            margin-top: 10px;
            color: #909399;
            font-size: 14px;
          }
        }
      }

      .qrcode-tip {
        color: #606266;
        font-size: 14px;
      }
    }

    .waiting-payment {
      margin-bottom: 30px;

      .spinning {
        animation: spin 1s linear infinite;
        color: #409eff;
        margin-bottom: 20px;
      }

      h3 {
        margin: 16px 0;
        color: #303133;
      }

      p {
        color: #909399;
        font-size: 14px;
      }
    }

    .payment-info {
      padding: 20px;
      background: #f5f7fa;
      border-radius: 8px;
      margin-bottom: 20px;

      p {
        margin: 8px 0;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .amount-highlight {
          font-size: 24px;
          color: #f56c6c;
          font-weight: bold;
        }
      }

      .countdown {
        color: #f56c6c;
        font-weight: 500;
      }
    }

    .payment-actions {
      display: flex;
      gap: 12px;

      .el-button {
        flex: 1;
      }
    }
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .pay-container {
    padding: 20px;
    margin: 0 12px;

    h2 {
      font-size: 20px;
    }

    .order-info .amount span {
      font-size: 28px;
    }

    .paying-status {
      .qrcode-wrapper .qrcode-placeholder {
        width: 160px;
        height: 160px;
      }

      .payment-info .amount-highlight {
        font-size: 20px;
      }

      .payment-actions {
        flex-direction: column;
      }
    }
  }
}
</style>