<template>
  <div class="order-detail-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">订单详情</h2>
        <div class="order-detail">
          <el-empty description="订单详情加载中..." />
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
</script>

<style scoped lang="scss">
.order-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-title {
  font-size: 24px;
  padding: 20px 0;
}

.order-detail {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  min-height: 400px;
}
</style>