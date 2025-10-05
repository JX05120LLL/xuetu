<template>
  <div class="user-settings-page">
    <Header />
    
    <div class="page-content">
      <div class="container">
        <h2 class="page-title">个人设置</h2>
        <div class="user-settings">
          <el-empty description="个人设置开发中..." />
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
.user-settings-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-title {
  font-size: 24px;
  padding: 20px 0;
}

.user-settings {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  min-height: 400px;
}
</style>