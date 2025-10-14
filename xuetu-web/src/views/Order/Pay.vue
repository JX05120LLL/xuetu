<template>
  <div class="order-pay-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <div class="pay-container" v-loading="loading">
          <h2>订单支付</h2>
          <div class="order-info" v-if="order">
            <p>订单号: {{ order.orderNo }}</p>
            <p class="amount">应付金额: <span>¥ {{ order.actualAmount.toFixed(2) }}</span></p>
          </div>

          <div class="payment-methods">
            <h3>选择支付方式</h3>
            <el-radio-group v-model="paymentType">
              <el-radio :label="1">支付宝</el-radio>
              <el-radio :label="2">微信支付</el-radio>
              <el-radio :label="3">模拟支付（测试）</el-radio>
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
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { getOrderByOrderNo } from '@/api/order'
import { createPayment, simulatePayment } from '@/api/order'
import type { Order } from '@/types/order'

const route = useRoute()
const router = useRouter()

const orderNo = ref(route.params.orderNo as string)
const paymentType = ref(3)
const loading = ref(false)
const paying = ref(false)
const order = ref<Order | null>(null)

// 加载订单信息
const loadOrder = async () => {
  loading.value = true
  try {
    order.value = await getOrderByOrderNo(orderNo.value)
    
    // 检查订单状态
    if (order.value.status !== 0) {
      ElMessage.warning('该订单已支付或已取消')
      router.push('/order/list')
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
    router.push('/order/list')
  } finally {
    loading.value = false
  }
}

// 处理支付
const handlePay = async () => {
  if (!order.value) return
  
  paying.value = true
  try {
    // 1. 创建支付记录
    const payment = await createPayment({
      orderNo: order.value.orderNo,
      paymentType: paymentType.value
    })
    
    console.log('支付记录创建成功:', payment)
    
    // 2. 如果是模拟支付，直接调用模拟支付接口
    if (paymentType.value === 3) {
      await simulatePayment(payment.paymentNo)
      ElMessage.success('支付成功！')
      
      // 跳转到我的课程页面
      setTimeout(() => {
        router.push('/user/courses')
      }, 1500)
    } else {
      // 真实支付方式（支付宝、微信）
      ElMessage.info('真实支付功能待接入')
      // TODO: 跳转到支付页面或二维码页面
    }
  } catch (error: any) {
    console.error('支付失败:', error)
    ElMessage.error(error.message || '支付失败')
  } finally {
    paying.value = false
  }
}

onMounted(() => {
  if (orderNo.value) {
    loadOrder()
  } else {
    ElMessage.warning('订单号无效')
    router.push('/order/list')
  }
})
</script>

<style scoped lang="scss">
.order-pay-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.pay-container {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  max-width: 600px;
  margin: 40px auto;

  h2 {
    text-align: center;
    margin-bottom: 30px;
  }

  .order-info {
    text-align: center;
    margin-bottom: 30px;

    .amount {
      font-size: 20px;
      margin-top: 10px;

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
    }
  }

  .el-button {
    width: 100%;
  }
}
</style>