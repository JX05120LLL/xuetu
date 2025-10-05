<template>
  <div class="order-pay-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <div class="pay-container">
          <h2>订单支付</h2>
          <div class="order-info">
            <p>订单号: {{ orderNo }}</p>
            <p class="amount">应付金额: <span>¥ 0.00</span></p>
          </div>

          <div class="payment-methods">
            <h3>选择支付方式</h3>
            <el-radio-group v-model="paymentType">
              <el-radio :label="1">支付宝</el-radio>
              <el-radio :label="2">微信支付</el-radio>
              <el-radio :label="3">模拟支付（测试）</el-radio>
            </el-radio-group>
          </div>

          <el-button type="primary" size="large" @click="handlePay">
            确认支付
          </el-button>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'

const route = useRoute()
const orderNo = ref(route.params.orderNo as string)
const paymentType = ref(3)

const handlePay = () => {
  console.log('支付功能开发中...')
}
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