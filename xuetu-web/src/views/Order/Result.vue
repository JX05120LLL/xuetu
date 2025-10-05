<template>
  <div class="order-result-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <div class="result-container">
          <el-result
            icon="success"
            title="支付成功"
            sub-title="恭喜您，订单支付成功！"
          >
            <template #extra>
              <el-button type="primary" @click="router.push('/user/courses')">
                去学习
              </el-button>
              <el-button @click="router.push('/user/orders')">
                查看订单
              </el-button>
            </template>
          </el-result>
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'

const router = useRouter()
</script>

<style scoped lang="scss">
.order-result-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.result-container {
  background: #fff;
  padding: 60px 40px;
  border-radius: 8px;
  margin: 40px auto;
  max-width: 600px;
}
</style>