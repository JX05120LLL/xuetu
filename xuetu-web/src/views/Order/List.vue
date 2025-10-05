<template>
  <div class="order-list-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">我的订单</h2>

        <el-tabs v-model="activeStatus">
          <el-tab-pane label="全部订单" :name="undefined" />
          <el-tab-pane label="待支付" :name="0" />
          <el-tab-pane label="已完成" :name="4" />
        </el-tabs>

        <div class="order-list">
          <el-empty description="暂无订单" />
        </div>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'

const activeStatus = ref<number | undefined>(undefined)
</script>

<style scoped lang="scss">
.order-list-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-title {
  font-size: 24px;
  padding: 20px 0;
}

.order-list {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  min-height: 400px;
}
</style>